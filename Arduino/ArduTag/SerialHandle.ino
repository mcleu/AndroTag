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
/////////////////
// BEGIN PASTE //
/////////////////


// SET_LIVES 0
case SET_LIVES:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_life = dataVal;
      Serial.write(SET_LIVES); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_LIVES value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_LIVES expecting byte (1 byte).");
}
break;
// SET_RESPAWN 1
case SET_RESPAWN:
  // Error check for correct number of bytes
  if (Serial.available() == 4){
    dataVal = serialReadULong();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 4294967295){
      my_respawn = dataVal;
      Serial.write(SET_RESPAWN); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_RESPAWN value outside limits.");
    }
  }
  else{ // Serial.available != 4
    Serial.println("Serial Data Error: SET_RESPAWN expecting long (4 bytes).");
}
break;
// SET_SHIELD 2
case SET_SHIELD:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_shield = dataVal;
      Serial.write(SET_SHIELD); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_SHIELD value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_SHIELD expecting byte (1 byte).");
}
break;
// SET_ACTIVE 3
case SET_ACTIVE:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_active = dataVal;
      Serial.write(SET_ACTIVE); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_ACTIVE value outside limits.");
    }
  }
  else{ // Serial.available != 1
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

// SET_NUM_GUNS 6
case SET_NUM_GUNS:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_num_guns = dataVal;
      Serial.write(SET_NUM_GUNS); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_NUM_GUNS value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_NUM_GUNS expecting byte (1 byte).");
}
break;

// SET_TEAM 7
case SET_TEAM:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_team = dataVal;
      Serial.write(SET_TEAM); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_TEAM value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_TEAM expecting byte (1 byte).");
}
break;

// SET_COLOR 8
case SET_COLOR:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_color = dataVal;
      Serial.write(SET_COLOR); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_COLOR value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_COLOR expecting byte (1 byte).");
}
break;

// SET_PLAYER 9
case SET_PLAYER:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_player = dataVal;
      Serial.write(SET_PLAYER); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_PLAYER value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_PLAYER expecting byte (1 byte).");
}
break;

// SET_GAME 10
case SET_GAME:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_game = dataVal;
      Serial.write(SET_GAME); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_GAME value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_GAME expecting byte (1 byte).");
}
break;

// SET_STARTTIME 11
case SET_STARTTIME:
  // Error check for correct number of bytes
  if (Serial.available() == 4){
    dataVal = serialReadULong();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 4294967295){
      my_starttime = dataVal;
      Serial.write(SET_STARTTIME); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_STARTTIME value outside limits.");
    }
  }
  else{ // Serial.available != 4
    Serial.println("Serial Data Error: SET_STARTTIME expecting long (4 bytes).");
}
break;

// SET_ENDTIME 12
case SET_ENDTIME:
  // Error check for correct number of bytes
  if (Serial.available() == 4){
    dataVal = serialReadULong();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 4294967295){
      my_endtime = dataVal;
      Serial.write(SET_ENDTIME); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_ENDTIME value outside limits.");
    }
  }
  else{ // Serial.available != 4
    Serial.println("Serial Data Error: SET_ENDTIME expecting long (4 bytes).");
}
break;

// SET_DISABLED 13
case SET_DISABLED:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_disabled = dataVal;
      Serial.write(SET_DISABLED); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: SET_DISABLED value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: SET_DISABLED expecting byte (1 byte).");
}
break;

// END_GAME 14
case END_GAME:
  // #TODO END_GAME
break;

// GUN_PROPERTIES 15
case GUN_PROPERTIES:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
      my_properties = dataVal;
      Serial.write(GUN_PROPERTIES); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: GUN_PROPERTIES value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: GUN_PROPERTIES expecting byte (1 byte).");
}
break;

// ADD_ENEMY 21
case ADD_ENEMY:
  // Error check for correct number of bytes
  if (Serial.available() == 1){
    dataVal = Serial.read();
    // Error check for valid value (min, max)
    if(dataVal >= 1 && dataVal <= 255){
       addEmemy(dataVal);
      Serial.write(ADD_ENEMY); // write returns received OpCode
    }
    else{ // outside allowed range
      Serial.println("Serial Data Error: ADD_ENEMY value outside limits.");
    }
  }
  else{ // Serial.available != 1
    Serial.println("Serial Data Error: ADD_ENEMY expecting byte (1 byte).");
}
break;
// CLEAR_ENEMIES 22
case CLEAR_ENEMIES:
  clearEnemy();
break;




///////////////
// END PASTE //
///////////////


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
