# Smart Medicine Cabinet (알약 스마트 비서) 

* IoT 프로그래밍 수업에서 3인으로 수행한 팀 프로젝트입니다.(2022.6 - 2022.12)

<br>목차
-------------
- [Smart Medicine Cabinet (알약 스마트 비서)](#smart-medicine-cabinet-알약-스마트-비서)
  - [목차](#목차)
  - [프로젝트 개요](#프로젝트-개요)
  - [사용 스킬](#사용-스킬)
  - [기능 소개](#기능-소개)
  - [임베디드 요소](#임베디드-요소)
  - [상세 설계](#상세-설계)
  - [구조](#구조)
  - [구현 이미지](#구현-이미지)


## <br>프로젝트 개요

1. IT 기술 동향
  - IoT 기반 스마트 헬스케어의 서비스 증가
  - Smart Home 관련 서비스 증가 추세
  - 모바일(스마트폰) 디바이스 증가 추세
2. 기술 측면
  - 중요 센서를 사용한 알림 서비스 제공
  - OCR 기술 데이터 + DB를 이용하여 부가서비스 제공
3. 목표
  - 복약지도가 힘든 환자를 지속적으로 **모니터링** 하여 케어하고, 
  - **보호자가 환자의 복약 상태** 를 알기 쉽도록 돕고, 
  - **복약에 필요한 데이터를 제공** 함으로써 환자의 안전한 복용과 건강한 생활을 도움



## 사용 스킬
* Android Studio - Java 
* Firebase
* Raspberry Pi 4
  

## 임베디드 요소
* Raspberry pi 
* 스테핑모터
* 브레드보드
* 초음파센서
* RED LED 
* BLUE LED 
* speaker

## 기능 소개 

* 약이 나오고, 약이 감지되고, 약통이 비는 것 즉, 알약에 대한 전체적 데이터를 실시간 파이어 베이스에 업로드 한다.
* 알약이 나와야 할 순간이 되면 파이어베이스 값이 N->Y로 바뀌어서 스테핑 모터가 돌아가는 형태를 취한다.
* 모터 각을 계산하여 칸은 8칸으로 정한다.
* 각도가 다 돌아서 360가 되면 약통이 비었다고 하드웨어, 소프트웨어에서 알람이 울린다.
* 약 정보를 알고 싶을 땐, 처방전을 찍어서 OCR을 이용하여 파이베이스에 올린 후 이 데이터를 가지고 식약청 DB를 크롤링하여 복용정보를 알아낸다.
* 올바른 복용이 목표이므로 약이 나와도 알약을 복용하지 않으면 꾸준히 알람이 가게한다.

## 상세 설계
|HW 설계|
|------|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/a530415d-1d48-4dc9-b2e6-af5bc14e25ac)|
|약통과, 약칸은 박스와, 폼보드를 이용하여 제작한다.
스테핑모터와 우리가 만든 물레방아 형식의 약통을 본드로 결합하여 같이 돌아가게 한다.
약 칸 중 하나는 아래가 비어있고, 약 미끄럼틀도 약이 미끄럼틀을 통해 약이 약출구로 배출된다.
Raspberry pi1,2 둘 다 파이어베이스와 연결되어 있다. 
라즈베리파이1은 LED, 초음파센서, 스피커, 스테핑모터를 연결한다. 
라즈베리파이2는 Respeaker을 통해 음성인식을 구현한다. 
약 통 위에 보이도록 LED(빨강,파랑)을 연결한다. 빨강 LED는 약통 충전을 알리는 것이고, 파란색 LED는 약 복용을 알리게끔 구현한다. |
|![image](https://github.com/yangjenniee/alyak/assets/92010971/532d4b54-7800-4f49-9870-ba71093de408)|
|약통을 보고 시계방향 혹은 반시계방향으로 돌아가며 한조각 밑바닥을 제거하여약품이 아래로 떨어지고 그 약품이 약품 출구로 나갈 수 있는 방식으로 설계를 하는 것이 좋겠다고 생각했다. |


<br>

|SW 설계|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/e44cb77c-780e-47ae-8ff1-a85823c2dd83)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/dbe7a301-87b7-4cbf-839b-101c77cd671e)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/352130fe-3890-4907-8050-13ef52085d87)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/1eea8cbc-d085-4215-85f2-c8c5001aba48)|


## 구조 

|전체적 구조|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/6913a7ef-f80f-4146-be8c-884bff09e2bf)|

<br>

|내부 구조|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/3b11b681-2cef-4784-a688-44afbbc65786)|





## 구현 이미지 

|동작 1) 어플로 시간 설정 후 약 배출 – 초기 화면|
|-----|
|<img src="https://github.com/yangjenniee/alyak/assets/92010971/459686d9-f4eb-4c8b-8246-a56eac823cc3" width="500" height="500"/>
<br>


|동작 1)어플로 시간 설정 후 약 배출 – 알람 설정 화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/dd0695ca-14fd-4a98-81a5-e7e74ed11972)|

<br>

|동작 1) 어플로 시간 설정 후 약 배출 – 알람 완료 화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/05aaee73-d148-4d01-acba-49b3532d003a)|

<br>


|동작 1) 어플로 시간 설정 후 약 배출 – 16:51분 경 파이어베이스 현황|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/ea2ebab9-9687-4ab2-b67e-b5667b5093cb)|

