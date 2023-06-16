# Importing required libraries
from datetime import datetime  # To set date and time

import pygame
import time
import pyrebase
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
StepPins = [12,16,20,21]
trig = 23 #초음파 발생핀 
echo = 24 #초음파 수신핀
led_s = 18 #led_s 약 배출 여부
led_h = 4 #led_s 약 존재 여부 

GPIO.setwarnings(False)      # 경고문이 뜨지 않도록 설정
   
GPIO.setup(led_s, GPIO.OUT)    # 약 배출 여부를 알려줄 파란색 LED를 OUT 모드로 설정
GPIO.setup(trig, GPIO.OUT)      # 초음파가 발사할 트리거 핀을 OUT 모드로 설정
GPIO.setup(echo, GPIO.IN)       # 초음파가 튕겨져 나온것을 입력받을 에코 핀을 IN 모드로 설정
GPIO.setup(led_h, GPIO.OUT)     # 약 존재 여부를 알려줄 빨간색 LED를 OUT 모드로 설정
for pin in StepPins:         # 배열로 선언해놓은 핀번호들을 for문을 이용해 설정.
  GPIO.setup(pin,GPIO.OUT)
  GPIO.output(pin,False)
 
StepCounter = 0         # 스텝 수를 나타낼 변수
RocateCount=0         # 40도 회전 수를 나타낼 변수
StepCount = 4         # 필요 스텝 수를 나타낼 변수

Seq = [[0,0,0,1],      # 전기를 인가 할 위치 데이터를 배열로 선언함
      [0,0,1,0],      # 전자석 방식으로 모터가 회전하기 때문에 
      [0,1,0,0],      # 전기를 순서대로 인가하여 회전을 하도록 설정.
      [1,0,0,0]]

#DB info 
################## 파이어베이스 ##################

# config 변수에 파이어베이스의 키값, 주소값 등의 정보를 넣어둠
config = {
  "apiKey": "AIzaSyD8OB3BPWCWo49ot5oSizkQZSoZHSHGHMM",
  "authDomain": "my-application-8d0dd.firebaseapp.com",
  "databaseURL": "https://my-application-8d0dd-default-rtdb.firebaseio.com",
  "projectId": "my-application-8d0dd",
  "storageBucket": "my-application-8d0dd.appspot.com",
  "messagingSenderId": "1096225180796",
  "appId": "1:1096225180796:web:a4aedbefe5b52703a1cdf9",
  "measurementId": "G-E6J06BKGZE"
};

firebase = pyrebase.initialize_app(config)    # 위에서 선언했던 config 변수를 매개변수로 firebase 변수에 pyrebase를 구동 정보를 입력
storage = firebase.storage()                # storage에 우리의 파이어베이스 저장소 정보를 입력
database = firebase.database()                # database에 우리의 파이어베이스의 DB 정보를 입력

print("start")




#init
pygame.mixer.init()

# alarm_time 을 매개변수로 들고와 각각 시각, 분, 초로 나누고
# 그것과 같은 시간일 때 알람이 울리도록 하는 함수 .
def checkTime(alarm_time):
   alarm_hour = alarm_time[0:2] # alarm_time 에서 시각을 추출
   alarm_min = alarm_time[3:5]  # alarm_time 에서 분을 추출
   alarm_sec = alarm_time[6:8]  # alarm_time 에서 초를 추출
   alarm_period = alarm_time[9:].upper()   
   now = datetime.now()
   
   current_hour = now.strftime("%I")
   current_min = now.strftime("%M")
   current_sec = now.strftime("%S")
   current_period = now.strftime("%p")
   
   if alarm_period == current_period:
      if alarm_hour == current_hour:
         if alarm_min == current_min:
            if alarm_sec == current_sec:
               return True
   return False


# 약이 배출되었을 때 벨소리를 재생시키는 함수 .
def musicPlay():

   #load file
   pygame.mixer.music.load("/home/pi/pill_out.mp3")   
   #play
   pygame.mixer.music.play()
   
   #끝까지 재생할때까지 기다린다.
   while pygame.mixer.music.get_busy() == True:
      continue

# 모터를 회전 시키는 함수 .
def rocateMotor():
   global RocateCount          # 위에서 선언했던 변수들을 그대로 들고옴.
   global StepCount            
   global StepCounter          
   global Seq                # 위에서 선언했던 스퀀스 배열을 들고옴.
   if RocateCount % 9 == 8:    # 스테핑 모터의 기계적 이유로 인해
      for j in range(0, 224) : # 딱 40도 회전이 불가능 따라서 마지막회전 때 좀 더 덜 회전을 하여 각도를 맞춤.
         for pin in range(0, 4): 
            xpin = StepPins[pin]         # 위에서 선언했던 스테핑모터의 4개핀을 한번에 하나 씩 들고옴
            if Seq[StepCounter][pin] != 0:    # 현재 시퀀스에 맞게 전기를 인가함.
               GPIO.output(xpin, True)       # ex step pin 12,16,20,21 에서 현재 12 번을 들고옴
            else:                             # 현재 시퀀스는 0,0,0,1 을 들고옴
               GPIO.output(xpin, False)      # 그럼 12는 0 이기 때문에 false
                                    # 그다음 핀을 들고옴 16 이것또 false
         StepCounter += 1               # 시퀀스가 끝이나면 스텝카운터를 1증가 
                                    
         if (StepCounter == StepCount):      # 시퀀스가 만약 스텝카운터(4) 와 같다면
            StepCounter = 0               # 즉 모든 시퀀스를 실행하였다면 0으로 초기화
         if (StepCounter > 0):            # 그렇지 않다면 
            StepCounter = StepCount         # 스텝카운트를 그대로 둠.
         time.sleep(0.01)
      RocateCount+=1                     # 위 과정들을 모두 거쳤다면 40도 회전수를 1증가 시킴.
      print("약 다 먹엇습니다")
   else:
      for j in range(0, 228):               # 위 9번째 회전 코드와 첫번째 for문의 조건만 다르고 나머지는 동일코드
         for pin in range(0, 4):
            xpin = StepPins[pin]
            if Seq[StepCounter][pin] != 0:
               GPIO.output(xpin, True)
            else:
               GPIO.output(xpin, False)
         StepCounter += 1
         if (StepCounter == StepCount):
            StepCounter = 0
         if (StepCounter > 0):
            StepCounter = StepCount
         time.sleep(0.01)
      RocateCount+=1
   time.sleep(0.5)


