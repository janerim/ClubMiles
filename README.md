# ClubMile

## Description
### 트리플여행자 클럽 마일리지 서비스
트리플 사용자들이 장소에 리뷰를 작성할 때 포인트를 부여하고, 전체/개인에 대한 포인트 부여 히스토리와 개인별
누적 포인트를 관리하고자 합니다.

## Environment
### JAVA
> java 11  
> gradle 7.4.1  
> Spring boot 2.6.7   
> Swagger 3.0  
> JPA, QueryDSL

### DB
> mariaDB 10.6  
 (MySQL 5.7 = MariaDB 10.1 (2014년6월))

## Prerequisite
> java 11   
> mariaDB 10.6

## Usage
#### 프로젝트 실행을 위한 준비
1. Qclass 생성  
```
ClubMiles> ./gradlew compileQuerydsl
```
2. 프로젝트 빌드
```
ClubMiles> ./gradlew build (-x test)
```
3. 프로젝트 실행
```
ClubMiles\build\libs> java -jar .\ClubMiles-0.0.1-SNAPSHOT.jar
```
4. Swagger 접속  
```
http://localhost:8080/swagger-ui/index.html
```
5. DDL 실행  
* 기본 insert 데이터 : 사용자(user) 2명, 장소(place) 3곳
```
DDL_script.sql 
```
 
 
 #### 프로젝트 테스트 순서
 리뷰작성 이벤트
 * 리뷰작성 이벤트 api 호출을 위해서는 photoId와 reviewId 필요  
 * support-controller 의 api 들을 이용해 각각의 Id를 생성


**1. support-controller -> POST /api/photo - 첨부사진 업로드**  
> request: 사진 업로드(jpg, png만 가능)  
> response: photoId


**2. support-contoller -> GET /api/review_id - 리뷰 ID 생성**  
> request: none  
> response: reviewId


**3. event-controller -> POST /api/events - 리뷰 작성 이벤트**  
> request: 1,2 번에서 받은 Id를 포함하여 DTO 구성  
> response: reviewId 


**4. event-controller -> GET /api/point - 포인트 조회**  
> request: DTO(userId)  
- userId가 null일 경우 전체 사용자에 대해서 조회  
> response: 사용자ID, 현재포인트, 장소별포인트
