#include <TimerOne.h>
#include <Bounce2.h>

#include "ir_sender.h"
#include "ir_receiver.h"

// Pin definitions
#define PIN_TRIGGER 13


/* Buttons (debounced) */
#define DEBOUNCE_INTERVAL_MS 5
Bounce but_trigger = Bounce();


void setup(){
    // Initialize IR sending functions
    ir_init();
    ir_recv_init();
  
    // Init buttons
    pinMode(PIN_TRIGGER,INPUT_PULLUP);
    but_trigger.attach(PIN_TRIGGER);
    but_trigger.interval(DEBOUNCE_INTERVAL_MS);
    
    Serial.begin(9600);
}

unsigned long packet;
unsigned long last = 0;
boolean sw = false;

void loop(){
    
  // Check buttons
  but_trigger.update();
  if (but_trigger.fell()){
    if (sw)
        ir_send_packet(1,1,2,1,1,1);
    else
        ir_send_packet(1,1,1,1,3,1);
    sw = !sw;
  }
  
  // Check packets received
  packet = ir_pop_packet();
  if (packet != NO_PACKET){
      Serial.print("RECV: ");
      Serial.println(packet,BIN);
  }
  
  
}

    
