# 한림대학교 공지사항 알림 시스템

## 프로젝트 개요

교내 공지사항을 매일 확인해야 하는 번거로움을 해소하고, 주요 공지를 놓치지 않도록 자동화된 알림 시스템을 개발했습니다. 사용자에게 당일의 주요 공지를 요약하여 매일 저녁 한 번에 전달함으로써, 필요한 정보를 편리하게 받아볼 수 있도록 합니다.

## 주요 기능

- **구독 기능**  
  이메일 인증을 통해 구독을 신청하면 최신 공지사항 알림을 매일 받을 수 있습니다. 

- **구독 취소 기능**  
  구독을 원하지 않는 경우, 이메일 인증을 통해 구독 취소를 진행할 수 있습니다. 취소 이후에는 더 이상 알림이 발송되지 않습니다.

## 기술적 도전과 해결 방법

**비용 효율적인 서버 운영**  
오랜 기간 유지 가능한 프로젝트로, 비용 절감을 위해 개인 라즈베리파이를 서버로 활용했습니다. 고정 IP가 불가능한 가정용 환경에서 안정적인 서버를 운영하기 위해 아래와 같은 설정을 적용했습니다.

1. **SuperDMZ 설정**: 라즈베리파이에 동적 외부 IP를 할당하여 외부 접속 가능하도록 설정.
2. **DDNS (no-ip)**: 무료 도메인을 사용하여 IP 변경 시에도 지속적으로 연결 유지.
3. **IP 자동 갱신**: no-ip의 DUC 프로그램을 라즈베리파이에 설치하여 IP 변경 시 자동 갱신하도록 설정.

**발송량 제한 완화**  
인증 메일과 공지사항 발송 메일을 분리하여 Gmail의 발송량 제한 문제를 해결했습니다. 

**성능 개선**  
Blocking I/O 문제로 인해 발송 시간이 오래 걸렸으나, 병렬 처리를 위한 메일 발송 스레드를 10개로 늘려 발송 소모 시간을 약 10배 단축했습니다.

**사용자 피드백 반영**  
사용자 피드백을 수용하여 새로운 공지가 있을 때마다 메일을 발송하는 구조에서, 하루에 한 번 당일 공지사항을 요약하여 보내는 방식으로 변경하여 메일 발송량을 줄이고 효율성을 높였습니다.

## 기술 스택

- **백엔드**: Spring Boot, Java, JPA/Hibernate, Thymeleaf
- **데이터베이스**: MySQL, Redis (이메일 인증 코드 저장)
- **이메일 전송**: SMTP (Gmail)
- **배포 환경**: 라즈베리파이 서버 (Ubuntu 22.04 LTS)
- **기타**: DDNS (no-ip) - 동적 IP 관리

## 프로젝트 링크

- **GitHub Repository**: [https://github.com/jyp-on/notice-crawler](https://github.com/jyp-on/notice-crawler)
- **Website**: [http://notice-crawler.ddns.net/](http://notice-crawler.ddns.net/)
