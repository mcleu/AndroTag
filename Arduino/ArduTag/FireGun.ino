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
