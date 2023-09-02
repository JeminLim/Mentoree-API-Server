# 프로젝트 소개
멘토링 프로그램을 개설하여, 멘티를 모집하고, 미션을 통한 게시판 형식으로 멘토링 프로그램을 진행하는 게시판 형식의 웹서비스 입니다.    

스프링 부트를 이용하여 백엔드를 구성하였으며, AWS를 이용하여 실제 사이트 배포 과정까지 경험하는 것을 목표로 프로젝트를 진행하였습니다.

차후, 공부한 것을 덧붙여 나가며 지속적인 업데이트를 할 예정입니다.

프로젝트 관련 기록: [블로그](https://devcabinet.tistory.com/)    

---

## 프로젝트 구성

![project](https://user-images.githubusercontent.com/65437310/220124700-eb043b2d-7e4e-4421-b3f3-6bccd07a35e7.png)

* 요청 흐름
  + 정적인 요청의 경우, Nginx에서 웹서버의 역할을 하고, api 요청이 들어온 경우 리버스 프록시 구조로써 스프링부트 어플리케이션으로 요청을 합니다.    
  + 백엔드단에서는, 유저의 로그인 토큰 관련 정보는 Redis를 통해서 저장을 하고, 이외 데이터들은 AWS의 RDS를 사용하여 데이터베이스를 구축했습니다.    
    
* 배포 흐름
  + 깃허브 main 브랜치에 푸시가 일어날 경우, webhook을 통해 젠킨스에서 변동을 감지한다.
  + S3에 빌드 파일을 전달하여, 빌드를 한 후, 빌드가 완료되면 codedeploy를 통해서 배포를 요청한다.
  + codedeploy에서는 요청을 받으면, S3 버킷의 빌드 파일을 가져와 배포스크립트를 토대로 활성화 된 도커 이미지를 정지한 후, 새로운 버전의 이미지를 생성 및 구동한다.
  
---
        
## ERD
![ERD](https://user-images.githubusercontent.com/65437310/220372640-1eb0763e-0e5d-48b6-b70c-85b26722735f.png)
    
---
## 사용 기술 스택
* Spring Boot 2.7.5
* MariaDB, AWS RDS
* JPA
* 스프링 시큐리티, JWT
* Java 11
---

## 기능구현
* 프로그램 목록 조회 및 필터링    
  > 프로그램 목록을 조회하거나 카테고리에 따른 프로그램을 필터링해서 조회할 수 있습니다.
        
![program_list](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/9356491f-a50e-488b-8c63-7bb49a2269b2)    
![program_filter](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/d96ba16c-5a78-471d-b7f8-007e38c20ea5)

* 폼 로그인 및 소셜 로그인
  > 폼 로그인 또는 구글을 이용한 OAuth 로그인을 할 수 있습니다.    

![form_login](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/aa78bfe1-5d7e-46b2-b807-b9b23e141720)    
![oauth_login](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/13fe927e-cde6-4895-b451-618e199f017b)    


* 프로그램 개설
  > 프로그램을 개설해서 멘티를 모집할 수 있습니다.
      
![program_create](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/1fbcfe9c-3c3c-4ffb-9979-d2c767b9f9a5)

  
* 프로그램 신청 및 관리
  > 프로그램 신청 및 프로그램 개설자는 신청자에 대한 승낙/거절 을 할 수 있습니다.    

![apply_program](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/4c81bd1c-fca4-43c5-8109-8ee9a3659a52)     
![apply_approve](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/d9dae545-2916-4957-9d8f-2080f24a415b)     

  
* 게시글 작성 및 임시저장
  > 게시글을 작성할 수 있으며, 작성 중인 글은 자동으로 임시저장이 됩니다. 1분 주기 자동저장과 페이지 이동 시 임시저장이 이루어집니다.    


![board_write](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/55998625-5e6c-4ced-9614-60b69b9b2a0f)




---

## 프로젝트 구조
![프로젝트구조](https://github.com/JeminLim/Mentoree-API-Server/assets/65437310/cd61f143-554d-484e-86e3-ab71dd0990d4)

- api : 각 API controller와 advice 관련 요청에 대한 수신 및 결과를 반환에 관여합니다.
- config : aop, filter, interceptor, redis, security 등 비지니스 로직 이외의 시스템에 관계되어 있는 패키지 입니다.
- domain : entity와 repository로 구성하여, 도메인과 해당 연관 비지니스 로직을 함꼐 작성하였습니다.
- exception : 각종 커스텀 예외를 작성한 패키지 입니다.
- service : 비지니스 로직에 관련된 패키지 입니다.

---




