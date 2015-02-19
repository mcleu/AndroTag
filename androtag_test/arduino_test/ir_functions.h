
#ifndef IR_FUNCTIONS_H
#define IR_FUNCTIONS_H

#define IR_QUEUED  0
#define IR_SENDING 1
#define IR_DONE    2
#define IR_EMPTY   3

int ir_get_status();
void ir_init();
void send_packet(int sensor, long gid, long tid, long pid, long gunid, long extras);
void wait_packet();
int getParity(long x);
void oscillator_isr();


#endif
