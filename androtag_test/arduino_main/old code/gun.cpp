#include <Arduino.h>
#include "ir_receiver.h"
#include "ir_sender.h"
#include "player_functions.h"






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
    ir_send_packet(g.fireMode, getGid(), getTid(), getPid(), g.id, 0);
    
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


