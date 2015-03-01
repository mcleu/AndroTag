

#include "game.h"
#include "player_functions.g"

int gid = 0;
unsigned long game_start_time = 0;
unsigned long game_end_time = 0;

int game_state = GAME_DISABLED;

void setGameState(int state){
	game_state = state;
}

void setGameStart(unsigned long time){
	game_start_time = time;
	updateGame();
}

void setGameEnd(unsigned long time){
	game_end_time = time;
	updateGame();
}

void endGame(){
	game_state = GAME_DISABLED;
}
	
void updateGame(){
	unsigned long time = millis();
	if (game_state != GAME_DISABLED || game_state != GAME_PAUSED){
		if (time <= game_start_time){
			game_state = GAME_WAITING;
		} else if (time >= game_start_time && game_end_time < time) {
			game_state = GAME_RUNNING;
		} else if (time >= game_end_time){
			game_state = GAME_ENDED;
		} else {
			game = GAME_DISABLED;
		}
	}
}