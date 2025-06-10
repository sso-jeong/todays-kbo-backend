import logging
import time
import re
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from kafka import KafkaProducer
import json

# 로그 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    handlers=[logging.StreamHandler()]
)

# Kafka 설정
producer = KafkaProducer(
    bootstrap_servers=['localhost:9094'],
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
)

def send_to_kafka(topic, data):
    logging.info(f"📤 Kafka 전송 완료 [{topic}]: {data}")
    producer.send(topic, value=data)

def is_valid_team_name(name):
    return bool(re.fullmatch(r'[가-힣A-Z]{2,10}', name))

def extract_team_data(row):
    try:
        cols = row.find_elements(By.TAG_NAME, "td")
        name = cols[0].text.strip()

        if not is_valid_team_name(name):
            return None, [], {}

        inning_scores = []
        for td in cols[1:-4]:
            try:
                score_div = td.find_element(By.CLASS_NAME, "score")
                score_text = score_div.text.strip().split('\n')[0]
                inning_scores.append(score_text if score_text.isdigit() else '0')
            except:
                inning_scores.append('0')

        totals = {
            "R": cols[-4].text.strip(),
            "H": cols[-3].text.strip(),
            "E": cols[-2].text.strip(),
            "B": cols[-1].text.strip()
        }

        return name, inning_scores, totals
    except Exception as e:
        logging.warning(f"❌ 팀 데이터 추출 실패: {e}")
        return None, [], {}

def crawl_game_inning_score(s_no):
    url = f"https://statiz.sporki.com/schedule/?m=summary&s_no={s_no}"
    logging.info(f"📅 크롤링 시작: s_no={s_no}")
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

    try:
        driver.get(url)
        time.sleep(2)

        rows = driver.find_elements(By.CSS_SELECTOR, ".table_type03 tbody tr")
        if len(rows) < 2:
            logging.warning(f"❌ 팀 데이터 부족: {s_no}")
            return

        valid_teams = []
        team_scores = {}
        team_totals = {}

        for row in rows:
            team_name, scores, totals = extract_team_data(row)
            if team_name:
                valid_teams.append(team_name)
                team_scores[team_name] = scores
                team_totals[team_name] = totals

        if len(valid_teams) != 2:
            logging.warning(f"❌ 유효 팀 부족: s_no={s_no}, teams={valid_teams}")
            return

        away_team, home_team = valid_teams
        game_id = s_no

        for team in [away_team, home_team]:
            # 이닝별 점수
            for inning, run in enumerate(team_scores[team], start=1):
                data = {
                    "gameId": game_id,
                    "teamName": team,
                    "inning": inning,
                    "runs": int(run),
                    "awayTeamName": away_team,
                    "homeTeamName": home_team
                }
                send_to_kafka("game-inning-scores", data)

            # R, H, E, B 개별 전송
            totals = team_totals[team]
            for key in ['R', 'H', 'E', 'B']:
                data = {
                    "gameId": game_id,
                    "teamName": team,
                    "awayTeamName": away_team,
                    "homeTeamName": home_team,
                    "type": key,
                    "value": int(totals[key]) if totals[key].isdigit() else 0
                }
                send_to_kafka("game-total-stats", data)

    except Exception as e:
        logging.error(f"❌ 예외 발생: s_no={s_no}, error={e}")
    finally:
        driver.quit()

def batch_crawl_inning_scores(start_year, end_year, max_games_per_year=100):
    for year in range(start_year, end_year + 1):
        for i in range(1, max_games_per_year + 1):
            s_no = int(f"{year}{str(i).zfill(4)}")
            crawl_game_inning_score(s_no)

# 실행 예시 (1년 10경기 테스트)
batch_crawl_inning_scores(2025, 2025, 10)

# 전체 시즌 크롤링 예시
# batch_crawl_inning_scores(1982, 2025, 720)
