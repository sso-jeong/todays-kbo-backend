#game-result-meta
#- gameId, gameDate, stadium, awayTeamName, homeTeamName, awayScore, homeScore, winner, loser, save

#game-inning-scores
#- gameId, teamName, inning, runs, awayTeamName, homeTeamName

#game-total-stats
#- gameId, teamName, type (R/H/E/B), value, awayTeamName, homeTeamName

import logging
import time
import re
from datetime import datetime
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

def extract_game_meta(driver):
    try:
        box_head = driver.find_element(By.CSS_SELECTOR, ".box_head").text.strip()
        game_date, stadium = box_head.split()
        game_date = game_date.strip()
        stadium = stadium.strip("()")
        return game_date, stadium
    except:
        return None, None

def extract_result_players(driver):
    try:
        win_elem = driver.find_element(By.CSS_SELECTOR, ".game_result .win a")
        lose_elem = driver.find_element(By.CSS_SELECTOR, ".game_result .lose a")
        save_elem = driver.find_element(By.CSS_SELECTOR, ".game_result .save a")
        return win_elem.text.strip(), lose_elem.text.strip(), save_elem.text.strip()
    except:
        try:
            win_elem = driver.find_element(By.CSS_SELECTOR, ".game_result .win a")
            lose_elem = driver.find_element(By.CSS_SELECTOR, ".game_result .lose a")
            return win_elem.text.strip(), lose_elem.text.strip(), None
        except:
            return None, None, None

def crawl_game_inning_score(s_no):
    url = f"https://statiz.sporki.com/schedule/?m=summary&s_no={s_no}"
    logging.info(f"📅 크롤링 시작: s_no={s_no}")
    options = webdriver.ChromeOptions()
    options.add_argument('--headless')
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

    try:
        driver.get(url)
        time.sleep(1.5)

        rows = driver.find_elements(By.CSS_SELECTOR, ".table_type03 tbody tr")
        game_date, stadium = extract_game_meta(driver)

        # 결과 없음 시 종료 조건 검사
        if not rows and game_date:
            game_dt = datetime.strptime(game_date, "%Y-%m-%d").date()
            if game_dt < datetime.today().date():
                send_to_kafka("game-result-meta", {
                    "gameId": s_no,
                    "status": "경기취소",
                    "gameDate": game_date,
                    "stadium": stadium
                })
                return False  # 종료 조건 만족
            return True  # 오늘/미래면 계속 진행

        if len(rows) < 2:
            logging.warning(f"❌ 팀 데이터 부족: {s_no}")
            return True

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
            return True

        away_team, home_team = valid_teams
        winner, loser, save = extract_result_players(driver)
        game_id = s_no
        away_score = int(team_totals[away_team]["R"]) if team_totals[away_team]["R"].isdigit() else 0
        home_score = int(team_totals[home_team]["R"]) if team_totals[home_team]["R"].isdigit() else 0

        # 이닝별 점수
        for team in [away_team, home_team]:
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

        # R, H, E, B 전송
        for team in [away_team, home_team]:
            for key in ['R', 'H', 'E', 'B']:
                data = {
                    "gameId": game_id,
                    "teamName": team,
                    "awayTeamName": away_team,
                    "homeTeamName": home_team,
                    "type": key,
                    "value": int(team_totals[team][key]) if team_totals[team][key].isdigit() else 0
                }
                send_to_kafka("game-total-stats", data)

        # 메타데이터 전송
        meta_data = {
            "gameId": game_id,
            "gameDate": game_date,
            "stadium": stadium,
            "awayTeamName": away_team,
            "homeTeamName": home_team,
            "awayScore": away_score,
            "homeScore": home_score,
            "winner": winner,
            "loser": loser,
            "save": save
        }
        send_to_kafka("game-result-meta", meta_data)

        return True
    except Exception as e:
        logging.error(f"❌ 예외 발생: s_no={s_no}, error={e}")
        return True
    finally:
        driver.quit()

def batch_crawl_until_end(year):
    i = 1
    while True:
        s_no = int(f"{year}{str(i).zfill(4)}")
        keep_going = crawl_game_inning_score(s_no)
        if keep_going is False:
            logging.info(f"✅ 크롤링 종료 조건 도달: s_no={s_no}")
            break
        i += 1

# 예시 실행
#batch_crawl_until_end(2025)
#전체 연도 반복하고 싶으면:
for y in range(1982, 2025): batch_crawl_until_end(y)
