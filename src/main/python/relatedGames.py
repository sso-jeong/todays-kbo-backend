import requests
import json
import time
from kafka import KafkaProducer
from datetime import datetime

# Kafka 설정
producer = KafkaProducer(
    bootstrap_servers=['svc.sel4.cloudtype.app:31375'],
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
)

# 중복 방지용 전송 기록 파일
SENT_LOG_FILE = "sent_game_ids.txt"

# 전송 완료된 gameId 목록 불러오기
def load_sent_ids():
    try:
        with open(SENT_LOG_FILE, "r") as f:
            return set(line.strip() for line in f.readlines())
    except FileNotFoundError:
        return set()

# 전송 완료된 gameId 저장
def save_sent_id(game_id):
    with open(SENT_LOG_FILE, "a") as f:
        f.write(f"{game_id}\n")

sent_game_ids = load_sent_ids()

def send_to_kafka(topic, data):
    game_id = data.get("gameId")
    if game_id in sent_game_ids:
        print(f"⏩ 중복 건너뜀 [{topic}] | gameId={game_id}")
        return

    try:
        producer.send(topic, value=data)
        producer.flush()
        print(f"📤 Kafka 전송 완료 [{topic}] | gameId={game_id}")
        save_sent_id(game_id)
        sent_game_ids.add(game_id)
    except Exception as e:
        print(f"❌ Kafka 전송 실패: {e}")

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
            game["weekday"] = get_weekday(game.get("gameDate"))
            game["homeTeamScore"] = safe_int(game.get("homeTeamScore"))
            game["awayTeamScore"] = safe_int(game.get("awayTeamScore"))

            if not game.get("homeTeamScoreByInning") or not game.get("awayTeamScoreByInning"):
                continue

            game["homeTeamScoreByInning"] = safe_int_list(game.get("homeTeamScoreByInning"))
            game["awayTeamScoreByInning"] = safe_int_list(game.get("awayTeamScoreByInning"))
            game["homeTeamRheb"] = safe_int_list(game.get("homeTeamRheb"))
            game["awayTeamRheb"] = safe_int_list(game.get("awayTeamRheb"))

            send_to_kafka("related-games", game)
            time.sleep(0.2)

    except Exception as e:
        print(f"❌ relatedGames 호출 실패: gameId={game_id}, error={e}")

def fetch_game_record(game_id: str):
    url = f"https://api-gw.sports.naver.com/schedule/games/{game_id}/record"
    headers = {"User-Agent": "Mozilla/5.0"}

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        result = response.json().get("result", {}).get("recordData", {})

        if not result:
            print(f"⚠️ recordData 없음: {game_id}")
            return

        record_payload = {
            "gameId": game_id,
            "etcRecords": result.get("etcRecords", []),
            "pitchingResult": result.get("pitchingResult", []),
            "teamPitchingBoxscore": result.get("teamPitchingBoxscore", {}),
            "battersBoxscore": result.get("battersBoxscore", {}),
            "scoreBoard": result.get("scoreBoard", {}),
        }

        send_to_kafka("game-records", record_payload)
        time.sleep(0.2)

    except Exception as e:
        print(f"❌ 기록 API 호출 실패: {game_id}, error={e}")

def process_range(from_date: str, to_date: str):
    game_ids = fetch_game_ids(from_date, to_date)
    print(f"📅 {from_date} ~ {to_date} 기간의 gameId 수: {len(game_ids)}")

    for gid in game_ids:
        fetch_and_send_related_games(gid)
        fetch_game_record(gid)
        time.sleep(0.5)

# 실행
if __name__ == "__main__":
    process_range("2025-03-22", "2025-06-16")
