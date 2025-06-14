import requests
import json
from kafka import KafkaProducer
from datetime import datetime
import time

# Kafka 설정
producer = KafkaProducer(
    bootstrap_servers=['svc.sel4.cloudtype.app:3079'],
    #localhost:9094'],
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
)

def send_to_kafka(topic, data):
    print(f"📤 Kafka 전송 완료 [{topic}] | gameId={data.get('gameId')}")
    producer.send(topic, value=data)
    producer.flush()

def get_weekday(date_str):
    try:
        date_obj = datetime.strptime(date_str, "%Y-%m-%d").date()
        return ["월", "화", "수", "목", "금", "토", "일"][date_obj.weekday()]
    except:
        return None

def safe_int(value):
    try:
        return int(value)
    except:
        return None

def safe_int_list(value):
    if isinstance(value, list):
        return [int(v) if str(v).isdigit() else 0 for v in value]
    return []


def fetch_game_ids(from_date: str, to_date: str) -> list:
    url = "https://api-gw.sports.naver.com/schedule/games"
    params = {
        "fields": "basic,schedule,baseball",
        "upperCategoryId": "kbaseball",
        "categoryId": "kbo",
        "fromDate": from_date,
        "toDate": to_date,
        "roundCodes": "",
        "size": 500
    }
    headers = {"User-Agent": "Mozilla/5.0"}

    try:
        response = requests.get(url, params=params, headers=headers)
        response.raise_for_status()
        games = response.json().get("result", {}).get("games", [])
        return [g["gameId"] for g in games if "gameId" in g]
    except Exception as e:
        print(f"❌ gameId 조회 실패: {e}")
        return []

def fetch_and_send_related_games(game_id: str):
    url = f"https://api-gw.sports.naver.com/schedule/games/{game_id}/relatedGames"
    headers = {"User-Agent": "Mozilla/5.0"}

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        games = response.json().get("result", {}).get("games", [])

        for game in games:
            # 필드 정제
            game["weekday"] = get_weekday(game.get("gameDate"))
            game["homeTeamScore"] = safe_int(game.get("homeTeamScore"))
            game["awayTeamScore"] = safe_int(game.get("awayTeamScore"))

            # ✅ 이닝별 점수가 없는 게임은 스킵
            if not game.get("homeTeamScoreByInning") or not game.get("awayTeamScoreByInning"):
                continue

            game["homeTeamScoreByInning"] = safe_int_list(game.get("homeTeamScoreByInning"))
            game["awayTeamScoreByInning"] = safe_int_list(game.get("awayTeamScoreByInning"))
            game["homeTeamRheb"] = safe_int_list(game.get("homeTeamRheb"))
            game["awayTeamRheb"] = safe_int_list(game.get("awayTeamRheb"))

            send_to_kafka("related-games", game)
            time.sleep(0.2)  # ✅ API 과부하 방지

    except Exception as e:
        print(f"❌ relatedGames 호출 실패: gameId={game_id}, error={e}")

def process_range(from_date: str, to_date: str):
    game_ids = fetch_game_ids(from_date, to_date)
    print(f"📅 {from_date} ~ {to_date} 기간의 gameId 수: {len(game_ids)}")
    for gid in game_ids:
        fetch_and_send_related_games(gid)
        time.sleep(0.5)  # ✅ 연속 호출 방지

# 실행
if __name__ == "__main__":
    # process_range("2025-03-22", "2025-06-12")
    process_range("2025-03-22", "2025-06-13")
