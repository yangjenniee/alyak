# Smart Medicine Cabinet (알약 스마트 비서) 

* IoT 프로그래밍 수업에서 3인으로 수행한 팀 프로젝트입니다.(2022.6 - 2022.12)

<br>목차
-------------
- [Smart Medicine Cabinet (알약 스마트 비서)](#smart-medicine-cabinet-알약-스마트-비서)
  - [목차](#목차)
  - [프로젝트 개요](#프로젝트-개요)
  - [사용 스킬](#사용-스킬)
  - [데이터셋 구성도](#데이터셋-구성도)
  - [결정 모델 학습결과](#결정-모델-학습결과)
  - [상세 설계](#상세-설계)
  - [결정 모델 학습결과](#결정-모델-학습결과)
  - [결과 이미지](#결과-이미지)


## <br>프로젝트 개요

(1) **주제 선정 이유/필요성**

- 청소년 모바일(스마트폰) 디바이스 증가 추세로 유해 컨텐츠에 접근 되기 쉬움
- 청소년의 일상과 매체이용은 필연적으로 연결되어 있음
- 실제 청소년의 50%이상이 하루 평균 최소 3시간 이상 디지털 매체를 사용하고 있음.
- 디지털 매체는 청소년들에게 실질적 의미나 유용성은 거의 없는 상태이다.
- 초등학생들이 제일 먼저 접하고, 피해가 크가도 생각하는
 유해메체물의 미성년자 제한 등급제는 실효성이 낮았음.
- 무기 모자이크의 경우 칼은 대부분 모자이크 되지만 총기는 그대로 노출되는 경우가 많다

(2) **개념 설명**
-  yolov5모델로 학습된 결과를 토대로 무기 인식 후 해당 객체를 openCV의 rectangle를 이용하여 해당 매체를 가린다.

(3) **목표**
- 위험도가 높은 총기에 대한 환상을 낮추고, 총기에 대한 경각심을 설립
- 디지털 유해매체환경으로부터 근본적으로 청소년을 보호하고, 장기적으로 국가를 위한 건전한 성장동력으로 청소년들이 교육될 수 있는 토대를 마련
- 미성년자 제한 등급제의 낮은 실효성을 개선함
- 폭력물 등 중독성이 강한 컨텐츠에 중독된 청소년의 교화를 촉진시킴


## 사용 스킬
* yolov5
* openCV 
* google colab

  
 ## 데이터셋 구성도
 
|원본 영상|
|------|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/df48a3de-2d2a-48d1-bfa3-bb64d2c322e5)|
|원본 영상의 같은 경우 총이나 칼같은 공격성을 띄는 무기가 영상에 노출되고 있다.  |

<br>

|차단 영상|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/d3dc21c2-8aec-4465-b1fd-864b9ac9477b)|
|초기 구상은 무기 뿐만 아니라 혈흔, 욕설 등도 필터링 되게 하는 것으로 구성을 하였다. 하지만 혈흔 같은 경우 색깔이 오직 빨간색이면 block 될 것이라는 우려가 있었고, 영상기반으로 학습시키는 것이 이번 프로젝트의 주된 목표라고 교수님이 말씀하신 것을 토대로, 영상 데이터를 기반으로 목표를 수정하였다. 그리고 촉박한 기한으로 여러 가지 종류의 경우를 필터링 하는 것은 불가하다고 생각하여서 총기, 칼로 레이블을 최소시켰다. |

<br>

|데이터셋 구성도 - image폴더|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/89bc8927-a3bd-414f-8707-fb2aa6703aa0)|
|3108개의 항목이 있고 , 총기 image 3000장 칼 이미지 108장으로 구성되어 있다.|

<br>

|데이터셋 구성도 - lables폴더|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/4244e7d5-db4b-4e56-bca5-66f25f1261a7)|
|3108개의 항목이 있고 , 총기 image 3000장 칼 이미지 108장으로 구성되어 있다.|
|칼 같은 경우 총기에 비해 데이터가 적은 점을 고려하여 boundary box 방식이 아닌 polygon형식으로 label을 저장하였다|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/f4f8e2b4-1f86-40dd-94d1-dd2479854cc5)|
|클래스를 0으로 지정하였고 X_center,Y_center,width,height순이다.|

<br>

|val 폴더 (valid 관련 데이터)-images 폴더|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/609d0283-0eb3-428c-b759-59695b7dd9e2)|
|총 452개의 항목이 있고, 총기 300개 칼 152개가 들어가있다.|

<br>

|test 폴더(test 관련 데이터)-images 폴더|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/7ca173c5-39fd-48c1-b7c9-a1847c36eaab)|
|총 401장이고, 총기 400장, 칼 101장으로 이루어져있다.|

## 상세 설계

- 전체적 동작
주된 목적은 칼과 총을 인식하고 즉, 유해컨텐츠를 인식한다.
파일을 업로드 하면 훈련된 데이터를 기반으로 필터린 된 영상이 제작된다. 

![image](https://github.com/yangjenniee/alyak/assets/92010971/4b526668-e96f-48a0-a89e-14d6d544a7bc)

비디오 업로드 뿐만 아니라 유튜브 링크로도 총기와 칼을 인식하게 한다.:
이는 detect.py의 파라미터를 적극 활용하면 가능할 것으로 보였다.

총기와 칼을 인식하고 해당 물체를 필터링한다.
필터링 방법은 cv2를 import 하여 메서드를 이용할 것인데,
이는 mosaic 보다 더 확실하게 매체를 차단시킬 수 있는 White box로 해당 물체를 fill out 시킨다. 
이는 해당 물체를 모자이크 하는 것보다 더 확실한 필터링 방법이라고 생각하였다.

크지 않은 모델인 yolov5s를 이용해도 상관없을 것이라고 판단하여 yolov5s를 선택하였다. 
batch는 16, 이미지는 기본 default 값인 416을 해주었다. image 값 같은 경우 다른 파라미터를 넣었을 때 에러가 났다. 따라서 기본 default 값인 416을 주었다.


## 결정 모델 학습결과 

|batch 16 epochs 200 img 크기 416 val_ loss - train 셋 loss |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/f44af302-cffe-4cf0-be2b-5a0838c64ed4)|
|box loss 같은 경우 0,018, cls loss의 경우 0,0169 obj loss 의 경우 5.6938e 정도이다. 
과적합은 일어나지 않은 것으로 보이며 나쁘지 않은 결과였다. 
오히려 더 학습을 시켜도 괜찮을 것 같았다. |


<br>

|batch 16 epochs 200 img 크기 416 val_ loss - var 셋 loss |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/6194d626-24a7-4444-8d61-7d95aa3727b7)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/422f0765-2c06-46d8-83fb-eca0c74149ef)|
|box loss의 경우 0.2829 cls_loss의 경우 0.0102 obj_loss의 경우 7.4792e-3으로 나왔다. 
box 로스의 경우 안정적이지만 다른 그래프는 조금 불안전하지만 로스가 계속 깎이는 것을 볼 수 있다. 
값이 계속 줄어드는 것으로 보아 오버피팅은 일어나지 않은 것으로 보였다. |


