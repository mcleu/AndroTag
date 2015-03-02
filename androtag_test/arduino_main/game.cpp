

#include "game.h"
#include "player_functions.h"
#include <Arduino.h>

int gid = 255;
unsigned long game_start_time = 0;
unsigned long game_end_time = 0-1;

int game_state = GAME_WAITING;

int getGameState(){
    return game_state;
}
void setGameState(int state){
    game_state = state;
}

void setGameStart(unsigned long time){
    //Serial.println(time,BIN);
    unsigned long t0 = millis();
    game_start_time = time+t0;
    updateGame();
}

void setGameEnd(unsigned long time){
    unsigned long t0 = millis();
    game_end_time = time+t0;
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
        } else if (game_start_time <= time && time < game_end_time) {
            game_state = GAME_RUNNING;
        } else if (time >= game_end_time){
            game_state = GAME_ENDED;
        } else {
            game_state = GAME_DISABLED;
        } 
    }
}
