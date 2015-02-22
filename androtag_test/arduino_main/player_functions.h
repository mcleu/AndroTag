
#include "gun.h"
#ifndef PLAYER_FUNCTIONS_H
#define PLAYER_FUNCTIONS_H

#define DEBUG 0
#define dbmsg(msg) if (DEBUG) {Serial.print(msg);}

extern int num_guns;
extern int active_gun;
extern int pid;
extern int tid;
extern int gid;
extern Gun* gun;
extern Gun* loadout[4];
extern unsigned shield;

int dealDamage(int x, int teamsrc, int playersrc);
//#define writePacket(a,b,c,d) {Serial.print(a); Serial.print(' '); Serial.print(b); Serial.print(' '); Serial.print(c);Serial.print(' '); Serial.println(d);}
void writePacket(int a, int b, int c, int d);
void setLoadout(int gunids[]);


void updateShield();
void updateRespawn();

void swap();


#endif
