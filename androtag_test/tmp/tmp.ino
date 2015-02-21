#include "gun_def.h"

void setup(){
    Serial.begin(9600);
    Serial.println("TEST");
}

void loop(){
    
    Serial.println(mygun.damage);
    
    Serial.println(mygun.onFire(mygun));
    
    Serial.println(getGun(1).onFire(getGun(1)));
    
}
