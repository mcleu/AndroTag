// Serial //

void readSerial()  {
  uint16_t  dataCode = 0;
  uint8_t   echoCode = 0;
  float     dataVal  = 0.0;
  
  //Some serial instruction has been recieved.    
  //Delay to ensure full data stream is recieved
  delay(100);
    
  //First byte is always the operation code
  byte opCode  = Serial.read();
    
  switch (opCode) {
      
    // SET_LIVES 0
    case SET_LIVES:
      // do some error check for correct number of bytes
      if (Serial.available() == 1){
        dataVal = Serial.read();
        // do some error check for valid frequency value (min, max)
        if(dataVal >= 0 && dataVal <= 255){
          // Update Desired frequency
          my_life = dataVal;
          Serial.write(SET_LIVES);
        }
        else{ // outside allowed range
          Serial.println("Serial Data Error: SET_LIVES outside limits (0, 255).");
        }
      }
      else{//Serial.available != 1
        Serial.println("Serial Data Error: SET_LIVES expecting byte (1 byte).");
      }
      break;
      
   
      
    // SET_RESPAWN 1 
    case SET_RESPAWN:
      // do some error check for correct number of bytes
      if (Serial.available() == 4){
        dataVal = serialReadULong();
        // do some error check for valid value (min, max)
        if(dataVal >= 1 && dataVal <= 60000){
          // Update the number of triggers to desired value
          my_respawn = dataVal;

          Serial.write(SET_RESPAWN); // write returns one byte
        }
        else{//Frequency outside allowed range
          Serial.println("Serial Data Error: SET_RESPAWN outside limits (0, 60000).");
        }
      }
      else{//Serial.available != 2
        Serial.println("Serial Data Error: SET_RESPAWN expecting long (4 bytes).");
      }
      break;

    // SET_SHIELD 2
    case SET_SHIELD:
      // do some error check for correct number of bytes
      if (Serial.available() == 1){
        dataVal = Serial.read();
        // do some error check for valid value (min, max)
        if(dataVal >= 1 && dataVal <= 255){
          // Update the number of triggers to desired value
          my_shield = dataVal;

          Serial.write(SET_SHIELD); // write returns one byte
        }
        else{//Frequency outside allowed range
          Serial.println("Serial Data Error: SET_SHIELD outside limits (0, 255).");
        }
      }
      else{//Serial.available != 2
        Serial.println("Serial Data Error: SET_RESPAWN expecting byte (1 byte).");
      }
      break;

    // SET_ACTIVE 3
    case SET_ACTIVE:
      // do some error check for correct number of bytes
      if (Serial.available() == 1){
        dataVal = Serial.read();
        // do some error check for valid value (min, max)
        if(dataVal >= 1 && dataVal <= my_num_guns){
          // Update the number of triggers to desired value
          my_active  = dataVal;

          Serial.write(SET_ACTIVE); // write returns one byte
        }
        else{//Frequency outside allowed range
          Serial.println("Serial Data Error: SET_ACTIVE value outside limits (0, my_num_guns).");
        }
      }
      else{//Serial.available != 2
        Serial.println("Serial Data Error: SET_ACTIVE expecting byte (1 byte).");
      }
      break;

    // TRY_RELOAD 4
    case TRY_RELOAD:
    gunReload();
      break;
    
    // TRY_FIRE 5
    case TRY_FIRE:
    gunFire();
      break;


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
      
    //Invalid operation code!!
    default:
      Serial.print("Serial Data Error: Invalid operation code.\n");
    break;
  }
  
  //Clear unsused bytes from the buffer
  //This should only occur from transmission errors
  byte dummy;
  while(Serial.available() > 0){
    dummy = Serial.read();
  }
  
}

unsigned short serialReadUShort() {
  union u_tag {
    byte b[2];
    unsigned short ulval;
  } u;
  u.b[0] = Serial.read();
  u.b[1] = Serial.read();
  return u.ulval;
}

double serialReadULong() {
  union u_tag {
    byte b[4];
    unsigned long dval;
  } u;
  u.b[0] = Serial.read();
  u.b[1] = Serial.read();
  u.b[2] = Serial.read();
  u.b[3] = Serial.read();
  return u.dval;
}
