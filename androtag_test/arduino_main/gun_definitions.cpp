#include <Arduino.h>
#include "gun.cpp"
#include "ir_receiver.h"
#include "ir_sender.h"
#include "player_functions.h"

/** GUN DEFINITIONS

This contains all definitions for all guns 

RIFLES
  Lancer       Boring rifle
  Solaris      Heat instead of ammo
  Plasma Coil  Deals more damage the more you hit
  Sonos        Deals damage in a close cone and at a range
  
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


  
*/

/* STANDARD CALLBACKS */
int noCBF(Gun g){return 1;}
int noCBF(){ return 1; }  
int stdHit(Gun g, int teamsrc, int playersrc, int extras);
int stdAmmoReload(Gun g);
int stdFire(Gun g);


/* HEADER, DEFINITIONS BELOW */
Gun lancer;

Gun coil;
int coilOnHit(Gun g, int teamsrc, int playersrc, int extras);

Gun accelerator;
int acceleratorOnTrigger(Gun g);
int acceleratorOnFire(Gun g);
int acceleratorOnHit(Gun g, int teamsrc, int playersrc, int extras);

Gun shotgun;

Gun magnum;

Gun pulsar;
int pulsarOnHit(Gun g, int teamsrc, int playersrc, int extras);

Gun solaris;
int solarisOnFire(Gun g);
int solarisUpdate(Gun g);


Gun allGuns[7] = {lancer, coil, accelerator, shotgun, magnum, pulsar, solaris};

Gun getGun(int id){
    return allGuns[id%7];   
}

Gun[] getLoadout(int [] ids, int loadoutSize){
    Gun g[4];
    int i;
    for (i=0; i<loadoutSize; i++){
        g[i] = getGun(ids[i]);
    }
    return g;    
}


/* Lancer */
Gun lancer = {00,
	12,12,20, // ammo + dmg
	500,1000, // timing
	0,0,	  // firemode
	0,0,0,0,0,0, // other
	stdFire, // firePress
	noCBF, // fireHold
	noCBF, // fireRelease
	stdReload, // reloadPress
	noCBF, // update
	stdHit // onHit
	};

Gun coil = {01,
	100,100,7, // ammo+dmg
	50, 1000, // timing
	1, 0, // firemode
	0,0, // init
	0,0,0,0, // Last hit time, number of hits in a row
	noCBF,
	stdFire,
	noCBF,
	stdReload,
	noCBF.
	coilOnHit //TODO: Change to damage ramp
	};

Gun accelerator = {02,
	4,4,45,
	750,2000,
	0,0,
	0,0,
	0L-1,0,0,0, // hold time
	acceleratorOnTrigger, 
	noCBF, // TODO: Play sound?
	acceleratorOnFire, 
	stdReload,
	noCBF, 
	acceleratorOnHit
	};

Gun shotgun = {03,
	6,6,60,
	550,2100,
	0,1,
	0,0,
	0,0,0,0, // last reload
	stdFire, // TODO: Allow firing as long as there's ammo, i.e. ignore reloading
	noCBF, 
	noCBF,
	stdReload,
	noCBF, // TODO: Reload a shot if isReloading
	stdHit
	};

Gun magnum = {04,
	100,100,15,
	300,1000,
	0,2,
	0,0,
	0,0,0,0, //team,player,time
	stdFire, // TODO: Shot overlapping
	noCBF,
	noCBF,
	stdReload,
	noCBF,
	stdHit // TODO: Shot overlapping
	};

Gun pulsar = {05,
	40,40,12,
	150,2100,
	1,1,
	0,0,
	0,0,0,0,
	noCBF,
	stdFire,
	noCBF,
	stdReload,
	noCBF,
	pulsarOnHit
	};

Gun solaris = {06,
        0,10000,15, // Uses heat
	250,3000,
	0,1,
	0,0,
	0,0,0,0, 
	solarisOnFire, // TODO: HEAT SHOTS
	noCBF,
	noCBF,
	noCBF, // HEAT
	solarisUpdate, // Slowly cools down
	stdHit
	};


