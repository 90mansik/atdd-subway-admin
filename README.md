<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/next-step/atdd-subway-admin">
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.

## 프로그래밍 요구사항
* 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.프로그래밍 요구사항
* 아래의 순서로 기능을 구현하세요.
    - 인수 조건을 검증하는 인수 테스트 작성
    - 인수 테스트를 충족하는 기능 구현
    - 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리 시키세요.
    - 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링 하세요.

```text
각각의 테스트를 동작시키면 잘 동작하지만 한번에 동작시키면 실패할 수 있습니다. 
이번 단계에서는 이 부분에 대해 고려하지 말고 각각의 인수 테스트를 작성하는 것에 집중해서 진행하세요.
```

## 힌트
### 인수테스트 격리
- @DirtiesContext
    - Spring Context를 이용한 테스트 동작 시 스프링 빈의 상태가 변경되면 해당 컨텍스트의 재사용이 불가하여 컨택스트를 다시 로드해야함
    - 스프링빈의 상태가 변경되었다는 설정을 하는 애너테이션
    - 테스트 DB는 메모리 디비로 컨테이너에 띄워져있는 상태이므로 컨텍스트가 다시 로드되면 기존 DB의 내용이 초기화됨
- @Sql
    - 테스트 수행 시 특정 쿼리를 동작시키는 애너테이션
- Table Truncate
    - 테이블을 조회하여 각 테이블을 Truncate시켜주는 방법
    
### 인수 테스트 리팩터링
- 중복 코드 처리
- 지하철역과 노선 인수 테스트를 작성하면서 중복되는 부분이 발생하는데 이 부분을 리팩터링 하면 부가적인 코드는 테스트로부터 분리되어 테스트에 조금 더 집중할 수 있게됨

### JsonPath
- Json 문서를 읽어오는 DSL
- JsonPath를 사용하면 Response Dto 객체로 받을 필요 없이 필요한 값만 추출하여 검증에 사용할 수 있음

### 요구사항
 - 인수 테스트 주도 개발 프로세스에 맞춰서 기능을 구현
    1. 요구사항 설명을 참고하여 인수 조건을 정의
    2. 인수 조건을 검증하는 인수 테스트 작성
    3. 인수 테스트를 충족하는 기능 구현
 - 인수 조건은 인수 테스트 메서드 상단 주석에 작성
 - 인수 테스트의 결과가 다른 인수 테스트에 영향을 끼치지 않도록 인수 테스트를 서로 격리
 - 인수 테스트의 재사용성과 가독성, 그리고 빠른 테스트 의도 파악을 위해 인수 테스트를 리팩터링

## 기능 요구사항
### 지하철역 인수 테스트를 완성
- [X] 지하철역 목록 조회 인수 테스트 작성
- [X] 지하철역 삭제 인수 테스트 작성

### 지하철노선 생성
- [X] When 지하철 노선을 생성
- [X] Then 지하철 노선 목록 조회 시 생성한 노선 찾기

### 지하철노선 목록 조회
- [X] Given 2개의 지하철 노선을 생성
- [X] When 지하철 노선 목록을 조회
- [X] Then 지하철 노선 목록 조회 시 2개의 노선을 조회 확인

### 지하철노선 조회
- [X] Given 지하철 노선을 생성하고
- [X] When 생성한 지하철 노선을 조회
- [X] Then 생성한 지하철 노선의 정보를 응답 확인

### 지하철노선 수정
- [X] Given 2개의 지하철 노선을 생성
- [X] When 생성한 지하철 노선을 수정
- [X] Then 해당 지하철 노선 정보는 수정 확인

### 지하철노선 삭제
- [X] Given 지하철 노선을 생성하고
- [X] When 생성한 지하철 노선을 삭제
- [X] Then 해당 지하철 노선 정보는 삭제 확인

### 지하철노선 환승역 고려
- [X] 역(1) : 노선(M), 환승

### 추가 기능
- [X] 공통로직 추출
- [X] 테스트간의 처리방법
- [X] API 명세 예제처럼 동작하는지 확인

### API 명세
```
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}
```

### 구간
 - 노선 등록 시 자동으로 두 종점간에 간선(section)추가

### 구간 등록시 고려 사항
 - [X] 역 사이에 새로운 역을 등록할 경우 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
 - [X] 새로운 역을 상행 종점으로 등록할 경우
 - [X] 새로운 역을 하행 종점으로 등록할 경우
 - [X] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
 - [X] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
 - [X] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

### 지하철 구간 제거 request API
```
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```
 
### 구간삭제
 - [X] 종점이 제거될 경우 다음으로 오던 역이 종점
 - [X] 중간역이 제거될 경우 재배치하며 거리는 두 구간의 합으로 설정

### 구간 삭제시 고려 사항
 - [X] 노선의 첫번째 역 삭제 고려
 - [X] 노선의 마지막 역 삭제 고려
 - [X] 구간이 하나인 경우 제거 불가능
 
 