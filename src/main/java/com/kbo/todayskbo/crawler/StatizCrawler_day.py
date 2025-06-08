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

def crawl_schedule_by_date(date_str: str):
    # === 1. 날짜 파싱 ===
    dt = datetime.strptime(date_str, "%Y-%m-%d")
    year, month, day = dt.year, dt.month, dt.day
    weekday = get_weekday(year, month, day)

    print(f"📅 크롤링 시작: {date_str} ({weekday})")

    # === 2. URL 접속 ===
    url = f"https://statiz.sporki.com/schedule/?year={year}&month={month}"
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
    driver.get(url)
    time.sleep(3)

    # === 3. 날짜(td) 순회해서 해당 일(day)만 추출 ===
    tds = driver.find_elements(By.CSS_SELECTOR, "td")
    for td in tds:
        try:
            day_elem = td.find_element(By.CLASS_NAME, "day")
            td_day = int(day_elem.text.strip())

            if td_day != day:
                continue  # 다른 날짜는 스킵

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

                # ✅ 우천취소

                if weather and "우천취소" in weather and len(teams) == 2:
                    awayTeam, homeTeam = teams
                    print(f"{date_str} ({weekday}) | {awayTeam} vs {homeTeam} - 우천취소")

                # ✅ 경기 결과
                elif len(score_spans) >= 2 and len(teams) == 2:
                    awayScore = score_spans[0].text.strip()
                    homeScore = score_spans[1].text.strip()
                    awayTeam, homeTeam = teams
                    print(f"{date_str} ({weekday}) | {awayTeam} {awayScore} vs {homeScore} {homeTeam}")

                # ✅ 예정 경기
                elif stadium and len(teams) == 2:
                    awayTeam, homeTeam = teams
                    print(f"{date_str} ({weekday}) |{awayTeam} vs {homeTeam} - 경기 예정 ({stadium})")

        except:
            continue

    driver.quit()

# === 실행 ===
if __name__ == "__main__":
    crawl_schedule_by_date("2025-06-08")
