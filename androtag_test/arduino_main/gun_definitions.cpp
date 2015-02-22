#include <Arduino.h>
#include "gun_definitions.h"
#include "ir_receiver.h"
#include "ir_sender.h"
#include "player_functions.h"
#include "serial_codes.h"

/** GUN DEFINITIONS

This contains all definitions for all guns 

RIFLES
  Lancer       Boring rifle
  Solaris      Heat instead of ammo
  Plasma Coil  Deals more damage the more you hit
  Buckrifle    Deals damage in a close cone and at a range
  
HE RIFLES
  Accelerator  Hold to charge for OHKO
  Sniper       Boring old sniper rifle
  
HAND CANNONS
  Magnum       Close range pistol w/ headshots
  Heat Pistol  Heat based full auto, w/o headshots
  Arc Pistol   Hurts you, but deals a little more damage

PARTY POOPERS
  Pulsar       MG with random hits
  Shotgun      Boring shotgun with incremental reload
  SQ Gun       Accelerator but with blast instead
  
EQUIPMENT
  TODO
  
ID Order (so far)
00	Lancer
01	Plasma Coil
02 	Accelerator
03	Shotgun
04	Magnum
05	Pulsar
06      Solaris

*/

/* STANDARD CALLBACKS */
int noCBF(Gun* g){return 1;}
int noCBF(){ return 1; }  
int stdHit(Gun* g, int teamsrc, int playersrc, int extras);
int stdReload(Gun* g);
int stdAmmoUpdate(Gun* g);
int stdFire(Gun* g);


/* NONSTANDARD CALLBACKS, DEFINITIONS BELOW */
int coilOnHit(Gun* g, int teamsrc, int playersrc, int extras);

int acceleratorOnTrigger(Gun* g);
int acceleratorOnFire(Gun* g);
int acceleratorOnHit(Gun* g, int teamsrc, int playersrc, int extras);

int shotgunOnFire(Gun* g);
int shotgunReload(Gun* g);
int shotgunUpdate(Gun* g);

int pulsarOnHit(Gun* g, int teamsrc, int playersrc, int extras);

int solarisOnFire(Gun* g);
int solarisUpdate(Gun* g);

/* Lancer */
Gun lancer = {00,
	12,12,20, // ammo + dmg
	500,1000, // timing
	1,	  // firemode
	0,0,0,0,0,0, // other
	stdFire, // firePress
	noCBF, // fireHold
	noCBF, // fireRelease
	stdReload, // reloadPress
	stdAmmoUpdate, // update
	stdHit // onHit
	};

Gun coil = {01,
	100,100,7, // ammo+dmg
	50, 1000, // timing
	1, // firemode
	0,0, // init
	0,0,0,0, // Last hit time, number of hits in a row
	noCBF,
	stdFire,
	noCBF,
	stdReload,
	stdAmmoUpdate,
	coilOnHit
	};

Gun accelerator = {02,
	4,4,45,
	750,2000,
	1,
	0,0,
	0L-1,0,0,0, // hold time
	acceleratorOnTrigger, 
	noCBF,
	acceleratorOnFire, 
	stdReload,
	stdAmmoUpdate, 
	acceleratorOnHit
	};

Gun shotgun = {03,
	6,6,60,
	550,2100,
	2,
	0,0,
	0,0,0,0, // last reload
	shotgunOnFire,
	noCBF, 
	noCBF,
	shotgunReload,
	shotgunUpdate,
	stdHit
	};

Gun magnum = {04,
	100,100,15,
	300,1000,
	1,
	0,0,
	0,0,0,0, //player,time
	stdFire, // TODO: Shot overlapping
	noCBF,
	noCBF,
	stdReload,
	stdAmmoUpdate,
	stdHit // TODO: Shot overlapping
	};

Gun pulsar = {05,
	40,40,12,
	150,2100,
	2,
	0,0,
	0,0,0,0,
	noCBF,
	stdFire,
	noCBF,
	stdReload,
	stdAmmoUpdate,
	pulsarOnHit
	};

