# 프로젝트 소개
멘토링 프로그램을 개설하여, 멘티를 모집하고, 미션을 통한 게시판 형식으로 멘토링 프로그램을 진행하는 게시판 형식의 웹서비스 입니다.    

스프링 부트를 이용하여 백엔드를 구성하였으며, AWS를 이용하여 실제 사이트 배포 과정까지 경험하는 것을 목표로 프로젝트를 진행하였습니다.

차후, 공부한 것을 덧붙여 나가며 지속적인 업데이트를 할 예정입니다.

사이트: [mentoree.tk](https://mentoree.tk)    
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
3. 구현 기능
