#include <Arduino.h>

/** GUN CONFIG

This file concerns the setup of the gun structures, datatypes, 
as well as some default callbacks that can be used in the gun_definitions file.

There are various callbacks for each gun, described below. They are saved as a function pointer.

firePressCBF     Called when the trigger goes from up to pressed
fireHoldCBF      Called intermittently while the trigger is still down (guaranteed to be called after fireDownCBF is called)
fireReleaseCBF   Called when the trigger is released
reloadPressCBF   Called when the reload button is pressed, generally this is just set to default

updateCBF        Called intermittently throughout the main loop

hitCBF           Called when a packet is recieved of that gun type

*/


/* Gun structure definition */
typedef struct {
	int ammo;
	int maxAmmo;
	int damage;

	long fireCd;
	long reloadCd;

        int auto;
        int fireMode;
        
	int isReloading;
	long readyTime;
	
	long extra0; // Random extra storage for "things"
	long extra1; // Why not use an array? I want to keep all things static so 
	long extra2; // the structures are all the same size (ie. no dynamic allocation/sizing)
	long extra3;
	
	int (* firePressCBF)(Gun g);
	int (* fireHoldCBF)(Gun g);
	int (* fireReleaseCBF)(Gun g);
	int (* reloadPressCBF)(Gun g);
        int (* updateCBF) (Gun g);
	int (* hitCBF)();
} Gun;


/* These do nothing */
int noCBF(Gun g){ return 1; }
int noCBF(){ return 1; }  


/* Standard firing function */
int stdFire(Gun g){
  long time = millis();
  if (time < g.readyTime)
    if (g.isReloading)
      return -1;
    else
      return -2;
      
    if (g.isReloading)
      g.isReloading = 0;
      
    if (g.ammo <= 0) 
      return -2;
		
    g.ammo -= 1;
    g.readyTime = time + g.fireCd;
	
    //TODO: Make and send packet
    
    // Send serial message?
    
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
	
}

int stdHit(Gun g){
	// TODO: take damage
}
