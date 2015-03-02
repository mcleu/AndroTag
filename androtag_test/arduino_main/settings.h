
/* SHIELD PARAMETERS */
#define SHIELD_DELAY 5000 // ms
#define SHIELD_RATE  0.03  // ms^-1
#define SHIELD_RATE2 SHIELD_RATE*100 // Adjusted for increased shield precision

/*RESPAWN PARAMETERS */
#define TIME_DEAD 5000 //ms

/* DEBUGGING */
#define VERBOSE_SERIAL
#define DEBUG_SEND
#define DEBUG_RECV
#define DEBUG 0
#define dbmsg(msg) if (DEBUG) {Serial.print(msg);}