Gun solaris = {06,
        0,10000,15, // Uses heat
	120,3500,
	1,
	0,0,
	0,0,0,0, 
	solarisOnFire, // HEAT SHOTS WOO
	noCBF,
	noCBF,
	noCBF, // No reload for this gun
	solarisUpdate, // Slowly cools down
	stdHit
	};


/* ------------------------- STANDARD FUNCTIONS ------------------------- */
/* Standard firing function */
int stdFire(Gun* g){
    long time = millis();
    if (time < g->readyTime || g->isReloading)
        return 0;
    
    if (g->ammo <= 0){
        writePacket(FIRE_SUCCESS,0,0,0);
        return 0;
    }
		
    g->ammo -= 1;
    g->readyTime = time + g->fireCd;
	
    // Send an IR packet
    ir_send_packet(g->fireMode, gid, tid, pid, g->id, 0);
    
    // Send Serial Message
    writePacket(FIRE_SUCCESS,1,0,0);
    writePacket(SET_AMMO,g->ammo,0,0);
    
    return 1;
}

/* Standard Reload Function */
int stdReload(Gun* g){
  long time = millis();
  if (time < g->readyTime || g->isReloading)
    if (g->isReloading)
      return 0;
	
  if (g->ammo == g->maxAmmo)
    return 0;
	
  g->readyTime = time + g->reloadCd;
  g->isReloading = 1;
  
  writePacket(TRY_RELOAD,1,0,0);
  return 1;
	
}

/* Standard Ammo update that completes a reload */
int stdAmmoUpdate(Gun* g){
  long time = millis();
  if (g->isReloading && time >= g->readyTime){
      g->ammo = g->maxAmmo;
      writePacket(RELOAD_SUCCESS,1,0,0);
      writePacket(SET_AMMO,g->ammo,0,0);
      g->isReloading = 0;
      
      return 1;
  
  } 
  
  return 0;
}

/* Standard Hit Function */
int stdHit(Gun* g, int teamsrc, int playersrc, int extras){
    // Serial write completed in this fcn
    return dealDamage(g->damage, teamsrc, playersrc);
}
	
/* ------------------------- ACCELERATOR ------------------------- */
int acceleratorOnTrigger(Gun* g){
    unsigned long time = millis();
    if (time < g->readyTime || g->isReloading)
        return 0;
    
    if (g->ammo <= 0){
        writePacket(TRY_FIRE,0,0,0);
        return 0;
    }
    
    g->extra0 = time;
    writePacket(TRY_FIRE,1,0,0);
}

int acceleratorOnFire(Gun* g){
    unsigned long time = millis();
    if (time < g->readyTime || g->isReloading)
        return 0;
    
    if (g->ammo <= 0){
        return 0;
    }
    
    
    int charged;
    if (time-g->extra0 > 2000 && g->ammo>1) { // Charged
        int ammo = g->ammo;
        g->ammo = 0;
        g->readyTime = time + g->fireCd;
        charged = 1;
    	        
    } else { // Normal
        g->ammo -= 1;
        g->readyTime = time + g->fireCd;
        charged = 0;
    }
    
    ir_send_packet(g->fireMode, gid, tid, pid, g->id, charged);
    writePacket(FIRE_SUCCESS,1+charged,0,0);
    writePacket(SET_AMMO,g->ammo,0,0);
    return 1;
}

int acceleratorOnHit(Gun* g, int teamsrc, int playersrc, int extras){
    if (extras)
        return dealDamage(g->damage*2, teamsrc, playersrc);
    else
        return dealDamage(g->damage, teamsrc, playersrc);
}


/* ------------------------- COIL ------------------------- */
int coilOnHit(Gun* g, int teamsrc, int playersrc, int extras){
    // Check timing
    unsigned long time = millis();
    
    if (time-g->extra0 < 400){
        // Hit in a 400ms window, increase damage!
        g->extra1 += (g->extra1==20)?0:1; //Cap at 20 stacks
    } else {
        g->extra1 = 0;
    }
    g->extra0 = time;
    
    return dealDamage(g->damage + g->extra1/5, teamsrc, playersrc);
}
	

