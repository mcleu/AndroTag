#include <TimerOne.h>


#define IR 10
#define sensor 2

void setup(){
  pinMode(IR, OUTPUT);
  pinMode(sensor, INPUT);
  Serial.begin(9600);
  Timer1.initialize();
  //Timer1.setPeriod(13);
  //Timer1.attachInterrupt(cbf);
  attachInterrupt(0,hi,FALLING);
}

long nextmsg = 0;
volatile int hit = 0;
volatile int num1 = 0;
volatile int num2 = 0;
volatile long diff = 0;
volatile long buffer[10] = {0,0,0,0,0,0,0,0,0,0};
void loop(){
  /*
  long time = millis();
  if (time > nextmsg){
    Serial.println(analogRead(sensor));
    nextmsg = time + 200;
  }*/
  if (hit){
    int i;
    for(i = 0; i<10; i++){
      Serial.print((int) ((float) buffer[i]/562+0.5)); 
      Serial.print(' ');
   }
    Serial.println(' ');
    hit = 0;
  }
  
}


volatile int s = 0;
void cbf(){
  s = !s;
  if (s)
    digitalWrite(IR,HIGH);
  else
    digitalWrite(IR,LOW);
}


volatile long last = 0;
volatile int ind = 0;
void hi(){ 
  diff = micros()-last;
  last = micros();
  hit = 1; 
  
  if (diff >20000){
    int i;
    buffer[0] = 0;
    buffer[1] = 0;
    buffer[2] = 0;
    buffer[3] = 0;
    buffer[4] = 0;
    buffer[5] = 0;
    buffer[6] = 0;
    buffer[7] = 0;
    buffer[8] = 0;
    buffer[9] = 0;
    ind = 0;
  } else {
    ind++;
  }
  
  if (ind>=10)
    buffer[10] = 999;
  else
    buffer[ind] = diff;
  
  
}
  
