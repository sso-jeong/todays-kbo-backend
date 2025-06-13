import logging
import time
import random
import re
import json
import csv
import os
from datetime import datetime
import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from kafka import KafkaProducer
from multiprocessing import Pool

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
    return bool(re.fullmatch(r'[\uac00-\ud7a3A-Z]{2,10}', name))

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
        return game_date.strip(), stadium.strip("()")
    except:
        return None, None

def extract_result_players(driver):
    try:
        win = driver.find_element(By.CSS_SELECTOR, ".game_result .win a").text.strip()
        lose = driver.find_element(By.CSS_SELECTOR, ".game_result .lose a").text.strip()
        save_elem = driver.find_elements(By.CSS_SELECTOR, ".game_result .save a")
        save = save_elem[0].text.strip() if save_elem else None
        return win, lose, save
    except:
        return None, None, None

def determine_status(rows):
    text = " ".join(row.text for row in rows)
    if "콜드" in text:
        return "콜드게임"
    elif "지연" in text or "딜레이" in text:
        return "딜레이"
    elif "종료" in text or "승" in text:
        return "경기종료"
    else:
        return "진행중"

def crawl_game_inning_score(s_no, year):
    url = f"https://statiz.sporki.com/schedule/?m=summary&s_no={s_no}"
    logging.info(f"🗕️ 크롤링 시작: s_no={s_no}")

    options = uc.ChromeOptions()
    options.add_argument('--headless')
    options.add_argument('--disable-blink-features=AutomationControlled')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    driver = uc.Chrome(options=options)

    try:
        driver.get(url)
        time.sleep(1.5)
        rows = driver.find_elements(By.CSS_SELECTOR, ".table_type03 tbody tr")
        game_date, stadium = extract_game_meta(driver)

        weekday_str = None
        if game_date:
            game_dt = datetime.strptime(game_date, "%Y-%m-%d").date()
            weekday_str = ["월", "화", "수", "목", "금", "토", "일"][game_dt.weekday()]

        if not rows and game_date:
            if game_dt < datetime.today().date():
                send_to_kafka("game-result-meta", {
                    "gameId": s_no,
                    "status": "경기취소",
                    "gameDate": game_date,
                    "weekday": weekday_str,
                    "stadium": stadium
                })
                return False
            else:
                send_to_kafka("game-result-meta", {
                    "gameId": s_no,
                    "status": "경기예정",
                    "gameDate": game_date,
                    "weekday": weekday_str,
                    "stadium": stadium
                })
                return True

        if len(rows) < 2:
            logging.warning(f"❌ 팀 데이터 부족: {s_no}")
            return True

        valid_teams, team_scores, team_totals = [], {}, {}
        for row in rows:
            name, scores, totals = extract_team_data(row)
            if name:
                valid_teams.append(name)
                team_scores[name] = scores
                team_totals[name] = totals

        if len(valid_teams) != 2 or any(t == "정규" for t in valid_teams):
            logging.warning(f"❌ 유효 팀 부족 또는 비정상 팀명: s_no={s_no}, teams={valid_teams}")
            if game_date:
                send_to_kafka("game-result-meta", {
                    "gameId": s_no,
                    "status": "경기취소",
                    "gameDate": game_date,
                    "weekday": weekday_str,
                    "stadium": stadium
                })
            return True

        away, home = valid_teams
        winner, loser, save = extract_result_players(driver)
        game_id = s_no
        away_score = int(team_totals[away]["R"]) if team_totals[away]["R"].isdigit() else 0
        home_score = int(team_totals[home]["R"]) if team_totals[home]["R"].isdigit() else 0

        for team in [away, home]:
            for inning, run in enumerate(team_scores[team], start=1):
                send_to_kafka("game-inning-scores", {
                    "gameId": game_id,
                    "teamName": team,
                    "inning": inning,
                    "runs": int(run),
                    "awayTeamName": away,
                    "homeTeamName": home
                })

        for team in [away, home]:
            for key in ['R', 'H', 'E', 'B']:
                send_to_kafka("game-total-stats", {
                    "gameId": game_id,
                    "teamName": team,
                    "awayTeamName": away,
                    "homeTeamName": home,
                    "type": key,
                    "value": int(team_totals[team][key]) if team_totals[team][key].isdigit() else 0
                })

        game_status = determine_status(rows)
        send_to_kafka("game-result-meta", {
            "gameId": game_id,
            "gameDate": game_date,
            "weekday": weekday_str,
            "stadium": stadium,
            "awayTeamName": away,
            "homeTeamName": home,
            "awayScore": away_score,
            "homeScore": home_score,
            "winner": winner,
            "loser": loser,
            "save": save,
            "status": game_status
        })

        with open(f"success_log_{year}.csv", "a", newline='') as csvfile:
            csv.writer(csvfile).writerow([game_id, datetime.now().isoformat()])

        return True
    except Exception as e:
        logging.error(f"❌ 예외 발생: s_no={s_no}, error={e}")
        return True
    finally:
        driver.quit()

def load_checkpoint(year):
    path = f"checkpoint_{year}.txt"
    return int(open(path).read()) if os.path.exists(path) else 1

def save_checkpoint(year, i):
    with open(f"checkpoint_{year}.txt", "w") as f:
        f.write(str(i))

def batch_crawl_until_end(year):
    i = load_checkpoint(year)
    while True:
        s_no = int(f"{year}{str(i).zfill(4)}")
        keep_going = crawl_game_inning_score(s_no, year)
        if not keep_going:
            logging.info(f"✅ 크롤링 종료: s_no={s_no}")
            break
        save_checkpoint(year, i)
        time.sleep(random.uniform(2, 5))
        i += 1

def crawl_for_year(year):
    batch_crawl_until_end(year)

#if __name__ == "__main__":
#    years = list(range(2024, 2025))
#    with Pool(processes=3) as pool:
#        pool.map(crawl_for_year, years)
if __name__ == "__main__":
    years = [2025]  # ✅ 여기만 이렇게 수정하면 됨
    with Pool(processes=3) as pool:
        pool.map(crawl_for_year, years)