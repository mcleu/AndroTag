
#include "player_functions.h"
#include "gun_definitions.h"
#include "serial_codes.h"

int pid;
int gid;
int tid;

Gun loadout[4] = getLoadout({0,1,2});
int num_guns = 3;
int active_gun = 0;

Gun gun = loadout[num_guns];


/* SHIELD PARAMETERS */
#define SHIELD_DELAY 5000 // ms
#define SHIELD_RATE  0.03  // ms^-1
#define SHIELD_RATE2 SHIELD_RATE*100 // Adjusted for increased shield precision

/* Shielding */
unsigned shield = 10000;
long shield_last_damage = 0;
long shield_last_update = 0;



/** XXXXXXXXXXXXXXXXXXXXXXX Damage Function XXXXXXXXXXXXXXXXXXXXXXXXX **/
// Deals d damage to the shields
void dealDamage(int d, int teamsrc, int playersrc){
      
      if (isDead)
        return;  
  
      shield = (shield>=1000)?shield-1000:0;
      shield_last_damage = millis();
      
      // Write Kill/hit message to serial
      if (shield == 0){
        writePacket(KILLED_BY, teamsrc, playersrc, 255); 
        isDead = 1;
        respawnTime = shield_last_damage + TIME_DEAD;
      } else {
        writePacket(HIT_BY, teamsrc, playersrc, 255); 
      }
}



/** XXXXXXXXXXXXXXXXXXXXXXX SHIELD UPDATING XXXXXXXXXXXXXXXXXXXXXXXXX **/

int i;
unsigned shieldSend = 0;
void updateShield() {
  // Update the shield value
  long time = millis();
  long time_update = time-shield_last_update;
  
  // Update the thermometer to write to shift reg
  int shield_thermometer_ = 0;
  for (i=0; i<8; i++){
    if (i*1000>=shield) break;
    bitSet(shield_thermometer_,i);
  }
  shield_thermometer = shield_thermometer_; // Copy to data for ISR
  
  // Skip super short updates
  if (time_update < 100)
    return;
    
  if (shield < 10000 && !isDead){
    long time_damage = time-shield_last_damage;
    
    if (time_damage >= SHIELD_DELAY){
       //Update by amount since last update
       float incr = SHIELD_RATE2*((float)time_update);
       shield += (int) incr;
       shield = (shield>10000)?10000:shield;
    }
    
    // Record the update time
    shield_last_update = time;
    
  }
  
  // Update phone with new value
  if (shieldSend != shield/100){
    shieldSend = shield/100;
    writePacket(SET_SHIELD, shieldSend, 0, 255);    
  }
}


/** XXXXXXXXXXXXXXXXXXXXXXX OTHER THINGS XXXXXXXXXXXXXXXXXXXXXXXXX **/

byte outBuffer[] = {0,0,0,0,0};
void writePacket(int a, int b, int c, int d){
	outBuffer[0] = a;
	outBuffer[1] = b;
	outBuffer[2] = c;
	outBuffer[3] = d;
	Serial.write(outBuffer,4);
	Serial.flush();
}


/** XXXXXXXXXXXXXXXXXXXXXXX RESPAWN UPDATING XXXXXXXXXXXXXXXXXXXXXXXXX **/
void updateRespawn(){
   if (isDead){
     long time = millis();
     if (time>respawnTime){
       isDead = 0;
       shield = 100;
     }
   } 
}

/** XXXXXXXXXXXXXXXXXXXXXXX BUTTON CALLBACKS XXXXXXXXXXXXXXXXXXXXXXXXX **/
void swap(){
    // TODO: Check if trigger is down
    
    active_gun = (active_gun < num_guns-1)?active_gun+1:0;
    writePacket(SET_ACTIVE, active_gun, 0, 255);
    gun = loadout[active_gun];
}
