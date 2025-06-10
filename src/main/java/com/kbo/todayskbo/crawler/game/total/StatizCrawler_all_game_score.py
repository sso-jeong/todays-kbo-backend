from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from datetime import datetime
import time
import json
from kafka import KafkaProducer
import logging

# 🛠 로그 설정
logging.basicConfig(
    filename="crawl_errors.log",
    level=logging.ERROR,
    format="%(asctime)s [%(levelname)s] %(message)s"
)

# 🛰 Kafka Producer 설정
producer = KafkaProducer(
    bootstrap_servers=['localhost:9094'],  # Kafka 도커 포트
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
)

def send_game_to_kafka(game_data):
    print(f"📤 Kafka 전송: {game_data}")
    producer.send("games", value=game_data)

def get_weekday(y, m, d):
    try:
        return ["월", "화", "수", "목", "금", "토", "일"][datetime(y, m, d).weekday()]
    except Exception as e:
        logging.error(f"요일 계산 실패 - {y}-{m}-{d}: {e}")
        return "-"

def crawl_schedule_by_month(year: int, month: int):
    try:
        url = f"https://statiz.sporki.com/schedule/?year={year}&month={month}"
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        driver.get(url)
        time.sleep(3)

        tds = driver.find_elements(By.CSS_SELECTOR, "td")
        for td in tds:
            try:
                day_elem = td.find_element(By.CLASS_NAME, "day")
                day = int(day_elem.text.strip())
                date_str = f"{year}-{month:02d}-{day:02d}"
                weekday = get_weekday(year, month, day)

                games = td.find_elements(By.CSS_SELECTOR, ".games ul li")
                for game in games:
                    spans = game.find_elements(By.TAG_NAME, "span")

                    team_spans = [s for s in spans if "team" in s.get_attribute("class")]
                    weather_spans = [s for s in spans if "weather" in s.get_attribute("class")]
                    score_spans = [s for s in spans if "score" in s.get_attribute("class")]
                    stadium_spans = [s for s in spans if "stadium" in s.get_attribute("class")]

                    teams = [s.text.strip() for s in team_spans]
                    weather = weather_spans[0].text.strip() if weather_spans else None
                    stadium = stadium_spans[0].text.strip() if stadium_spans else None

                    if weather and "우천취소" in weather and len(teams) == 2:
                        game_data = {
                            "gameDate": date_str,
                            "weekday": weekday,
                            "awayTeamName": teams[0],
                            "homeTeamName": teams[1],
                            "awayScore": None,
                            "homeScore": None,
                            "status": "우천취소",
                            "stadium": stadium or "",
                            "inningStatus": None,
                            "highlightUrl": None
                        }
                        send_game_to_kafka(game_data)

                    elif len(score_spans) >= 2 and len(teams) == 2:
                        game_data = {
                            "gameDate": date_str,
                            "weekday": weekday,
                            "awayTeamName": teams[0],
                            "homeTeamName": teams[1],
                            "awayScore": int(score_spans[0].text.strip()),
                            "homeScore": int(score_spans[1].text.strip()),
                            "status": "경기종료",
                            "stadium": stadium or "",
                            "inningStatus": None,
                            "highlightUrl": None
                        }
                        send_game_to_kafka(game_data)

                    elif stadium and len(teams) == 2:
                        game_data = {
                            "gameDate": date_str,
                            "weekday": weekday,
                            "awayTeamName": teams[0],
                            "homeTeamName": teams[1],
                            "awayScore": None,
                            "homeScore": None,
                            "status": "경기예정",
                            "stadium": stadium,
                            "inningStatus": None,
                            "highlightUrl": None
                        }
                        send_game_to_kafka(game_data)

            except Exception as e:
                logging.error(f"[{year}-{month}] 하루 처리 중 오류: {e}")
                continue

        driver.quit()
    except Exception as e:
        logging.error(f"[{year}-{month}] 월 전체 크롤링 실패: {e}")

def crawl_all_dates(start_year: int, end_year: int, end_month: int):
    for year in range(start_year, end_year + 1):
        for month in range(1, 13):
            if year == end_year and month > end_month:
                break
            print(f"🌍 {year}년 {month}월 크롤링 시작")
            crawl_schedule_by_month(year, month)

# 실행
if __name__ == "__main__":
    crawl_all_dates(start_year=1982, end_year=2025, end_month=6)