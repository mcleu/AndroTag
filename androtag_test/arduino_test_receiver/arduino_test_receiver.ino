#include <TimerOne.h>


#define IR 10
#define sensor 2

#define TIMEADJ (2*13*25)


void setup(){
  pinMode(IR, OUTPUT);
  pinMode(sensor, INPUT);
  Serial.begin(9600);
  Timer1.initialize();
  //Timer1.setPeriod(13);
  //Timer1.attachInterrupt(cbf);
  attachInterrupt(0,ir_sense_isr,FALLING);
}

long nextmsg = 0;
volatile int hit = 0;
volatile long diff = 0;
volatile long buffer[10] = {0,0,0,0,0,0,0,0,0,0};
void loop(){
  if (hit){
    int i;
    for(i = 0; i<10; i++){
      Serial.print((int) ((float) buffer[i]/TIMEADJ+0.5)); 
      //Serial.print(buffer[i]); 
      Serial.print(' ');
   }
    Serial.println(' ');
    hit = 0;
  }
  
}

/*
unsigned long get_packet(int[] buf){
  int i;
  unsigned long packet = 0;
  for (i = 1; i<10; i++){
    if (!buf[i]){
      return packet;
    }
    
  }
}*/


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
void ir_sense_isr(){ 
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
  
