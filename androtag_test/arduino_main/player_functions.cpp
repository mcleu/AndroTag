
#include "player_functions.h"
#include "gun_definitions.cpp"

int pid;
int gid;
int tid;




/* SHIELD PARAMETERS */
#define SHIELD_DELAY 5000 // ms
#define SHIELD_RATE  0.03  // ms^-1
#define SHIELD_RATE2 SHIELD_RATE*100 // Adjusted for increased shield precision

/* Shielding */
unsigned shield = 10000;
long shield_last_damage = 0;
long shield_last_update = 0;


int getPid(){return pid;}
int getTid(){return tid;}
int getGid(){return gid;}
void setPid(int x){ pid = x; }
void setTid(int x){ tid = x; }
void setGid(int x){ gid = x: }

int dealDamage(int x){
    Serial.print("Damage: ");
    Serial.println(x);
    // TODO: Deal damage!
    return 0; // TODO: Return code for killed
} 

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

