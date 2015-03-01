

#ifndef GAME_H
#define GAME_H

#define GAME_DISABLED 0
#define GAME_WAITING 1
#define GAME_RUNNING   2
#define GAME_PAUSED 3 // Partial implementation, I wouldn't suggest using it
#define GAME_ENDED 4


extern int gid;
void setGameStart(unsigned long time);
void setGameEnd(unsigned long time);
void setGameState(int state);
int getGameState();
void updateGame();


#endif
