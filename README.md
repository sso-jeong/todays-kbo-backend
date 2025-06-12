작성자 : 김소정(백앤드 개발자)

수정일 : 2025년 06월 06일

프로젝트 백앤드 깃 주소

https://github.com/sso-jeong/todays-kbo-backend

프로젝트 상세히 보기

https://ss-o.tistory.com/186

https://www.notion.so/Today-s-KBO-2089270efdce80baa7a8d6752f5e9bc6

프론트엔드 협업 주소

https://www.notion.so/204d83a1aa7a8094ae45dca4aadb2d47?v=208d83a1aa7a80269480000c0205b7a5&source=copy_link

## 프로젝트 정보

- 유형: 개인 프로젝트
- 기간: 2025년 06월 ~ 2025년 8월
- 인원: 총 2명 ( 프론트앤드 개발자 1명, 백앤드 개발자 1명 )
- 목표: KBO 팬들을 위한 통합 정보 제공 앱 서비스
- 간단 요약: Spring Boot 기반 백엔드와 React Native 앱을 연동한 실제 서비스 개발 경험을 통해, 기획부터 구현, 배포, 협업까지의 전 과정을 익히는 것을 목표로 한다.

## 사용 기술
![image](https://github.com/user-attachments/assets/2309ee87-f487-457f-8508-840599e155b3)

프론트엔드 개발자 1명
- 사용 기술

백엔드 개발자 1명 - 김소정
- 스프링 부트를 이용한 백엔드 서버 구현
- 완성된 애플리케이션은 클라우드 타입을 통해 배포
- backend
  - Java
  - SpringBoot
  - SpringBatch
  - MySQL
  - JPA
  - Querydsl
 
- deploy
  - cloudtype
 
- etc
  - kafka
  - selenium

## 시스템 구성도
![image](https://github.com/user-attachments/assets/dd243be3-e834-4a0f-9d57-26f5de88d982)

1. 사용자 앱 (React Native)
- 사용자는 모바일 앱을 통해 REST API로 서버에 요청을 보냄

2. Spring Boot 서버
- 클라우드 플랫폼인 Cloudtype에 배포되어 있는 Spring Boot 앱이 API 요청을 처리함
- 주요 기능:
- REST API 제공
- 정기 크롤링을 위한 스케줄링 (Spring Batch)
- Kafka 메시지 프로듀서 & 컨슈머 역할 수행

3. 크롤링 모듈 (내부 포함)
- Selenium, BeautifulSoup 등을 활용해 경기 정보를 크롤링
- 매일 00:00에 Spring Batch가 실행되어 최신 경기 데이터를 수집

4. Kafka Producer
- 크롤링된 경기 정보를 Kafka Topic에 메시지로 전송

5. Kafka
- 도커 컨테이너로 실행

6. Kafka Consumer (Spring Boot 내부)
- Spring Boot에 내장된 Kafka Consumer가 메시지를 받아 처리함
- 메시지를 받아서 다음 작업을 수행함:
- Game/Highlight 정보 MySQL에 저장
- 알림 전송 준비 (썸네일 이미지 처리 포함 가능)

7. DB (MySQL)
- 사용자, 경기 정보, 커뮤니티 게시물 등의 모든 데이터가 저장됨
- /api/v1/games/today 같은 API에서 필요한 데이터를 조회함

8. 배포 플랫폼: Cloudtype
- 초기에는 Cloudtype을 사용해 Spring Boot 서버와 MySQL 컨테이너를 함께 구성 가능
- Kafka는 별도 도커 컨테이너로 로컬 또는 클라우드에서 동작

## Skills
⭐ Java, Spring Boot, Spring Batch, Kafka / RabbitMQ
⭐ 
⭐

## ✅ 작업 내용 요약

## 🙋‍♀️ 기타
- 프론트와 API 명세 공유
