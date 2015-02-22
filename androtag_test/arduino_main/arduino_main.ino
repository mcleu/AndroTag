
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
  readSerial();
  updateIRPackets();
  updateRespawn();
  gun->updateCBF(gun);
  updateButtons();
  updateShield();
  
  
}


/* ---------------------   IR RECEIVING   --------------------- */
unsigned long packet;
Gun* hitByGun;
void updateIRPackets(){
    packet = ir_pop_packet();
    while (packet!=NO_PACKET){
        // Check if correct game and correct parity (error checking)
        if (getPacketGid(packet) == gid && getPacketParity(packet)==0){
            
            // Get the gun type firing this packet
            hitByGun = getGun(getPacketGunid(packet));
            
            // Get hit by this gun, given the source and extra information
            hitByGun->hitCBF(hitByGun,
                            getPacketTid(packet),
                            getPacketPid(packet),
                            getPacketExtras(packet));
            
        }
        
        // Try to do another packet if we can
        packet = ir_pop_packet();
    }
            
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
        gun->fireHoldCBF(gun);
    }
    
        
    if (button_swap.fell()) {
        swap();
    }
    
    if (button_reload.fell()) {
        gun->reloadPressCBF(gun);
    }
  
}

/* ---------------------     SERIAL READ     --------------------- */
byte a0, a1, a2, a3;
void readSerial(){
    while (Serial.available() >= 4){
        a0 = Serial.read();
        a1 = Serial.read();
        a2 = Serial.read();
        a3 = Serial.read();
        
        switch (a0){
            case SET_GUN_0:
                loadout[0] = getGun(a1);
                gun = loadout[active_gun];
                break;
            case SET_GUN_1:
                loadout[1] = getGun(a1);
                gun = loadout[active_gun];
                break;
            case SET_GUN_2:
                loadout[2] = getGun(a1);
                gun = loadout[active_gun];
                break;
            case SET_GUN_3:
                loadout[3] = getGun(a1);
                gun = loadout[active_gun];
                break;
            case SET_NUM_GUNS:
                num_guns = a1;
                break;
        }
                
        
    }
}

