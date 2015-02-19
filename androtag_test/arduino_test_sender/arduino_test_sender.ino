#include <TimerOne.h>
#include <Bounce2.h>

#include "ir_functions.h"

// Pin definitions
#define PIN_TRIGGER 13


/* Buttons (debounced) */
#define DEBOUNCE_INTERVAL_MS 5
Bounce but_trigger = Bounce();


void setup(){
  // Initialize IR sending functions
  ir_init();
  
  // Init buttons
  pinMode(PIN_TRIGGER,INPUT_PULLUP);
  but_trigger.attach(PIN_TRIGGER);
  but_trigger.interval(DEBOUNCE_INTERVAL_MS);
  
  Serial.begin(9600);
}

void loop(){
  but_trigger.update();
  if (but_trigger.fell()){
    ir_send_packet(1,1,1,1,1,1);
  }
  
}
