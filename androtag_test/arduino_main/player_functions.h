
#include "gun.h"
#ifndef PLAYER_FUNCTIONS_H
#define PLAYER_FUNCTIONS_H

#define writePacket(a,b,c,d) Serial.print(a); Serial.print(' '); Serial.println(b);


extern int pid;
extern int tid;
extern int gid;
extern Gun gun;
extern unsigned shield;

int dealDamage(int x, int teamsrc, int playersrc);
//void writePacket(int a, int b, int c, int d);
void setLoadout(int gunids[]);


void updateShield();
void updateRespawn();

void swap();


#endif
