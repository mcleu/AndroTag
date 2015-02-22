/* Gun structure definition */

#ifndef GUN_H
#define GUN_H
typedef struct Gun {
        int id;
	int ammo;
	int maxAmmo;
	int damage;

	long fireCd;
	long reloadCd;

        int fireMode;
        
	int isReloading;
	long readyTime;
	
	unsigned long extra0; // Random extra storage for "things"
	unsigned long extra1; // Why not use an array? I want to keep all things static so 
	unsigned long extra2; // the structures are all the same size (ie. no dynamic allocation/sizing)
	unsigned long extra3;
	
	int (* firePressCBF)(Gun* g);
	int (* fireHoldCBF)(Gun* g);
	int (* fireReleaseCBF)(Gun* g);
	int (* reloadPressCBF)(Gun* g);
        int (* updateCBF) (Gun* g);
	int (* hitCBF)(Gun* g, int teamsrc, int playersrc, int extras);
} Gun;

#endif