## 결과 이미지 

|영화 (자연영상)을 넣었을 때 필터링 |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/a4e90d4c-0cf5-425b-be6a-98cf57013da7)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/77c9415c-bf3a-4211-8a39-d96e4fd8f989)|
|동영상에 있는 칼과 총을 정확히 인식하고, 해당 객체를 white box로 필터링이 되었다.|

<br>

|3D 영상을 넣었을 때 필터링 |
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/c4002cd5-3fb4-45c1-9cd6-59694810e3cc)|
![image](https://github.com/yangjenniee/alyak/assets/92010971/496a1f72-a831-47b6-b629-d8de9eca9402)|

<br>

|비정상 동작|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/00bb0142-b419-458e-b35f-cacf8d0aaba7)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/c392bd25-5de3-455e-b841-84c5924cdbbb)|
|동영상 같은 경우 워낙 많은 프레임이 있다보니 인식이 풀릴 때도 있었고
화질이 안좋아서 인식이 못하는 경우도 있었다. 
그리고 격렬한 싸움씬 같은 경우에도 잘 인식하지 못하였다.|

<br>

|비정상 동작|
|-----|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/58030e2d-2a81-4afb-81b3-7f6c817fd3df)|
|![image](https://github.com/yangjenniee/alyak/assets/92010971/ca134982-b8c0-4b5a-ac13-dd3633bb0400)|
|전체가 출력되지 않은 총구는 인식 하지 못한다.
그리고 동영상 같은 경우 워낙 많은 프레임이 있다보니 인식이 풀릴 때도 있다. |


