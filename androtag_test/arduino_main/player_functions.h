

#ifndef PLAYER_FUNCTIONS_H
#define PLAYER_FUNCTIONS_H


extern int pid;
extern int tid;
extern int gid;
extern Gun gun;
extern unsigned shield;

int deal_damage(int x);
void writePacket(int a, int b, int c, int d);
void updateShield();
void updateRespawn();

void swap();


#endif
