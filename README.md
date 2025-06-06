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
![image](https://github.com/user-attachments/assets/50ff2c67-b0b3-43cd-a7ba-27205c68d6e4)
1. 사용자는 앱에서 팀 정보/경기 결과/게시글 등을 요청
2. React Native -> /api/v1/... Spring REST API 호출
3. 경기 데이터는 매일 새벽 크롤링
- 크롤링 모듈에서 Kafka로 메시지 전송
- Consumer가 DB에 경기/하이라이트 데이터 저장
4. 알림/썸네일 등은 Kafka 비동기로 처리
5. 서버는 Cloudtype으로 배포되어 자동 스케줄링 가능

## Skills
⭐ Java, Spring Boot, Spring Batch, Kafka / RabbitMQ
⭐ 
⭐

## ✅ 작업 내용 요약

## 🙋‍♀️ 기타
- 프론트와 API 명세 공유
