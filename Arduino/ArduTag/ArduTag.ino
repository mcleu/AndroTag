/*
ArduTag
Arduino code for AndroTag. See link below for complete project
https://github.com/mcleung/AndroTag

V0.0a
20160123 V0.0a    First commit. Not working yet!

Michael Leung
mcleung@stanford.edu
*/

// Pinout Map
// Hardware outputs
#define IRLASERPIN  13
#define IRLAMPPIN   12
#define IRBODYLED   11
#define IRSENSOR    A0

// Hardware inputs
#define IRSENSOR    A0
#define TRIGBUT     7
#define RELOADBUT   6
#define SWAPBUT     5

// Hardcoded for Arduino Uno
#define SDAPIN      A4
#define SCLPIN      A5

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
#define HITBY 19
#define KILLEDBY 20
#define ADD_ENEMY 21
#define CLEAR_ENEMIES 22


// Internal Variables
volatile byte my_life = 255;
unsigned long my_respawn = 5000;
volatile byte my_shield = 255;
byte my_active = 1;
byte my_num_guns = 4;
byte my_team = 0;
byte my_color = 0;
byte my_player = 0;
byte my_game = 0;
unsigned long my_starttime;
unsigned long my_endtime;  
boolean my_disabled = 1;

volatile long gun_firetime = 300;
volatile long gun_reloadtime = 5000;


void setup() {
  // initialize serial:
  Serial.begin(115200);
  
  Serial.println("Begin Initialisation");
  initPins();
  Serial.println("Init Complete");
}

void initPins(){
  pinMode(IRLASERPIN, OUTPUT);
  pinMode(IRLAMPPIN, OUTPUT);
  pinMode(IRBODYLED, OUTPUT);
  pinMode(IRSENSOR, OUTPUT);
  
  // Short to ground when "pushed"
  pinMode(IRSENSOR, OUTPUT);
  // digitalWrite(IRSENSOR, HIGH);
  pinMode(TRIGBUT, OUTPUT);
  digitalWrite(TRIGBUT, HIGH); // turn on pullup resistors
  pinMode(RELOADBUT, OUTPUT);
  digitalWrite(RELOADBUT, HIGH);
  pinMode(SWAPBUT, OUTPUT);
  digitalWrite(SWAPBUT, HIGH);
}

void loop() {
  
  // Check for new instructions on the serial port
  if (Serial.available() > 0) 
  {
    readSerial();
  }
  checkHit();
}
