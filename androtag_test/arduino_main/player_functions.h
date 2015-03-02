
#include "gun.h"
#include "settings.h"
#include <Arduino.h>
#ifndef PLAYER_FUNCTIONS_H
#define PLAYER_FUNCTIONS_H

extern int isDead;
extern int isDisabled;

extern int num_guns;
extern int active_gun;
extern int pid;
extern int tid;
extern int gid;

extern Gun* gun;
extern Gun* loadout[4];
extern unsigned shield;

int dealDamage(int x, int teamsrc, int playersrc);
// Allow direct writing of packet data to serial
//#ifdef VERBOSE_SERIAL
//#define writePacket(a,b,c,d) {Serial.print(a); Serial.print(' '); Serial.print(b); Serial.print(' '); Serial.print(c);Serial.print(' '); Serial.println(d);}
//#else
void writePacket(int a, int b, int c, int d);
//#endif
void setLoadout(int gunids[]);

boolean isEnemy(byte tid);
void addEnemy(byte tid);
void clearEnemies();

void updateShield();
void updateRespawn();
void setColor(byte r, byte g, byte b);

void swap();


#endif
