
#ifndef IR_SENDER_H
#define IR_SENDER_H

#define IR_QUEUED  0
#define IR_SENDING 1
#define IR_DONE    2
#define IR_EMPTY   3

#define PULSEWIDTH 30 //Defined in number of cycles of the IR carrier wave
#define CARRIER_PERIOD 13 //Number of us to keep IR on/off for

int ir_get_status();
void ir_init();
void ir_send_packet(int sensor, long gid, long tid, long pid, long gunid, long extras);
void ir_wait_packet();
int get_packet_parity(long x);

#endif
