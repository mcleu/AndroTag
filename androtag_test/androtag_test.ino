#include <Bounce2.h>
#include <TimerOne.h>


/* PIN ASSIGNMENTS */
#define SH_LATCH   8
#define SH_CLK     12
#define SH_Q       11

#define BUT_HIT    2
#define BUT_FIRE   3
#define BUT_SWAP   4
#define BUT_RELOAD 5
#define BUT_FLUSH  6

/* SHIELD PARAMETERS */
#define SHIELD_DELAY 5000 // ms
#define SHIELD_RATE  0.03  // ms^-1
#define SHIELD_RATE2 SHIELD_RATE*100 // Adjusted for increased shield precision

/*RESPAWN PARAMETERS */
#define TIME_DEAD 5000 //ms

/* TEMP GUN PROPERTIES */
int gun_cd[4] = {500, 50, 750, 150};
int gun_auto[4] = {0, 1, 0, 1};

// Operation codes
#define SET_LIVES 0
#define SET_RESPAWN 1
#define SET_SHIELD 2
#define SET_ACTIVE 3
#define TRY_RELOAD 4
#define TRY_FIRE 5
#define SET_NUM_GUNS 6
#define SET_TEAM 7
#define SET_COLOR 8
#define SET_PLAYER 9
#define SET_GAME 10
#define SET_STARTTIME 11
#define SET_ENDTIME 12
#define SET_DISABLED 13
#define END_GAME 14
#define GUN_PROPERTIES 15
#define FIRE_SUCCESS 16
#define RELOAD_SUCCESS 17
#define NO_LIVES 18
#define HIT_BY 19
#define KILLED_BY 20
#define ADD_ENEMY 21
#define CLEAR_ENEMIES 22
#define FLUSH_SERIAL  254
#define ACK           255

/* STATIC TESTING VARIABLES */
#define ENEMY_TEAM   1
#define ENEMY_PLAYER 1
#define HIT_DAMAGE   10

/* Shielding */
unsigned shield = 10000;
long shield_last_damage = 0;
long shield_last_update = 0;

/* Respawning */
int isDead = 0;
long respawnTime = 0;

/* ISR Variables */
volatile int shield_thermometer = 0;
volatile int isr_damage_taken = 0;

/* Buttons (debounced) */
#define DEBOUNCE_INTERVAL_MS  5
Bounce button_hit = Bounce();
Bounce button_fire = Bounce();
Bounce button_swap = Bounce();
Bounce button_reload = Bounce();
Bounce button_flush = Bounce();


void setup(){
  //set pins to output
  pinMode(SH_LATCH, OUTPUT);
  pinMode(SH_CLK, OUTPUT);  
  pinMode(SH_Q, OUTPUT);
  
  // Buttons, with pullup
  pinMode(BUT_HIT, INPUT_PULLUP);
  pinMode(BUT_FIRE, INPUT_PULLUP);
  pinMode(BUT_SWAP, INPUT_PULLUP);
  pinMode(BUT_RELOAD, INPUT_PULLUP);
  pinMode(BUT_FLUSH, INPUT_PULLUP);
  button_hit.attach(BUT_HIT);
  button_fire.attach(BUT_FIRE);
  button_swap.attach(BUT_SWAP);
  button_reload.attach(BUT_RELOAD);
  button_flush.attach(BUT_FLUSH);
  button_hit.interval(DEBOUNCE_INTERVAL_MS);  
  button_fire.interval(DEBOUNCE_INTERVAL_MS); 
  button_swap.interval(DEBOUNCE_INTERVAL_MS); 
  button_reload.interval(DEBOUNCE_INTERVAL_MS); 
  button_flush.interval(DEBOUNCE_INTERVAL_MS); 
  
  
  // Initialize ISR for shield shift register
  Timer1.initialize(10000); // set a timer for 10k us, or 10ms
  Timer1.attachInterrupt( shiftTimerIsr );
  
  // Serial setup
  Serial.begin(9600);
  
  // Flush serial queue initially
  int tmp;
  for (tmp = 0; tmp<2; tmp++){
    writePacket(FLUSH_SERIAL,FLUSH_SERIAL,FLUSH_SERIAL,FLUSH_SERIAL);
    delay(500);
  }
}


void loop(){
  
  updateRespawn();
  updateButtons();
  updateShield();
  
  
}

/** XXXXXXXXXXXXXXXXXXXXXXX SERIAL FUNCTIONS XXXXXXXXXXXXXXXXXXXXXXXXX **/
byte outBuffer[] = {0,0,0,0,0};
void writePacket(int a, int b, int c, int d){
	outBuffer[0] = a;
	outBuffer[1] = b;
	outBuffer[2] = c;
	outBuffer[3] = d;
	Serial.write(outBuffer,4);
	Serial.flush();
}


