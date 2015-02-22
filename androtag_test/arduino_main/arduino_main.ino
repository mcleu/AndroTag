
/* ------------------------- IMPORTS ------------------------- */

#include <Bounce2.h>
#include <TimerOne.h>
#include "serial_codes.h"
#include "gun_definitions.h"
#include "player_functions.h"
#include "ir_sender.h"
#include "ir_receiver.h"
#include "gun.h"


/* --------------------- PIN ASSIGNMENTS --------------------- */

// SERIAL
//			0        HARD WIRED
//			1        HARD WIRED

// HIT DETECTION (interruptable)
//#define PIN_BODY_RECV	2        HARD WIRED
//#define PIN_HEAD_RECV	3        HARD WIRED

// BUTTONS
#define PIN_HIT    	4
#define PIN_FIRE   	5
#define PIN_SWAP   	6
#define PIN_RELOAD 	7

// LEDS
//#define PIN_IR_FAR	8        HARD WIRED
//#define PIN_IR_NEAR	9        HARD WIRED
#define PIN_LED_BODY	A0
#define PIN_LED_GUN	A1



/* ---------------------   BUTTON SETUP   --------------------- */

#define DEBOUNCE_INTERVAL_MS  5
Bounce button_hit = Bounce();
Bounce button_fire = Bounce();
Bounce button_swap = Bounce();
Bounce button_reload = Bounce();



/* --------------------- STATIC VARIABLES --------------------- */

#define ENEMY_TEAM       1
#define ENEMY_PLAYER     1


/* ---------------------      SETUP      --------------------- */

void setup(){
    // Buttons, with pullup
    pinMode(PIN_HIT, INPUT_PULLUP);
    pinMode(PIN_FIRE, INPUT_PULLUP);
    pinMode(PIN_SWAP, INPUT_PULLUP);
    pinMode(PIN_RELOAD, INPUT_PULLUP);
    button_hit.attach(PIN_HIT);
    button_fire.attach(PIN_FIRE);
    button_swap.attach(PIN_SWAP);
    button_reload.attach(PIN_RELOAD);
    button_hit.interval(DEBOUNCE_INTERVAL_MS);  
    button_fire.interval(DEBOUNCE_INTERVAL_MS); 
    button_swap.interval(DEBOUNCE_INTERVAL_MS); 
    button_reload.interval(DEBOUNCE_INTERVAL_MS); 
    
    
    // Initialize ISR for ir sending and receiving
    ir_send_init();
    ir_recv_init();
    
    // Serial setup
    Serial.begin(9600);
    
    // Flush serial queue initially
    writePacket(FLUSH,FLUSH,FLUSH,FLUSH);
    delay(500);
    writePacket(FLUSH,FLUSH,FLUSH,FLUSH);
    delay(500);
    
    gid = 31;
    tid = 7;
    pid = 31;
}


/* ---------------------       LOOP      --------------------- */

void loop(){
  // TODO: Check Serial
  updateRespawn();
  gun->updateCBF(gun);
  updateButtons();
  updateShield();
  
  
}


/* ---------------------     BUTTONS     --------------------- */
int (* firePressCBF)(Gun g);
void updateButtons(){

    button_hit.update();
    button_fire.update();
    button_swap.update();
    button_reload.update();
    
      
    if (button_fire.fell()){
        gun->firePressCBF(gun);
        
    } else if (button_fire.rose()){
        gun->fireReleaseCBF(gun); 
        
    }else if (button_fire.read() == LOW){
        //Serial.println("Fire HOLD!");
        gun->fireHoldCBF(gun);
    }
    
    
    if (button_hit.fell()) {
        Serial.println("HIT");
        gun->hitCBF(gun,ENEMY_TEAM,ENEMY_PLAYER,0); 
    }
    
    if (button_swap.fell()) {
        Serial.println("SWAP");
        swap();
    }
    
    if (button_reload.fell()) {
        Serial.println("RELOAD");
        gun->reloadPressCBF(gun);
    }
  
}

