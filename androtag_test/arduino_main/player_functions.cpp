
#include "player_functions.h"
#include "gun_definitions.h"
#include "serial_codes.h"
#include "settings.h"
#include <Arduino.h>

/* Color Information */
byte color_r = 1;
byte color_b = 0;
byte color_g = 0;

/* Player slot information */
int pid;
int tid;

/* Loadout configuration */
int num_guns = 7;
Gun* loadout[4] = {getGun(3),getGun(1),getGun(2),getGun(6)};
int active_gun = 0;

Gun* gun = loadout[active_gun];

/* Shielding */
unsigned shield = 10000;
long shield_last_damage = 0;
long shield_last_update = 0;

/* Respawning */
int isDead = 0;
int isDisabled = 0;
long respawnTime = 0;

/* Enemies */
boolean enemies[16];


/** XXXXXXXXXXXXXXXXXXXXXXX Damage Function XXXXXXXXXXXXXXXXXXXXXXXXX **/
// Deals d damage to the shields
int dealDamage(int d, int teamsrc, int playersrc){
      
      if (isDead)
        return 0;  
      
      dbmsg("Hit for ");
      dbmsg(d);
      dbmsg('\n');
  
      shield = (shield>=d*100)?shield-d*100:0;
      shield_last_damage = millis();
      
      // Write Kill/hit message to serial
      if (shield == 0){
        writePacket(KILLED_BY, teamsrc, playersrc, 0); 
        isDead = 1;
        respawnTime = shield_last_damage + TIME_DEAD;
        return 1;
      } else {
        writePacket(HIT_BY, teamsrc, playersrc, 0); 
		return 0;
      }
}



/** XXXXXXXXXXXXXXXXXXXXXXX SHIELD UPDATING XXXXXXXXXXXXXXXXXXXXXXXXX **/

int i;
unsigned shieldSend = 0;
void updateShield() {
  // Update the shield value
  long time = millis();
  long time_update = time-shield_last_update;
    
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
    writePacket(SET_SHIELD, shieldSend, 0, 0);    
  }
}


/** XXXXXXXXXXXXXXXXXXXXXXX OTHER THINGS XXXXXXXXXXXXXXXXXXXXXXXXX **/
#ifndef VERBOSE_SERIAL
byte outBuffer[] = {0,0,0,0,0};
void writePacket(int a, int b, int c, int d){
	outBuffer[0] = a;
	outBuffer[1] = b;
	outBuffer[2] = c;
	outBuffer[3] = d;
	Serial.write(outBuffer,4);
	Serial.flush();
}
#endif


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
    writePacket(SET_ACTIVE, active_gun, 0, 0);
    gun = loadout[active_gun];
}


/** XXXXXXXXXXXXXXXXXXXXXXX ENEMY CONTROL XXXXXXXXXXXXXXXXXXXXXXXXX **/

void addEnemy(byte tid){
	enemies[tid] = true;
}
boolean isEnemy(byte tid){
	return enemies[tid%16];
}
void clearEnemies(){
	int i;
	for (i = 0; i< 16; i++)
		enemies[i] = false;
}


/** XXXXXXXXXXXXXXXXXXXXXXX COLOR CONTROL XXXXXXXXXXXXXXXXXXXXXXXXX **/

void setColor(byte r, byte g, byte b){
    color_r = r;
    color_g = g;
    color_b = b;   
}