def pillout():   # 초음파 동작 코드
   GPIO.output(trig, False)   # 
   time.sleep(0.5)
   
   GPIO.output(trig, True)
   time.sleep(0.00001)
   GPIO.output(trig, False)
   
   while GPIO.input(echo) == 0:   # echo에 인풋이 없을 때를 시작으로 간주
      pulse_start = time.time()   # pulse_start 변수에 시작 시간을 저장
      
   while GPIO.input(echo) == 1:   # echo 변수에 초음파가 팅겨져나와 인풋이 들어오면 끝으로간주
      pulse_end = time.time()      # pulse_end 변수에 끝난 시간을 저장.
   
   pulse_duration = pulse_end - pulse_start   # 두 변수의 차이를 계산하여 시간을 이용해 거리를 계산 (거리 = 시간 * 속도)
   # 그렇게 얻어낸 거리를 아래 계산을 통하여 실제 우리가 사용하는 단위로 환산   
   distance = pulse_duration * 17000         
   distance = round(distance, 2)
   
   print('Distance : ', distance, 'cm')
   
   if distance <= 10:   # 만약에 10cm 이내로 들어왔다면 파란색 LED를 점등.
      GPIO.output(led_s, True)
      time.sleep(0.5)
      result = "Y"   # 파이어베이스에 업데이트할 데이터를 result 변수에 대입
      #init
      
   else:   # 위 조건을 만족하지 못한다면 파란색 LED를 소등.
      time.sleep(0.3)
      GPIO.output(led_s, False)
      result ="N"      # 파이어베이스에 업데이트할 데이터를 result 변수에 대입
   return result   # result 변수를 리턴.
# 프로그램 메인 코드 


isFirst = True # 약이 다 떨어졌을 때 음악을 재생하기 위한 조건 변수.




while True:
   
   root = database.child("Give_pill")      # 최상위 데이터베이스의 아들노드인 Give_pill을 들고옴
   state = root.child("should").get()      # Give_pill의 아들 노드인 should를 들고옴.
   
   
   index = database.child("차례").child("번호").get()   # 최상위 데이터베이스의 아들노드(차례)의 아들노드(번호)를 들고옴.
   a = str(state.val())   # 현재 파이어베이스에 약 배출 조건을 들고옴
   b = str("Y")         # 비교 변수.
   if(int(index.val()) > 8 ):   # 현재 번호가 8번 이상이라면 
      GPIO.output(led_h, True)   # 약이 떨어졌음을 알리는 빨간색 LED를 킴
      if isFirst :            # 약이 떨어졌음을 한번만 알리기위한 isFirst 변수를 사용
         # 음악재생코드
         #load file
         pygame.mixer.music.load("/home/pi/no_pill.mp3")
         #play
         pygame.mixer.music.play()
         #끝까지 재생할때까지 기다린다.
         while pygame.mixer.music.get_busy() == True:
            continue
         isFirst = False   # 이 코드가 끝나기전에 첫번째 임을 알리는 isFirst 변수를 false로 바꿈.
   else :# 번호가 9 번이 이상이 되지 않았을 때 실행
      if isFirst == False :   # 약이 다시 채워졌기 때문에 isFirst 변수를 True로 초기화 
         isFirst = True       
      GPIO.output(led_h, False)
      if a == b:   # a ( 현재 파이어베이스 데이터 ) == b ("Y") 라면 아래 코드 실행 즉 약을 꺼내야하는 상황이면 실행
         database.child("Give_pill").update({"should": "N"})   # 파이어베이스 데이터에 Y 를 N으로 바꿈
         s_index = "칸" + str(index.val()) # 칸(index)을 string 형으로 변수 초기화   ex) 칸1 칸2
         database.child("약통 현황").update({s_index : "x"}) # 약통현황에 현재 칸의 데이터를 o -> x로 바꿈
         database.child("차례").update({"번호" : str(int(index.val()) + 1) }) # 그리고 차례 번호를 1 증가시킴.
         rocateMotor()      # 모터 회전 코드 실행
         musicPlay()         # 음악실행 코드 실행
         time.sleep(1)
   result = pillout()   # pillout 함수에서 리턴한 데이터를 result 변수에 대입

   database.child("DB object name")
   data = {"key1": result}   # DB Object name . key1 에 result 데이터를 세팅함.
   database.set(data)