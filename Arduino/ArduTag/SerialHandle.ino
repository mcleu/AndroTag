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
      
    //Adjust DDS tuning word
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
          Serial.println("Serial Data Error: SET_LIVES code outside limits (0, 255).");
        }
      }
      else{//Serial.available != 1
        Serial.println("Serial Data Error: SET_LIVES expecting byte (1 bytes).");
      }
      break;
      
   
      
    // Set Number of triggers desired 
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
          Serial.println("Serial Data Error: Trig value outside limits (0, 100).");
        }
      }
      else{//Serial.available != 2
        Serial.println("Serial Data Error: Trig adjustment expecting float (4 bytes).");
      }
      break;


      
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
