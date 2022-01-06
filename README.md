![header](https://capsule-render.vercel.app/api?type=soft&color=F7F3E9&height=120&text=BRRING&&animation=fadeIn&&fontSize=40)

# BRRING (Bring + Ring)
------------
### ✔프로젝트 주제
> 실시간 최저가 알림 및 공동 구매 애플리케이션 BRRING 개발

### 🧨프로젝트 개요
> 기존의 최저가 비교 서비스들은 실시간 가격 변동에 대한 알림이나 최저가 변동 추이를 알려주지 않기 때문에 합리적인 소비를 하는데 어려움이 있었다. 그래서 다나와닷컴의 최저가 정보를 크롤링하여 최저가 정보를 Firebase Realtime Database를 이용하여 저장하고 이를 그래프로 보여주며, 찜한 상품에 대하여 최저가 알림을 주는 애플리케이션을 제작하였다. 또한 최저가 제품의 특성상 한번에 많은 양의 제품을 묶어서 판매하기 때문에 공동 구매가 가능한 기능까지 추가하였다.

### 🗒프로젝트 구조
>
> &nbsp;&nbsp;&nbsp;&nbsp;![structure](https://user-images.githubusercontent.com/51983113/148374867-ed3f15f7-e7d3-4719-96aa-9fe04d936a14.png)
>
> 1. EC2 서버 위에서 Python과 Beautiful Soap, Selenium 라이브러리를 통해 다나와닷컴에서 최저가 정보 및 제품 정보를 크롤링한다. 
> 2. 크롤링한 데이터 중 제품 정보 및 가격 데이터는 Firebase의 Firestore와 Realtime Database를 통하여, 검색 결과는 Flask를 이용한 API를 통해 직접 애플리케이션으로 전달한다. 이 과정에서 캐싱을 통해 데이터 전달 속도를 높였다.
> 3. Kotlin으로 구현된 애플리케이션은 2번의 방법으로 받아온 데이터들을 이용하여 제품 정보 및 최저가와 검색 결과를 사용자에게 보여준다.

### 📖 애플리케이션 기능

> 1. 검색 기능을 활용한 원하는 제품 검색
> 2. 제품에 대한 현재 최저가 및 추이 확인
> 3. 공동구매 기능을 이용한 지역별 공동구매


### 🖥 애플리케이션 UI
>
> &nbsp;&nbsp;&nbsp;![login](https://user-images.githubusercontent.com/51983113/148375620-b912682e-5c02-4ab2-82e9-673ad5a2c7b3.png)
> &nbsp;&nbsp;&nbsp;![main](https://user-images.githubusercontent.com/51983113/148375624-f4a70332-185b-4787-92c6-1effc4b120e1.png)

> 1. Login 화면: 애플리케이션 자체 계정 또는 Google 계정을 이용하여 로그인 할 수 있다.
> 2. Main 화면: 추천 상품 및 최근 검색 상품 등에 대한 전반적인 정보를 받아볼 수 있다.

>&nbsp;&nbsp;&nbsp;![search_1](https://user-images.githubusercontent.com/51983113/148375928-457ab17a-639b-4949-bf9e-a4d6164e64f6.png)
>&nbsp;&nbsp;&nbsp;![search_2](https://user-images.githubusercontent.com/51983113/148378411-f44dbf30-d26c-453f-95a2-7b25b4c542f9.png)

> 1. 검색창: 검색창을 눌러 검색을 할 수 있다.
> 2. 검색 결과: 검색어에 대한 결과를 확인할 수 있다.

>&nbsp;&nbsp;&nbsp;![product](https://user-images.githubusercontent.com/51983113/148376127-b7bfcb98-dcb1-4c57-9e66-dfef69b9ced7.png)
>&nbsp;&nbsp;&nbsp;![product_2](https://user-images.githubusercontent.com/51983113/148376134-757a34c1-ff22-4e2f-8b0a-34312794126a.png)
>1. 제품 정보: 제품 사진 및 최저가 정보를 확인할 수 있다.

>&nbsp;&nbsp;&nbsp; ![board_1](https://user-images.githubusercontent.com/51983113/148376150-10c3dec7-66a1-49be-ad82-d01e5442c1e0.png)
>&nbsp;&nbsp;&nbsp;![board_2](https://user-images.githubusercontent.com/51983113/148376151-7e85d5c3-f2cf-4c6e-858e-d83169330550.png)
>&nbsp;&nbsp;&nbsp;![board_3](https://user-images.githubusercontent.com/51983113/148376156-f072934c-8eb5-4ff1-925c-4a328b124cdb.png)
>&nbsp;&nbsp;&nbsp;![board_4](https://user-images.githubusercontent.com/51983113/148376160-2541ca28-9621-4aa5-bc8b-3288808a233b.png)
> 1. 공동구매 게시판: 사용자의 지역에 맞는 게시판을 선택할 수 있다.
> 2. 게시판 글 목록: 해당 게시판에 업로드되어 있는 게시글을 확인할 수 있다.
> 3. 공동구매 글 확인: 게시글을 클릭하여 필요한 공동구매를 진행할 수 있다.
> 4. 게시글 작성: 사용자가 공동구매를 원하는 제품에 대한 게시글을 작성할 수 있다.

### 🎥 시연영상 링크
> https://www.youtube.com/watch?v=BNrxaMuk2iw

### 🧑 Notion 링크
>https://milkymoon.notion.site/Brring-8fb968a483af4e68a02e54bb2dfa379f

------------
### 팀원소개
#### 이준하 (컴퓨터학부 심화컴퓨터전공 4학년)   
#### 김도형 (컴퓨터학부 심화컴퓨터전공 4학년)   
#### 이예진 (컴퓨터학부 글로벌소프트웨어전공 4학년) 
#### 황지현 (컴퓨터학부 심화컴퓨터전공 4학년)   
------------
