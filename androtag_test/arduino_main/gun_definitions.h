


#ifndef GUN_DEFINITONS_H
#define GUN_DEFINITIONS_H

#include "gun.h"

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

extern Gun lancer;
extern Gun accelerator;
extern Gun solaris;
extern Gun allGuns[];
Gun getGun(int id);
Gun* getLoadout(int ids[], int loadoutSize);

#endif
