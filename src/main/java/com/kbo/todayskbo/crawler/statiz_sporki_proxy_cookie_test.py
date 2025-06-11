import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
import time

# ✅ 사용할 프록시 설정
PROXY = "http://159.69.57.20:8880"  # 예시 프록시

# ✅ 수동으로 복사한 쿠키 (브라우저에서 확보 후 수정)
cookies = [
    {"name": "cf_clearance", "value": "abcdef123456...", "domain": ".statiz.sporki.com"},
    {"name": "__cf_bm", "value": "xxxxyyyzzz...", "domain": ".statiz.sporki.com"}
]

# ✅ 옵션 설정
options = uc.ChromeOptions()
options.add_argument("--headless")  # 필요시 주석 처리
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")
options.add_argument("--disable-blink-features=AutomationControlled")
options.add_argument(f'--proxy-server={PROXY}')
options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                     "(KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
options.add_argument("--lang=ko-KR")

caps = DesiredCapabilities.CHROME
caps["goog:loggingPrefs"] = {"performance": "ALL"}

# ✅ 드라이버 실행
driver = uc.Chrome(options=options, desired_capabilities=caps)

try:
    # ✅ 현재 IP 확인 (프록시 확인)
    driver.get("https://httpbin.org/ip")
    print("📡 현재 IP:", driver.page_source)

    # ✅ statiz 사이트 먼저 진입해서 쿠키 적용 가능하게 함
    driver.get("https://statiz.sporki.com")
    time.sleep(2)

    # ✅ 쿠키 삽입
    for cookie in cookies:
        driver.add_cookie(cookie)
    print("🍪 쿠키 삽입 완료")

    # ✅ 크롤링 대상 페이지 접근
    test_url = "https://statiz.sporki.com/schedule/?m=summary&s_no=20250001"
    driver.get(test_url)
    time.sleep(2)

    print("✅ 페이지 제목:", driver.title)
    print("📄 페이지 내용 (일부):\n", driver.page_source[:1000])

except Exception as e:
    print("❌ 에러 발생:", e)

finally:
    driver.quit()