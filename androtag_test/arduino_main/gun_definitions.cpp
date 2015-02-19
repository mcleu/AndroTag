#include <Arduino.h>

/** GUN DEFINITIONS

This contains all definitions for all guns 

RIFLES
  Lancer       Boring rifle
  Mercurial    Heat instead of ammo
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

Gun lancer;
Gun coil;
Gun accelerator;
Gun shotgun;
Gun magnum;
Gun pulsar;

Gun guns[6] = {lancer, coil, accelerator, shotgun, magnum, pulsar};


/* Lancer */
Gun lancer = {
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

Gun coil = {
	100,100,7, // ammo+dmg
	50, 1000, // timing
	1, 0, // firemode
	0,0, // init
	5,0,0,0, // increase damage per hit
	noCBF,
	stdFire,
	noCBF,
	stdReload,
	noCBF.
	stdHit //TODO: Change to damage ramp
	};

Gun accelerator = {
	4,4,45,
	750,2000,
	0,0,
	0,0,
	1000,0,0,0, // Time to hold for charged shot, hold time
	noCBF,
	noCBF, // TODO: Save time
	stdFire, // TODO: Do charged shot
	stdReload,
	noCBF, // TODO: Play sound?
	stdHit // TODO: use packet extra for bonus damage = # shots fires
	};

Gun shotgun = {
	6,6,60,
	550,2100,
	0,1,
	0,0,
	500,0,0,0, // Time/shot reloaded, last reload
	stdFire, // TODO: Allow firing as long as there's ammo, i.e. ignore reloading
	noCBF, 
	noCBF,
	stdReload,
	noCBF, // TODO: Reload a shot if isReloading
	stdHit
	};

Gun magnum = {
	100,100,15,
	300,1000,
	0,2,
	0,0,
	0,0,0,0,
	stdFire, // TODO: Shot overlapping
	noCBF,
	noCBF,
	stdReload,
	noCBF,
	stdHit // TODO: Shot overlapping
	};

Gun Pulsar = {
	40,40,12,
	150,2100,
	1,1,
	0,0,
	50,0,0,0, // Hit rate (out of 100)
	noCBF,
	stdFire,
	noCBF,
	stdReload,
	noCBF,
	stdHit // TODO: Random hit
	};
	
	
	
