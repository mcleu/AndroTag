#ifndef GUN_H
#define GUN_H
typedef struct Gun {
    int id;
    int damage;
    int (* onFire)(struct Gun g);
} Gun;
#endif


