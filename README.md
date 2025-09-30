# 🎟️ 뮤지컬 & 콘서트 대규모 티켓팅 서비스 프로젝트
<div>
  <img src="https://github.com/user-attachments/assets/ef04522a-c191-478f-815b-f0340923c6c1" style="width: 100%; height: 450px;">
</div>

## 🗣️ 프로젝트 소개

- 실시간 대규모 트래픽에도 끊김 없는 예매 경험을 제공하는 고성능 공연 티켓팅 플랫폼
- 콘서트, 연극, 뮤지컬 등 다양한 공연에 대해 유저가 실시간으로 예매할 수 있는 티켓팅 시스템 구축
<br><br>

## 🥅 프로젝트 목표

- **폭발적인 동시 접속(▶ 수만 RPS)** 에도 예매/결제가 중단되지 않는 인프라 설계
- **Redis 대기열 + 분산 락** 으로 공정한 좌석 선점 보장
- **Kafka + Saga 패턴** 으로 서비스 간 데이터 일관성 유지
- **Prometheus + Grafana + Loki** 로 실시간 모니터링 & 알림

<br><br>
## 🛠️ 기술 스택

이 프로젝트는 마이크로서비스 아키텍처 기반의 고성능 웹 애플리케이션으로, 최신 Java 및 Spring 기술을 활용해 구축되었습니다.

**주요 언어**: Java 17  
**빌드 도구**: Gradle 8.10.2

<table>
  <tr style="background-color: #f0f9ff;">
    <th><strong>Database / Caching</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>MySQL</strong> 8.0: 관계형 데이터베이스<br>
      - <strong>Redis</strong> 7.2: 고속 캐싱 및 세션 관리
    </td>
    <td align="center">
      <img src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1603423163/noticon/az0cvs28lm7gxoowlsva.png" width="50"/>
      <img src="https://github.com/user-attachments/assets/2a90d034-1542-47e0-a524-b4febc5f170e" width="50"/>
    </td>

  </tr>

  <tr style="background-color: #f0fff4;">
    <th><strong>Library / Framework</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>Spring Boot</strong> 3.3.2: Java 웹 애플리케이션 프레임워크<br>
      - <strong>Spring Cloud Eureka</strong>: 서비스 디스커버리<br>
      - <strong>Spring Cloud Gateway</strong>: API 게이트웨이<br>
      - <strong>Spring Cloud OpenFeign</strong>: 선언적 HTTP 클라이언트<br>
      - <strong>Redisson</strong>: Redis 클라이언트<br>
      - <strong>Swagger</strong>: API 문서화<br>
      - <strong>QueryDSL</strong> 5.0.0: 타입 안전 쿼리 빌더<br>
    </td>
    <td align="center">
      <img src="https://spring.io/img/projects/spring-boot.svg" width="50" />
      <img src="https://docs.spring.io/spring-cloud-gateway/docs/2.2.10.BUILD-SNAPSHOT/reference/htmlsingle/favicon.ico" width="50" />
      <img src="https://github.com/user-attachments/assets/26200555-375a-45d7-afd0-bc0fb0f00877" width="50"/><br>
      <img src="https://static1.smartbear.co/swagger/media/assets/swagger_fav.png" width="50" />
      <img src="https://raw.githubusercontent.com/querydsl/querydsl.github.io/refs/heads/master/ico/favicon.ico" width="50" />
    </td>
  </tr>
  <tr style="background-color: #fefce8;">
    <th><strong>Cloud / DevOps</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>AWS EC2</strong>: 클라우드 컴퓨팅<br>
      - <strong>Docker & Docker-Compose</strong>: 컨테이너화<br>
      - <strong>GitHub Actions</strong>: CI/CD 파이프라인<br>
      - <strong>GitHub Container Registry</strong>: 컨테이너 이미지 저장
    </td>
    <td align="center">
      <img src="https://upload.wikimedia.org/wikipedia/commons/9/93/Amazon_Web_Services_Logo.svg" width="50" />
      <img src="https://github.com/user-attachments/assets/6e4ecc9f-2f9e-44cb-8088-8f780665c693" width="50"/>
      <img src="https://github.com/user-attachments/assets/ecc34476-f5be-4275-bd97-fa247dbc6fa2" width="50" /><br>
      <img src="https://github.com/user-attachments/assets/2f9ab1ca-6a02-4dcb-b15a-e4dec9f803c7" width="50" />
      <img src="https://github.com/user-attachments/assets/eafae5bf-27b5-4f2c-b96d-502dbba5b25d" width="50" />
    </td>
  </tr>
  <tr style="background-color: #f3e8ff;">
    <th><strong>Monitoring / Observability</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>Prometheus</strong>: 메트릭 수집<br>
      - <strong>Grafana</strong> 11.0: 시각화 대시보드<br>
      - <strong>Zipkin</strong>: 분산 추적<br>
      - <strong>Loki</strong>: 로그 집계
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/ea790f84-26fa-47fa-8692-336fe1cb215c" width="50" />
      <img src="https://github.com/user-attachments/assets/ca6ca0bf-dbd0-4259-969c-12fc4c3ba110" width="50" />
      <img src="https://github.com/user-attachments/assets/98dfbfd6-1735-4f9a-a2d0-263939832c14" width="50" />
      <img src="https://github.com/user-attachments/assets/b6ba9cf6-17ee-453e-a289-fc4e4afbdc98" width="50" />
    </td>
  </tr>
  <tr style="background-color: #ffe5e5;">
    <th><strong>Collaboration / Tools</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>GitHub</strong>: 코드 호스팅 및 협업<br>
      - <strong>Notion</strong>: 프로젝트 관리<br>
      - <strong>Postman</strong>: API 테스트<br>
      - <strong>Discord</strong>: 팀 커뮤니케이션
    </td>
    <td align="center">
      <img src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1567128822/noticon/osiivsvhnu4nt8doquo0.png" width="50" />
      <img src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1570106347/noticon/hx52ypkqqdzjdvd8iaid.svg" width="50"/>
      <img src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1566914838/noticon/qlfe77nbcvdscm762prm.png" width="50" />
      <img src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1640152045/noticon/albswwsjaaxvxbyhmwig.png" width="50" />
    </td>
  </tr>
  <tr style="background-color: #e0f2fe;">
    <th><strong>Messaging</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>Apache Kafka</strong> 3.7.0: 이벤트 스트리밍
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/992489bb-4764-40d4-a582-ed4103b59987" width="50" />
      <img src="https://github.com/user-attachments/assets/80ca122c-e8f6-45f0-86aa-56f30eaf8a81" width="50" />
    </td>
  </tr>
  <tr style="background-color: #ecfccb;">
    <th><strong>Performance Testing</strong></th>
    <th align="center">기술</th>
  </tr>
  <tr>
    <td>
      - <strong>JMeter</strong> 5.6.3: 부하 테스트
    </td>
    <td align="center">
      <img src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1666058624/noticon/zppnxgsegyfrhrl42q2p.png" width="50" />
    </td>
  </tr>