/* ------------------------- SHOTGUN ------------------------- */ 

/* Fire, ignoring if we are reloading */
int shotgunOnFire(Gun* g){
    long time = millis();
    if (time < g->readyTime)
        return 0;
    
    if (g->ammo <= 0){
        writePacket(FIRE_SUCCESS,0,0,0);
        return 0;
    }
    
    g->isReloading = 0;	
    g->ammo -= 1;
    g->readyTime = time + g->fireCd;
	
    // Send an IR packet
    ir_send_packet(g->fireMode, gid, tid, pid, g->id, 0);
    
    // Send Serial Message
    writePacket(FIRE_SUCCESS,1,0,0);
    writePacket(SET_AMMO,g->ammo,0,0);
    
    return 1;
}

/* Simply set the isreloading flag */
int shotgunReload(Gun* g){
  long time = millis();
  if (time < g->readyTime || g->isReloading)
    if (g->isReloading)
      return 0;
	
  if (g->ammo == g->maxAmmo)
    return 0;
	
  g->isReloading = 1;
  g->extra0 = time;
  
  writePacket(TRY_RELOAD,1,0,0);
  return 1;
}

/* Standard Ammo update that completes a reload */
int shotgunUpdate(Gun* g){
  long time = millis();
  if (g->isReloading){
      
      // Add ammo if possible
      long delta = (time-g->extra0);
      if (delta >= 900) {
          g->ammo += 1;
          writePacket(RELOAD_SUCCESS,1,0,0);
          writePacket(SET_AMMO,(g->ammo),0,0);
          g->extra0 = time;
      }
      
      if (g->ammo == g->maxAmmo)
          g->isReloading = 0;
          
      return 1;
  } 
  
  return 0;
}

/* ------------------------- PULSAR ------------------------- */
int pulsarOnHit(Gun* g, int teamsrc, int playersrc, int extras){
    if (random(2)==1)
        return dealDamage(g->damage, teamsrc, playersrc);
    else
        return 0; 
}


/* ------------------------- SOLARIS ------------------------- */ 

int solarisOnFire(Gun* g){
    long time = millis();
    if (time < g->readyTime || g->isReloading)
        return 0;
    
    if (g->ammo >= 10000) {
        writePacket(FIRE_SUCCESS,0,0,0);
        return 0;
    }
		
    g->ammo += 2200;
    g->extra1 = g->ammo/100;
    g->readyTime = time + g->fireCd;
	
    // Send an IR packet
    ir_send_packet(g->fireMode, gid, tid, pid, g->id, 0);
    
    writePacket(FIRE_SUCCESS,1,0,0);
    
    // Check for overheat
    if (g->ammo >= 10000){
        g->ammo = 10000;
        g->readyTime = time + g->reloadCd;
        g->isReloading = 1;
        writePacket(TRY_RELOAD,1,0,0);
    }        
    writePacket(SET_AMMO,g->ammo/100,0,0);

    return 1;
}
int solarisUpdate(Gun* g){
    unsigned long time = millis();
    
    // Check if overheated
    if (g->isReloading && time<g->readyTime) return 0;
    
    // Check if done overheating
    if (g->isReloading && time>=g->readyTime){
        g->isReloading = 0;
        g->ammo = 0;
        writePacket(RELOAD_SUCCESS,1,0,0);
        writePacket(SET_AMMO,0,0,0);
    }
        
    if (g->ammo == 0) return 0; // We're done if no heat
        
    // Take off a little heat
    long delta = (time-g->extra0)*4;
    g->ammo = (g->ammo<=delta)?0:g->ammo-delta;
    g->extra0 = time;
    
    if (g->ammo/100 != g->extra1){
        writePacket(SET_AMMO,(g->ammo/100),0,0);
        g->extra1 = g->ammo/100;
    }
    return 1;
        
}


Gun* allGuns[AVAILABLE_GUNS] = {&lancer, &coil, &accelerator, &shotgun, &magnum, &pulsar, &solaris};

Gun* getGun(int id){
    return allGuns[id%AVAILABLE_GUNS]; 
}
