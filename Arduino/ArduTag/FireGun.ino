// Active gameplay function
// Fire gun, reload gun
// Can be triggered from Android Serial or button press

boolean gunReload(){
  if (millis() < gunAvailable)  {
    return false;
  }
  if (gunAmmo[my_active] == gunMaxAmmo[my_active]) {
    return false;
  }
  gunAvailable = millis() + gunReloadTime[my_active];
  gunAmmo[my_active] = gunMaxAmmo[my_active];
  return true;
}


boolean gunFire() {
  if (millis() < gunAvailable)  {
    return false;
  }
  if (gunAmmo <= 0) {
    return false;
  }
  
  gunAmmo[my_active] -= 1;
  gunAvailable = millis();
  
  IRFire(gunDammage[my_active]);
  
  gunAvailable += gunFireTime[my_active];
  return true;
}


// Go to next gun available
boolean gunSwap() {
  if (millis() < gunAvailable)  {
    return false;
  }
  
  my_active_gun += 1;
  if (my_active_gun == my_num_guns) {
    my_active_gun = 0;
  }
  return true;
}


void IRFire(int dammage) {
  digitalWrite(IRLASERPIN, HIGH);
  delay(500); // delay for 500 ms temporairly until communcation is set up
  digitalWrite(IRLASERPIN, LOW);
}

// Frequency generation modified from
// http://j44industries.blogspot.com/2009/09/arduino-frequency-generation.html
void IRsendPulse(int length){ //importing variables like this allows for secondary fire modes etc.
  int i = 0;
  int o = 0;
  while( i < length ){
    i++;
    while( o < IRpulses ){
      o++;
      digitalWrite(pin, HIGH);
      delayMicroseconds(IRt);
      digitalWrite(pin, LOW);
      delayMicroseconds(IRt);
    }
  }
}

void frequencyCalculations() {
  IRt = (int) (500/IRfrequency);
  IRpulses = (int) (IRpulse / (2*IRt));
  IRt = IRt - 5;
  // Why -5 I hear you cry. Answer it just worked best (on Pin 3) that way, presumably something to do with the time taken to perform instructions

  Serial.print("Oscilation time period /2: ");
  Serial.println(IRt);
  Serial.print("Pulses: ");
  Serial.println(IRpulses);
  //timeOut = IRpulse + 50;
}