</table>
<br>

## 🗂️ 프로젝트 구조
```
TakenSeat                                                  # 루트 프로젝트
├──com.taken_seat.eureka-service                           # 서비스 디스커버리 (port : 19090)
│  ├──src/main/java/com/taken_seat/eureka_service
│  ├──Dockerfile
│
├──com.taken_seat.gateway-service                          # API 게이트 웨이 (port : 19091)
│  ├──src/main/java/com/taken_seat/gateway_service
│  │       ├──config/
│  │       ├──exception/
│  │       ├──filter/
│  │       ├──util/
│  ├──Dockerfile
│
├──com.taken_seat.auth-service                             # 유저 서비스 (port : 19092)
│  ├──src/main/java/com/taken_seat/auth_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/auth_service
│  ├──Dockerfile
│
├──com.taken_seat.coupon-service                           # 쿠폰 서비스 (port : 19093)
│  ├──src/main/java/com/taken_seat/coupon_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/coupon_service
│  ├──Dockerfile
│
├──com.taken_seat.queue-service                            # 대기열 서비스 (port : 19094)
│  ├──src/main/java/com/taken_seat/queue_service
│  │   ├──application/
│  │   ├──infrastructure/
│  │   ├──presentation/    
│  ├──test/java/com/taken_seat/queue_service
│  ├──Dockerfile
│
├──com.taken_seat.booking-service                          # 예매 서비스 (port : 19095)
│  ├──src/main/java/com/taken_seat/booking_service
│  │   ├──booking/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──ticket/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/booking_service
│  ├──Dockerfile
│
├──com.taken_seat.performance-service                      # 공연 서비스 (port : 19096)
│  ├──src/main/java/com/taken_seat/performance_service
│  │   ├──performance/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──performancehall/
│  │   │   ├──application/
│  │   │   ├──domain/
│  │   │   ├──infrastructure/
│  │   │   ├──presentation/
│  │   ├──performanceticket/
│  │   │   ├──application/
│  │   │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/performance_service
│  ├──Dockerfile
│
├──com.taken_seat.payment-service                          # 결제 서비스 (port : 19097)
│  ├──src/main/java/com/taken_seat/payment_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/payment_service
│  ├──Dockerfile
│
├──com.taken_seat.review-service                           # 리뷰 서비스 (port : 19098)
│  ├──src/main/java/com/taken_seat/review_service
│  │   ├──application/
│  │   ├──domain/
│  │   ├──infrastructure/
│  │   ├──presentation/
│  │   ├──common/
│  ├──test/java/com/taken_seat/review_service
│  ├──Dockerfile
│
├──com.taken_seat.common-service                           # 공통 모듈
│  ├──prometheus/
│  ├──docker-compose.common.yml
│  ├──Dockerfile
│  ├──src/main/java/com/taken_seat/review_service
│      ├──aop/
│      ├──component/
│      ├──config/
│      ├──dto/
│      ├──entity/
│      ├──exception/
│      ├──message/
│
├──loki-config.yml                                          # loki 설정
```

