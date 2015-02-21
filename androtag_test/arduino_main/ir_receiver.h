#include "ir_sender.h"

#ifndef IR_RECEIVER_H
#define IR_RECEIVER_H

/* CONSTANTS */
#define NO_PACKET (~(0L)) // Returned by getPacket when queue is empty
#define BUFFER_SIZE (32)  // Max number of queued packets for processing
#define BIT_MICROS (2*CARRIER_PERIOD*PULSEWIDTH)
#define PACKET_MICROS (31*BIT_MICROS)

#define MAX_BIT_DELAY (PACKET_MICROS*1.5)
#define MIN_BIT_DELAY (BIT_MICROS/2)
#define MAX_PACKET_MICROS (PACKET_MICROS*1.5)
#define MIN_PACKET_MICROS (PACKET_MICROS-0.1*BIT_MICROS)

/* PIN DEFINITIONS */
#define PIN_BODY_RECV  2  // Pin 2
#define INT_BODY_RECV  0  // Corresponds to interrupt 0


/* FUNCTIONS */
void ir_recv_init();
unsigned long ir_pop_packet();
int getPacketGid(unsigned long packet);
int getPacketTid(unsigned long packet);
int getPacketPid(unsigned long packet);
int getPacketGunid(unsigned long packet);
int getPacketExtras(unsigned long packet);

#endif