/** XXXXXXXXXXXXXXXXXXXXXXX HUD ISR UPDATING XXXXXXXXXXXXXXXXXXXXXXXXX **/

void shiftWrite(int b){
  digitalWrite(SH_LATCH, LOW);
  shiftOut(SH_Q, SH_CLK, MSBFIRST, b);
  digitalWrite(SH_LATCH, HIGH);
}

int isr_counter = 0;
int isr_last_shield = 0;
void shiftTimerIsr()
{   
  if (isr_counter < 100 || isr_last_shield < shield){
    if (isr_damage_taken ){ 
      isr_counter = 0;
      isr_damage_taken = 0;
    } else if (isr_last_shield < shield){
      isr_counter = 0;
    } else {
      isr_counter++;
    }
    
    shiftWrite(shield_thermometer);
    
  } else if (shield == 0) {
    isr_counter = 0;
    shiftWrite(1);
    
  } else {
    isr_counter = 0;
    shiftWrite(-1);
  }
  isr_last_shield = shield;
}



/** XXXXXXXXXXXXXXXXXXXXXXX SHIELD UPDATING XXXXXXXXXXXXXXXXXXXXXXXXX **/

int i;
unsigned shieldSend = 0;
void updateShield() {
  // Update the shield value
  long time = millis();
  long time_update = time-shield_last_update;
  
  // Update the thermometer to write to shift reg
  int shield_thermometer_ = 0;
  for (i=0; i<8; i++){
    if (i*1000>=shield) break;
    bitSet(shield_thermometer_,i);
  }
  shield_thermometer = shield_thermometer_; // Copy to data for ISR
  
  // Skip super short updates
  if (time_update < 100)
    return;
    
  if (shield < 10000 && !isDead){
    long time_damage = time-shield_last_damage;
    
    if (time_damage >= SHIELD_DELAY){
       //Update by amount since last update
       float incr = SHIELD_RATE2*((float)time_update);
       shield += (int) incr;
       shield = (shield>10000)?10000:shield;
    }
    
    // Record the update time
    shield_last_update = time;
    
  }
  
  // Update phone with new value
  if (shieldSend != shield/100){
    shieldSend = shield/100;
    writePacket(SET_SHIELD, shieldSend, 0, 255);    
  }
}



/** XXXXXXXXXXXXXXXXXXXXXXX BUTTON UPDATING XXXXXXXXXXXXXXXXXXXXXXXXX **/

int active_gun = 0;
int num_guns = 4;
long gunFireCd = 0;

void updateButtons(){
  
  button_hit.update();
  button_fire.update();
  button_swap.update();
  button_reload.update();
  button_flush.update();
  
  long time = millis();
  
  if (button_fire.read()==LOW && !isDead && time >= gunFireCd) {
  	if (button_fire.fell() || gun_auto[active_gun]){
		writePacket(TRY_FIRE,0,0,255);
		writePacket(FIRE_SUCCESS,1,0,255);
		gunFireCd = time + gun_cd[active_gun];
	}
  }
  
  if (button_hit.fell()) {
    	take_damage(HIT_DAMAGE, ENEMY_PLAYER, ENEMY_TEAM); //All callbacks handled here
  }
  
  if (button_swap.fell()) {
    active_gun = (active_gun < num_guns-1)?active_gun+1:0;
    writePacket(SET_ACTIVE, active_gun, 0, 255);
  }
  
  if (button_reload.fell()){
    writePacket(TRY_RELOAD,0,0,255);
    writePacket(RELOAD_SUCCESS,1,0,255);
  }
  
  if (button_flush.fell()){
    writePacket(FLUSH_SERIAL,FLUSH_SERIAL,FLUSH_SERIAL,FLUSH_SERIAL);
    writePacket(FLUSH_SERIAL,FLUSH_SERIAL,FLUSH_SERIAL,FLUSH_SERIAL);
  }
  
}



/** XXXXXXXXXXXXXXXXXXXXXXX Damage Function XXXXXXXXXXXXXXXXXXXXXXXXX **/
// Deals d damage to the shields
void take_damage(int d, int teamsrc, int playersrc){
      
      if (isDead)
        return;  
  
      shield = (shield>=1000)?shield-1000:0;
      shield_last_damage = millis();
      
      // Write Kill/hit message to serial
      if (shield == 0){
        writePacket(KILLED_BY, teamsrc, playersrc, 255); 
        isDead = 1;
        respawnTime = shield_last_damage + TIME_DEAD;
      } else {
        writePacket(HIT_BY, teamsrc, playersrc, 255); 
      }
}


/** XXXXXXXXXXXXXXXXXXXXXXX Damage Function XXXXXXXXXXXXXXXXXXXXXXXXX **/
void updateRespawn(){
   if (isDead){
     long time = millis();
     if (time>respawnTime){
       isDead = 0;
       shield = 100;
     }
   } 
}