<br>


|동작 1) 어플로 시간 설정 후 약 배출 – 16:51분 경 약 배출|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/ea2ebab9-9687-4ab2-b67e-b5667b5093cb)|



<br>

|동작 1) 어플로 시간 설정 후 약 배출 – 어플 알람기능|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/4034def9-a82b-4ceb-b977-3eacc3649a0e)|

<br>

|동작 2) 어플로 터치 했을 때 알약 배출 - 초기화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/c50f7eb7-c767-4c5d-b9f8-d49101755501)|


<br>

|동작 2) 어플로 터치 했을 때 알약 배출 – 알약 배출 시 파이어베이스|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/8b70de2b-6174-45be-8d90-f0ca6e819e19)|
|칸2의 약이 배출되어 x로 표기 변경됨|

<br>

|동작 2) 어플로 터치 했을 때 알약 배출 – 터치 후 약 배출|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/fce729e2-e84f-4b29-b935-e2a1a01d169b)|


<br>

|동작 3) 음성인식으로 알약 배출 – 음성인식 후 파이어베이스 현황|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/d0ab228b-8e2f-4460-a902-88c8bd4384c9)|
|칸3이 x로 변경됨|

<br>

|동작 3) 음성인식으로 알약 배출 – 음성인식 후 약 배출|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/76a864cd-2766-466e-956d-cb1b37f4acfc)|

<br>

|동작 3) 음성인식으로 알약 배출 – 음성인식 후 약 배출|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/76a864cd-2766-466e-956d-cb1b37f4acfc)|


<br>

|동작 4) 복용중인 약을 알고 싶을 때 - 초기화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/11f1693e-e9ce-4ef0-96f5-bf367752ef15)|


<br>

|동작 4) 복용 중인 약을 알고 싶을 때 – 처방전 찍기 |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/ea636594-8c37-4aff-a973-57f0ba2b99a0)|

<br>

|동작 4) 복용 중인 약을 알고 싶을 때 – 크롤링 결과 파이어베이스 업로드|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/e9b7527e-9e68-49e3-becc-a71182e87eab)|

<br>

|동작 4) 복용 중인 약을 알고 싶을 때 – 어플에서 약 항목 출력|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/53ef7b4c-606d-451f-8e1b-25df679faa59)|

<br>


|동작 5) 복용 약 정보를 알고 싶을 때 - 초기화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/2929ad4c-e25f-4f98-8426-e2b138bd4842)|


<br>


|동작 5) 복용 약 정보를 알고 싶을 때 - 결과화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/bcf81be4-7f7b-468b-a573-2e22b912e385)|

<br>


|동작 6) 약통의 현황을 알고 싶을 때 - 초기화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/e69749e0-6659-4a01-9291-37a1a3c43e5c)|

<br>

|동작 6) 약통의 현황을 알고 싶을 때 - 초기화면|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/a51f0294-f968-4c80-9fb0-8608c7062427)|
|약통 현황이 파이어베이스와 동기화 되어있다|

<br>


|동작 7) 약통이 비었을 때 - 파이어베이스, 어플 상황 |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/416b04b9-2f62-423b-9ea6-b70a80dbe06e)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/2ce9040d-aa89-4491-ad5b-016eb56699c6)|

<br>


|동작 7) 약통이 비었을 때 – 약통에서 알림|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/99dc6298-0cb6-48a4-9971-75fb9e16f541)|
|사진 상이라 뚜렷히 보이진 않지만 LED가 점등되고, 약이 떨어졌다는 멘트가 나옴|


<br>


|동작 7) 약통이 비었을 때 – 재투여 버튼 터치|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/2f67b233-5e9e-492c-8c57-aeee78f664c3)|


<br>


|동작 7) 약통이 비었을 때 – 재투여 버튼 터치 후|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/634e2c33-78d0-4b57-aacb-bb4dadf08d01)|
|어플과 파이어베이스의 데이터가 모두 O로 바뀐 모습(약이 채워진 상태)|

<br>


|동작 7) 약통이 비었을 때 – 재투여 버튼 터치 후|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/79408a48-9dde-4dff-afe6-042966241e7e)|
|LED 점등이 꺼진모습|

<br>


|동작 8) 약이 나왔는데도 약을 먹지 않으면 재알람 – 초기 파이어베이스 상태(약이 안나온상태)|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/06debb35-0231-4194-bc7e-1cef4674158e)|


<br>


|동작 8) 약이 나왔는데도 약을 먹지 않으면 재알람 – 약 나온 후 파이어베이스|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/a55a6627-9a3c-4e3c-acaa-3fffd8554fa5)|
|약 감지 LED 점등, 파이어베이스 해당 값 O으로 변경됨|

<br>


|동작 8) 약이 나왔는데도 약을 먹지 않으면 재알람 – 휴대폰 푸쉬알람|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/85804a9d-cec6-42f0-93b9-78968b0cd510)|
|약 이름을 설정하지 않아 null로 표기된 것임|


<br>


|동작 8) 약이 나왔는데도 약을 먹지 않으면 재알람 – 휴대폰 푸쉬알람 클릭 시 |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/05b32dd9-0d42-49a5-8e88-315eeb34c4eb)|









