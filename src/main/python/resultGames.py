import requests
import json
from kafka import KafkaProducer
from datetime import datetime
import os

# Kafka 설정
producer = KafkaProducer(
    bootstrap_servers=['svc.sel4.cloudtype.app:31375'],
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
)

# 전송 로그 파일
SENT_LOG_FILE = "sent_game_ids_meta.txt"

# 날짜 설정
FROM_DATE = "2025-03-22"
TO_DATE = "2025-08-31"

# 전송된 gameId 목록 불러오기
def load_sent_ids():
    if not os.path.exists(SENT_LOG_FILE):
        return set()
    with open(SENT_LOG_FILE, "r") as f:
        return set(line.strip() for line in f.readlines())

# 전송된 gameId 기록
def save_sent_id(game_id):
    with open(SENT_LOG_FILE, "a") as f:
        f.write(f"{game_id}\n")

sent_game_ids = load_sent_ids()

def determine_status_label(game: dict) -> str:
    if game.get("cancel"):
        return "취소"
    elif game.get("statusCode") == "RESULT":
        return "종료"
    elif game.get("statusCode") == "BEFORE":
        status_info = game.get("statusInfo", "")
        if status_info and "회" in status_info:
            return "진행중"
        else:
            return "예정"
    return "-"

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

def safe_int_list(value_list):
    if not isinstance(value_list, list):
        return None
    return [safe_int(v) for v in value_list]

def send_to_kafka(topic, data):
    game_id = data.get("gameId")
    if game_id in sent_game_ids:
        print(f"⏩ 중복 건너뜀 [{topic}] | gameId={game_id}")
        return
    try:
        producer.send(topic, value=data)
        producer.flush()
        print(f"📤 Kafka 전송 완료 [{topic}]: {game_id}")
        save_sent_id(game_id)
        sent_game_ids.add(game_id)
    except Exception as e:
        print(f"❌ Kafka 전송 실패: {e}")

def fetch_games(from_date, to_date):
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
    headers = {
        "User-Agent": "Mozilla/5.0"
    }
    try:
        response = requests.get(url, params=params, headers=headers)
        response.raise_for_status()
        return response.json()["result"]["games"]
    except Exception as e:
        print(f"❌ API 호출 실패: {e}")
        return []

# 🔁 전체 실행 로직
if __name__ == "__main__":
    games = fetch_games(FROM_DATE, TO_DATE)

    for game in games:
        game_id = game.get("gameId")
        if not game_id:
            continue

        payload = {
            **game,
            "weekday": get_weekday(game.get("gameDate", "")),
            "statusLabel": determine_status_label(game),
            "homeTeamScore": safe_int(game.get("homeTeamScore")),
            "awayTeamScore": safe_int(game.get("awayTeamScore")),
            "homeTeamScoreByInning": safe_int_list(game.get("homeTeamScoreByInning")),
            "awayTeamScoreByInning": safe_int_list(game.get("awayTeamScoreByInning")),
            "homeTeamRheb": safe_int_list(game.get("homeTeamRheb")),
            "awayTeamRheb": safe_int_list(game.get("awayTeamRheb")),
        }

        print(f"{game_id} | {payload['gameDate']} | {payload['awayTeamName']} {payload['awayTeamScore']} : {payload['homeTeamScore']} {payload['homeTeamName']}")
        send_to_kafka("game-result-meta2", payload)
