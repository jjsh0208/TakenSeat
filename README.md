## TakenSeat: 대규모 트래픽 티켓팅 서비스 (개인 기여 내역)
MSA(Microservices Architecture) 기반의 고성능 공연·뮤지컬 티켓 예매 플랫폼
- **개발 기간:** 2025년 4월 9일 ~ 2025년 5월 9일
- **핵심 목표:** 10만 명 이상의 트래픽을 가정한 환경에서 끊김 없는 예매 경험 제공 및 데이터 정합성 보장
- **담당 역할:** 백엔드 개발 (결제/리뷰 도메인) 및 DevOps (CI/CD 파이프라인 구축)
- **원본 레포지토리:** [TakenSeat GitHub Repository](https://github.com/toolatebro/TakenSeat)

<br><br>

## 시스템 아키텍처 및 ERD

<img width="4412" height="2892" alt="TakenSeat 아키텍처" src="https://github.com/user-attachments/assets/966f0d73-8335-4f4a-bcba-ae273126ac05" />

<br>

<img width="2370" height="1202" alt="04 16 최신화 버전" src="https://github.com/user-attachments/assets/f48f4386-fee0-4163-b92f-4cd960088633" />

<br><br>

## 핵심 기술적 성과 및 트러블슈팅

### 1. 대량 데이터 처리 성능 19배 향상 (95% 단축)
- **문제:** 리뷰 평점 및 통계 갱신 시 반복적인 개별 쿼리로 인한 성능 저하 발생
- **해결:**  IN 절 기반의 Bulk Fetch를 적용하여 DB I/O를 감소시키고, 캐시 갱신 시 Redis Pipeline과 DB Bulk Query를 도입하여 다수의 명령을 한 번의 네트워크 요청으로 처리
- **결과:** 처리 시간을 3.8초에서 0.2초로 95% 단축 (19배 성능 향상)
- **Blog:** [1차 배치 성능 개선 Blog ](https://ddong-kka.tistory.com/61) , [2차 리팩터링 Blog](https://ddong-kka.tistory.com/81)

<br>

### 2. Write-Back 전략 및 Redis Atomic 연산을 통한 동시성 제어와 DB 부하 최소화
- **문제:** 빈번한 좋아요 요청 시 발생하는 즉각적인 DB 업데이트로 인한 커넥션 병목 현상 및 다중 동시 요청 시의 카운트 누락의 동시성 문제 발생
- **해결:**  
  - Redis Write-Back 캐싱 전략을 도입하여 상태 변경을 인메모리에 우선 반영하고 Spring Scheduler를 통해 5분 단위로 DB에 일괄 갱신함
  - Redis의 Atomic 연산(increment)을 활용하여 별도의 락(Lock) 없이 동시성 제어 및 중복 클릭 방지 구현
- **결과:** 잦은 DB I/O를 5분 단위의 배치 처리로 통합하여 DB 부하를 감소시켰으며, 동시 요청 환경에서도 데이터 정합성 보장 및 응답 지연 없는 처리 달성

## 사용 기술 스택

- **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, QueryDSL
- **Database & Cache:** MySQL, Redis
- **Messaging:** Apache Kafka
- **MSA Architecture:** Spring Cloud Gateway, Eureka, OpenFeign
- **DevOps & Infra:** Docker, GitHub Actions, AWS EC2
- **Testing & Tools:** JMeter, TossPayments API

## 이벤트 흐름

<img width="1655" height="1161" alt="TakenSeat 이벤트 흐름도 drawio" src="https://github.com/user-attachments/assets/565cc8bf-3671-4e5e-a0a8-fc2eeadc1855" />


## 로컬 실행 방법
로컬 실행 및 환경 구성 방법은 아래 Wiki 문서를 참고해주세요.

[📖 TakenSeat Local 실행 가이드](https://github.com/jjsh0208/TakenSeat/wiki/TakenSeat-Local-%EC%8B%A4%ED%96%89-%EA%B0%80%EC%9D%B4%EB%93%9C)

<br>

## 프로젝트 구조

 8개의 마이크로서비스로 구성되어 있으며, 아래는 제가 담당하고 구현한 **결제, 리뷰, 공통 모듈**을 중심으로 요약한 디렉토리 구조입니다.

```text
TakenSeat (Root)
├── com.taken_seat.payment-service         # 결제 서비스 (담당)
│   ├── src/main/java/com/taken_seat/payment_service
│   │   ├── presentation/                  # REST API Endpoints (Controller)
│   │   ├── application/                   # 비즈니스 유스케이스 및 트랜잭션 관리
│   │   ├── domain/                        # 핵심 도메인 모델 및 Repository 인터페이스
│   │   └── infrastructure/                # TossPayments 연동, Redis/MySQL 영속성 구현
│   └── Dockerfile
│
├── com.taken_seat.review-service          # 리뷰 서비스 (담당)
│   ├── src/main/java/com/taken_seat/review_service
│   │   └── (결제 서비스와 동일한 도메인 중심 계층형 아키텍처 적용)
│   └── Dockerfile
│
├── com.taken_seat.common-service          # 공통 모듈 및 인프라 (담당)
│   ├── src/main/java/com/taken_seat/common_service
│   │   ├── aop/                           # 로깅 및 Role 검증 등 공통 관심사
│   │   ├── config/                        # Redis, Kafka 등 공통 인프라 설정
│   │   └── exception/                     # 전역 예외 처리 (GlobalExceptionHandler)
│   ├── prometheus/                        # 모니터링 수집 설정
│   └── docker-compose.common.yml          # 공통 인프라 컨테이너 설정
│
├── com.taken_seat.eureka-service          # 서비스 디스커버리 (공통 기여)
├── com.taken_seat.gateway-service         # API 게이트웨이 (공통 기여)
│
├── com.taken_seat.auth-service            # 유저 서비스 (타 팀원 담당)
├── com.taken_seat.booking-service         # 예매 서비스 (타 팀원 담당)
├── com.taken_seat.coupon-service          # 쿠폰 서비스 (타 팀원 담당)
├── com.taken_seat.performance-service     # 공연 서비스 (타 팀원 담당)
└── com.taken_seat.queue-service           # 대기열 서비스 (타 팀원 담당)
```

## 📌 팀원 역할분담

| 🫅 Leader | 👷 Sub-Leader | 👷 Member | 👷 Member | 👷 Member |
| --- | --- | --- | --- | --- |
| <img src="https://avatars.githubusercontent.com/u/128787964?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/81623522?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/140582940?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/155501200?v=4" width="120px;" alt=""/> | <img src="https://avatars.githubusercontent.com/u/100333239?v=4" width="120px;" alt=""/> |
| [전승현](https://github.com/jjsh0208) | [이채연](https://github.com/dlchacha) | [백승규](https://github.com/seungg8361) | [정다예](https://github.com/Jungdaye89) | [강성준](https://github.com/Goldbar97) |
| Payment / Review / DevOps | Queue | Auth / Coupon / Mileage | Performance / Mornitoring | Booking / Ticket |
