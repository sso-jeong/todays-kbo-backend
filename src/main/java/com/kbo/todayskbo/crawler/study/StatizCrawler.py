from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time
from datetime import datetime

def get_weekday(y, m, d):
    try:
        return ["월", "화", "수", "목", "금", "토", "일"][datetime(y, m, d).weekday()]
    except:
        return "-"

def crawl_schedule(year: int, month: int):
    # 1. URL 구성
    url = f"https://statiz.sporki.com/schedule/?year={year}&month={month}"

    # 2. 드라이버 시작 및 페이지 이동
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
    driver.get(url)
    time.sleep(3)

    print(f"📅 크롤링 시작: {year}년 {month}월")

    # 3. 각 날의 td를 돌며 정보 추출
    tds = driver.find_elements(By.CSS_SELECTOR, "td")

    for td in tds:
        try:
            day_elem = td.find_element(By.CLASS_NAME, "day")
            day = int(day_elem.text.strip())
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

                # 우천취소
                if weather and len(teams) == 2:
                    team1, team2 = teams
                    print(f"{year}-{month:02d}-{day:02d} ({weekday}) | {team1} vs {team2} - {weather}")

                # 경기 결과
                elif len(score_spans) >= 2 and len(teams) == 2:
                    score1 = score_spans[0].text.strip()
                    score2 = score_spans[1].text.strip()
                    team1, team2 = teams
                    print(f"{year}-{month:02d}-{day:02d} ({weekday}) | {team1} {score1} vs {score2} {team2}")

                # 예정 경기
                elif stadium and len(teams) == 2:
                    team1, team2 = teams
                    print(f"{year}-{month:02d}-{day:02d} ({weekday}) | {team1} vs {team2} - 경기 예정 ({stadium})")

        except:
            continue  # 날짜 없는 td 건너뜀

    driver.quit()

# === 실행 ===
if __name__ == "__main__":
    # 프론트에서 넘겨주는 값
    year = 2025
    month = 5
    crawl_schedule(year, month)
