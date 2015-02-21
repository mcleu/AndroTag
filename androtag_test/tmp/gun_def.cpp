#include "gun.h"
#include "gun_def.h"

int fire1(Gun g){
    return g.damage;
}
int fire2(Gun g){
    return g.id;
}

Gun gun1 = {00, 10, fire1};
Gun gun2 = {01, 20, fire2};
Gun gun3 = {02, 20, fire1};

Gun mygun = gun1;
Gun gunlist[3] = {gun1, gun2, gun3};

Gun getGun(int id){
    return gunlist[id];
}