<br><br>

## 📖 서비스 아키텍처
<img src="https://github.com/user-attachments/assets/870923ff-9446-4dfb-a2ed-026761128858" >



<br><br>

## ✏️ 이벤트 흐름
* 사용자 플로우 ⇒ 회원가입/로그인 → 공연 탐색 → 회차·좌석 선택 → 대기열 → 예매 정보 입력 → 결제 → 예매 내역 확인/취소 → 리뷰 작성
<img src="https://github.com/user-attachments/assets/13ec85de-610b-4886-9109-4558e304f71e">

<br><br>

## 🚩 로컬 실행 방법
```
# 1) 저장소 클론
$ git clone https://github.com/your-org/TakenSeat.git
$ cd TakenSeat

# 2) 빌드
$ ./gradlew clean build -x test  # 테스트 생략 빌드

# 3) 공통 인프라 + 서비스 기동
$ docker compose -f docker-compose.common.yml up -d   # MySQL · Redis · Kafka · Prometheus 등
$ ./scripts/start-services.sh                         # 각 서비스 도커 빌드 & 기동 스크립트

# 4) 확인
http://localhost:19091  (Gateway)
http://localhost:3000   (Grafana) – admin / admin

```
> 주의 : 환경 변수는 .env.sample 참고 후 .env 로 복사해 값 채우기

<br><br>

## 📋 API 문서
- Swagger UI URL ⇒ `http://localhost:19091/docs`
- 서비스별 Swagger UI ⇒ `http://localhost:{PORT}/swagger-ui.html`
- RestDocs HTML ⇒ `build/generated-snippets/index.html`

<br><br>

## 🖥️ 모니터링 & 관측

- **Prometheus** : `/actuator/prometheus` 메트릭 스크랩
- **Grafana** : Dashboard ID 12345 – Seat TPS, Queue Depth, Payment Latency
- **Loki** : `{service="booking-service"}` 쿼리로 예매 로그 검색
- **Zipkin** : TraceID 로 예매‑공연 호출 흐름 추적

<br><br>

## ⚙️ CI/CD 파이프라인

1. **GitHub Actions** – PR 검증 (Test · Lint · Build) & Docker 이미지 Push

<br><br>

## 🛎️ 기술적 의사 결정

