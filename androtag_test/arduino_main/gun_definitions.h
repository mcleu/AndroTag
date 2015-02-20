

#ifndef GUN_DEFINITONS_H
#define GUN_DEFINITIONS_H

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
        int id;
	int ammo;
	int maxAmmo;
	int damage;

	long fireCd;
	long reloadCd;

        int auto;
        int fireMode;
        
	int isReloading;
	long readyTime;
	
	unsigned long extra0; // Random extra storage for "things"
	unsigned long extra1; // Why not use an array? I want to keep all things static so 
	unsigned long extra2; // the structures are all the same size (ie. no dynamic allocation/sizing)
	unsigned long extra3;
	
	int (* firePressCBF)(Gun g);
	int (* fireHoldCBF)(Gun g);
	int (* fireReleaseCBF)(Gun g);
	int (* reloadPressCBF)(Gun g);
        int (* updateCBF) (Gun g);
	int (* hitCBF)(Gun g, int teamsrc, int playersrc, int extras);
} Gun;

Gun getGun(int id);

#endif
