## 👉🏼 8은 안으로 굽조 아웃소싱(Outsourcing) 팀 프로젝트

### 🙋‍♀️ 아웃소싱(Outsourcing) 팀 프로젝트 설명
- **아웃소싱(Outsourcing) 팀 프로젝트**는 <u>Spring을 활용하여 배달 앱을 구현</u>한 과제입니다.
- **팀원 : 이승민, 전서연, 권은서, 조예인, 전영환**
- 기간 : 2025.02.28 - 2025.03.07


<br><br>

#### 역할 분담
⌨️ [Seung-min-88](https://github.com/Seung-min-88) : 회원가입, 로그인, 유저 정보 및 인증인가 + Redis 설정  <p>
🗿 [Seoyeon](https://github.com/MythologyDevSeoyeon) : 가게 생성, 읽기, 업데이트, 삭제 등 전체 기능 + 스웨거 설정 <p>
🍊 [KWON EUNSEO](https://github.com/euuns) : 사장이 관리할 메뉴와 메뉴에 대한 옵션 기능 + 소유한 가게 Cache <p>
🐣 [YEIN JO](https://github.com/codingTrip-IT) : 고객의 주문 생성, 사장의 수락여부, 주문 상태 수정 등 주문 기능 <p>
🦥 [younghwan314](https://github.com/younghwan314) : 주문에 대한 리뷰 생성, 읽기, 수정, 삭제 등 주문 기능

<br>

📑 진행 및 회의 기록 : [8은 안으로 굽조](https://www.notion.so/teamsparta/8-1a82dc3ef514806a8fc4df81a08b34c1) 노션에서 진행

<br>

## 🛠 목차

1. [📚 STACKS](#-STACKS)
2. [👩🏻‍ API 명세](#-API-명세)
3. [👩 ERD](#-ERD)
4. [💥 트러블슈팅](#-트러블슈팅)
5. [⚗️ 테스트 커버리지](#-테스트-커버리지)
<br>   

<br><br><br>

<div align=center> 

## 📚 STACKS

<br>

  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">

<br><br><br>

## 👩🏻‍ API 명세
### 회원 API
![{B26715D1-F59D-4D95-885A-6226459E5C3F}](https://github.com/user-attachments/assets/110ba700-4a0d-4266-941c-6283830d5fb8)


### 가게 API
![{E40A2649-359D-49FE-A9CE-23B7FBC541B1}](https://github.com/user-attachments/assets/839c5c63-67bf-47a4-a391-cd03bc315860)


### 메뉴 API
![{3F7CF7B4-41C4-4C24-841B-9FC9E36E58A1}](https://github.com/user-attachments/assets/d8faca05-8d4c-46d2-b424-23186f129120)


### 주문 API
![{4F46091E-5477-4463-9E9D-A20FE5CABBA8}](https://github.com/user-attachments/assets/b2c3d005-b212-4f1f-9e95-aaa79af8e1c5)


### 리뷰 API
![{5668F61F-D8D9-441D-A532-93F38967EA08}](https://github.com/user-attachments/assets/7068aa31-5b7a-44b8-bd43-9ed1ea7fecf1)


<br><br><br>

## 👩 ERD
![image](https://github.com/user-attachments/assets/3eb5b204-3bcc-4c29-a1a1-0059b95a415b)

<br><br><br>

<div align=left> 

## 💥 트러블슈팅

### 1. Redis와 Refresh Token문제
블로그 링크: https://cork-7.tistory.com/56


🔎 상황 <p>
Redis와 DB에 Refresh 토큰을 저장하는 도중 Bean 충돌 발생 <br><br>

💥 문제 <p>
`ReFreshTokenRepository Bean`에서 `Redis`와 `JPA의 Bean` 등록 도중 충돌 <br><br>

🚀 해결 <p>
Redis에만 Refresh 토큰을 저장하기로 결정 <br><br>

✅ 추후 도전 예정 <p>
`HttpOnly`와 `secure`를 사용해 보안 강화 <br><br>

<br><br>

### 2. cache에 정보를 저장하고 값 조회
블로그 링크: [https://rvrlo.tistory.com/entry/Framework/SpringBoot](https://rvrlo.tistory.com/entry/SpringBoot-Cache-%EA%B0%92%EC%9D%84-%EC%A0%80%EC%9E%A5%ED%95%98%EC%A7%80-%EB%AA%BB%ED%95%98%EB%8D%98-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%EB%B0%8F-Cacheable%EA%B3%BC-CachePut-%EC%B0%A8%EC%9D%B4)


🔎 상황 <p>
가게를 생성하려고 할 때 캐시에 저장되지 않고, 오히려 가게 생성조차 되지 않는 문제가 발생 <br><br>

💥 문제 <p>
`@Cacheable`을 사용하여 캐시에 값이 존재하면 메서드 실행 x
제일 처음 가게를 생성하게 되면 무조건 List는 null → 참조 하려고 하여 `NPE` 발생 <br><br>

🚀 해결 <p>
`@Cacheable`을 무조건 메서드를 실행한 후에 캐시를 반환하는 `@CachePut`으로 변경 <br><br>

💡 느낀 점 <p>
코드를 작성해도 어떻게 동작하는지 이해하지 못하고, 참고자료만 찾았기 때문에 오류 해결을 위한 공부가 필요 <br>
결국 해결 과정에서 `@Cacheable`과 `@CachePut`의 차이를 더 명확히 알게 됨

<br><br><br>

## ⚗️ 테스트 커버리지
![image](https://github.com/user-attachments/assets/6088c825-4142-433f-a07d-1886b3761771)