/* ------------------------- STANDARD FUNCTIONS ------------------------- */
/* Standard firing function */
int stdFire(Gun g){
    long time = millis();
    if (time < g.readyTime)
        if (g.isReloading)
            return -1;
        else
            return -2;
    
    if (g.ammo <= 0) 
        return -2;
		
    g.ammo -= 1;
    g.readyTime = time + g.fireCd;
	
    // Send an IR packet
    ir_send_packet(g.fireMode, gid, tid, pid, g.id, 0);
    
    //TODO: Send serial message
    return 1;
}

/* Standard Reload Function */
int stdAmmoReload(Gun g){
  long time = millis();
  if (time < g.readyTime)
    if (g.isReloading)
      return -1;
    else
      return -2;
	
  if (g.ammo == g.maxAmmo)
    return -3;
	
  g.ammo = g.maxAmmo;
  g.readyTime = time + g.reloadCd;
  g.isReloading = 1;
  
  // TODO: Send serial message
  return 1;
	
}

/* Standard Hit Function */
int stdHit(Gun g, int teamsrc, int playersrc, int extras){
    return dealDamage(g.damage, int teamsrc, int playersrc);
}
	
/* ------------------------- ACCELERATOR ------------------------- */
int acceleratorOnTrigger(Gun g){
    //TODO: Serial?
    unsigned long time = millis();
    if (time < g.readyTime)
        if (g.isReloading)
            return -1;
        else
            return -2;
    
    if (g.ammo <= 0) 
        return -2;
    
    g.extra0 = time;  
}

int acceleratorOnFire(Gun g){
    unsigned long time = millis();
    
    if (time-g.extra0 > 2000 && g.ammo>1) { // Charged
        int ammo = g.ammo;
        g.ammo = 0;
        g.readyTime = time + g.fireCd;
    	
        ir_send_packet(g.fireMode, gid, tid, pid, g.id, 1);
        //TODO: Send serial message
        return 2;
        
    } else { // Normal
        g.ammo -= 1;
        g.readyTime = time + g.fireCd;
        
        ir_send_packet(g.fireMode, gid, tid, pid, g.id, 0);
        //TODO: Send serial message
        return 1;
    }
}

int acceleratorOnHit(Gun g, int teamsrc, int playersrc, int extras){
    if (extras)
        dealDamage(g.damage*2, int teamsrc, int playersrc);
    else
        dealDamage(g.damage, int teamsrc, int playersrc);
}


/* ------------------------- COIL ------------------------- */
int coilOnHit(Gun g, int teamsrc, int playersrc, int extras){
    // Check timing
    unsigned long time = millis();
    
    if (time-g.extras0 > 400){
        // Hit in a 400ms window, increase damage!
        g.extras1 += (g.extras1==20)?0:1; //Cap at 20 stacks
    } else {
        g.extras1 = 0;
    }
    g.extras0 = time;
    
    return dealDamage(g.damage + g.extras1/5, int teamsrc, int playersrc);
}
	

/* ------------------------- PULSAR ------------------------- */
int pulsarOnHit(Gun g, int teamsrc, int playersrc, int extras){
    if (random(2))
        return dealDamage(g.damage, int teamsrc, int playersrc);
    else
        return 0; // TODO: No damage?
}


/* ------------------------- SOLARIS ------------------------- */ 

int solarisOnFire(Gun g){
    long time = millis();
    if (time < g.readyTime)
        if (g.isReloading)
            return -1;
        else
            return -2;
    
    if (g.ammo >= 10000) 
        return -2;
		
    g.ammo += 130;
    g.readyTime = time + g.fireCd;
	
    // Send an IR packet
    ir_send_packet(g.fireMode, gid, tid, pid, g.id, 0);
    
    //TODO: Send serial message
    
    // Check for overheat
    if (g.ammo >= 10000){
        g.ammo = 10000;
        g.readyTime = time + g.reloadCd;
        g.isReloading = 1;
        // TODO: Send serial message
    }        

    return 1;
}
int solarisUpdate(Gun g, int extras){
    unsigned long time = millis();
    
    // Check if overheated
    if (g.isReloading && time<g.readyTime) return 0;
    
    // Check if done overheating
    if (g.isReloading && time>=g.readyTime){
        g.isReloading = 0;
        g.ammo = 0;
    }
    
    if (g.ammo == 0) return 0; // We're done if no heat
    
    // Take off a little heat
    g.ammo -= (time-g.extras0)*5;
    
    // TODO: Send serial message
    return 1;
        
}
