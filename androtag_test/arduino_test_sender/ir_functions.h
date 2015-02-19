
#ifndef IR_FUNCTIONS_H
#define IR_FUNCTIONS_H

#define IR_QUEUED  0
#define IR_SENDING 1
#define IR_DONE    2
#define IR_EMPTY   3

int ir_get_status();
unsigned long ir_get_mask();
void ir_init();
void ir_send_packet(int sensor, long gid, long tid, long pid, long gunid, long extras);
void ir_wait_packet();
int get_packet_parity(long x);
void oscillator_isr();

#endif