> ⏱️ [Queue 방식 비교 및 선택 기준](https://github.com/toolatebro/TakenSeat/wiki/%5BTechnical-Decision%5D-%EB%8C%80%EA%B8%B0%EC%97%B4-%EA%B5%AC%ED%98%84-%EB%B0%A9%EC%8B%9D-%EA%B2%B0%EC%A0%95)  
> 대기열 처리 방식의 장단점 비교 및 도입 이유

> 📊 [ELK vs Grafana + Prometheus + Loki](https://github.com/toolatebro/TakenSeat/wiki/%5BTechnical-Decision%5D-ELK-vs-Grafana---Prometheus---Loki)  
> 로그 및 모니터링 스택 비교 분석

> 🔍 [Micrometer Tracing VS OpenTelemetry](https://github.com/toolatebro/TakenSeat/wiki/%5BTechnical-Decision%5D-Micrometer-Tracing-VS-OpenTelemetry)  
> 분산 트레이싱 도구의 기능과 차이

> 🗃️ [PostgreSQL VS MySQL](https://github.com/toolatebro/TakenSeat/wiki/%5BTechnical-Decision%5D-PostgreSQL-VS-MySQL)  
> 데이터베이스 성능 및 호환성 비교

> 📨 [RabbitMQ VS Kafka](https://github.com/toolatebro/TakenSeat/wiki/%5BTechnical-Decision%5D-RabbitMQ-VS-Kafka)  
> 메시징 시스템의 처리량 및 구조적 차이

<br><br>

## ⁉️ 트러블 슈팅
> ❗️ [정다예 - Grafana에서 Prometheus 메트릭이 404 뜨던 문제 해결](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EB%8B%A4%EC%98%88%5D-Grafana%EC%97%90%EC%84%9C-Prometheus-%EB%A9%94%ED%8A%B8%EB%A6%AD%EC%9D%B4-404-%EB%9C%A8%EB%8D%98-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)  
> Grafana에서 Prometheus 메트릭 연결 시 404 오류 발생 원인 및 해결 과정

> ❗️ [정다예 - Windows 환경에서 JVM CPU 메트릭 수집 불가 이슈](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EB%8B%A4%EC%98%88%5D-Windows-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-JVM-CPU-%EB%A9%94%ED%8A%B8%EB%A6%AD-%EC%88%98%EC%A7%91-%EB%B6%88%EA%B0%80-%EC%9D%B4%EC%8A%88)  
> Windows에서 JVM CPU 사용률 메트릭이 수집되지 않던 문제 원인 분석

> ❗️ [전승현 - Spring Redis hashOps.keys()로 패턴 조회 안 되는 이유와 SCAN을 사용한 해결법](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%ED%98%84%5D--Spring-Redis-hashOps.keys()%EB%A1%9C-%ED%8C%A8%ED%84%B4-%EC%A1%B0%ED%9A%8C-%EC%95%88-%EB%90%98%EB%8A%94-%EC%9D%B4%EC%9C%A0%EC%99%80-SCAN%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%9C-%ED%95%B4%EA%B2%B0%EB%B2%95)  
> Redis에서 hashOps.keys()가 동작하지 않는 이유와 SCAN 명령어를 활용한 대체 방법

> ❗️ [전승현 - 결제 도메인에서 마일리지 쿠폰 차감 책임을 분리한 이유](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%ED%98%84%5D-%EA%B2%B0%EC%A0%9C-%EB%8F%84%EB%A9%94%EC%9D%B8%EC%97%90%EC%84%9C-%EB%A7%88%EC%9D%BC%EB%A6%AC%EC%A7%80-%EC%BF%A0%ED%8F%B0-%EC%B0%A8%EA%B0%90-%EC%B1%85%EC%9E%84%EC%9D%84-%EB%B6%84%EB%A6%AC%ED%95%9C-%EC%9D%B4%EC%9C%A0)  
> 결제와 할인 책임 분리를 통해 도메인 역할 명확화

> ❗️ [백승규 - access_token을 BlackList로 관리하면서 만료시간을 설정하는 과정에서 깨달음](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%EA%B7%9C%5D-access_token%EC%9D%84-BlackList%EB%A1%9C-%EA%B4%80%EB%A6%AC%ED%95%98%EB%A9%B4%EC%84%9C-%EB%A7%8C%EB%A3%8C%EC%8B%9C%EA%B0%84%EC%9D%84-%EC%84%A4%EC%A0%95%ED%95%98%EB%8A%94-%EA%B3%BC%EC%A0%95%EC%97%90%EC%84%9C-%EA%B9%A8%EB%8B%AC%EC%9D%8C)  
> 토큰 블랙리스트 처리 시 만료 시간 설정 방식에 대한 인사이트

> ❗️ [백승규 - 공통적으로 반복되는 부가적인 기능(권한 체크)에 커스텀 어노테이션과 AOP 활용](https://github.com/toolatebro/TakenSeat/wiki/%5BTrouble-Shooting%5D-%5B%EC%8A%B9%EA%B7%9C%5D-%EA%B3%B5%ED%86%B5%EC%A0%81%EC%9C%BC%EB%A1%9C-%EB%B0%98%EB%B3%B5%EB%90%98%EB%8A%94-%EB%B6%80%EA%B0%80%EC%A0%81%EC%9D%B8%EA%B8%B0%EB%8A%A5(%EA%B6%8C%ED%95%9C-%EC%B2%98%ED%81%AC)%EC%97%90-%EC%BB%A4%EC%8A%A4%ED%85%80-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98%EA%B3%BC-AOP-%ED%99%9C%EC%9A%A9)  
> 권한 검증과 같은 반복 로직에 커스텀 어노테이션과 AOP 도입

<br>

## 📌 팀원 역할분담
| 🫅 Leader | 👷 Sub-Leader | 👷 Member | 👷 Member | 👷 Member |   
| :---: | :---: | :---: | :---: | :---: |
| <img src="https://avatars.githubusercontent.com/u/128787964?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/81623522?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/140582940?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/155501200?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/100333239?v=4" width="120px;" alt=""/> |
| [전승현](https://github.com/jjsh0208) | [이채연](https://github.com/dkki4887) | [백승규](https://github.com/seungg8361) | [정다예](https://github.com/Jungdaye89) | [강성준](https://github.com/Goldbar97)
| Payment / Review / DevOps | Queue | Auth / Coupon / Mileage | Performance / Mornitoring | Booking / Ticket |

<br>
