#include <TimerOne.h>
#include <Arduino.h>
#include "ir_sender.h"

// IR Pins are defined as PB0 and PB1 (arduino ping 8 and 9)
#define IR_FAR_MASK 0b01
#define IR_NEAR_MASK 0b10
#define IR_OFF_MASK ~(IR_FAR_MASK | IR_NEAR_MASK)



/* Very important section for interfacing the IR oscillator interrupt */

volatile int packet_ir_mask = 0;
volatile int packet_status = IR_EMPTY;
volatile long packet_data; // Arduino has 32 bit long


void oscillator_isr();
unsigned long ir_get_mask();

int ir_get_status(){
  return packet_status;
}

void ir_send_init(){
        pinMode(8,OUTPUT);
        pinMode(9,OUTPUT);
	Timer1.initialize();
	Timer1.setPeriod(CARRIER_PERIOD);
	Timer1.attachInterrupt(oscillator_isr);
	PORTB &= IR_OFF_MASK; // set IR off
	
}

/* Packet setup: 
	
	START	2	31-30	(11)
	GID 	8	29-22
	TID 	4	21-18
	PID		8	17-10
	GUNID	5	09-05
	EXTRAS	3	04-02
	END		2	01-00	(P1)
	
	P is adjusted so the packet will always have even parity
*/

void ir_send_packet(int sensor, long gid, long tid, long pid, long gunid, long extras){
	
	// Spin until the last packet is done sending 
	// if we overwrote anything it would be very very bad
	while (packet_status == IR_SENDING || packet_status == IR_QUEUED);

	// Set oscillator port mask
	packet_ir_mask = sensor & 0b11; // 0: off, 1: near, 2: far, 3: both
	
	// Set packet data
	packet_data = 	( ((long)0b11) << 30) |
					(gid << 22) | 
					(tid << 18) |
					(pid << 10) |
					(gunid << 05) |
					(extras << 02) |
					((long)0b01);
					
	// Adjust to even parity, getPacketParity returns 0 for even and 1 for odd
	// TODO: Check that parity is properly done
	packet_data |= (getPacketParity(packet_data) << 1); // Parity bit is second last
        
        Serial.print("SEND: ");
        Serial.println(packet_data,BIN);
		
	// Flag the interrupt to fire the packet
	packet_status = IR_QUEUED;
}

void ir_wait_packet(){
	while (packet_status == IR_SENDING || packet_status == IR_QUEUED);
}


int getPacketParity(long x){
	x ^= x >> 16;
	x ^= x >> 8;
	x ^= x >> 4;
	x ^= x >> 2;
	x ^= x >> 1;
	return x & 1;
}

/*  Handles all package sending a recieving  */
// DO NOT MODIFY THE LOCAL VARIABLES BELOW
boolean osc_isr_state = true;
int osc_isr_count = 0;
unsigned long packet_bitmask = 1L<<31;
void oscillator_isr(){
	
	/*-------- Check if a packet has been queued --------*/
	if (packet_status == IR_QUEUED){
		// Prep the packet to send
                packet_bitmask = 1L<<31;
		packet_status = IR_SENDING;
		osc_isr_count = 0;
		osc_isr_state = true;
	}
	
	
	/*-------- Check if we are sending --------*/
	if (packet_status == IR_SENDING) {
	
		// Quit if at end of packet
		if (packet_bitmask == 0){
			packet_status = IR_DONE;
			PORTB &= IR_OFF_MASK; // Just set off
			return;
		}
		
		// Pulse if not in low segment or broadcasting a 0
		// TODO: Combine into a single if statement
		if (osc_isr_count > PULSEWIDTH){
			PORTB &= IR_OFF_MASK; // Low section of code
		} else {
			// Check to fire
			if (osc_isr_state && ( packet_bitmask & packet_data ) ){ // isolates and checkes current bit
                                PORTB |= packet_ir_mask; // Sets masked bits
			}else{
				PORTB &= IR_OFF_MASK;// Sending a 0
                        }
			
		}
		
		// Invert for next time
		osc_isr_state = !osc_isr_state; 
		
		// Increment the counter with rollover
		osc_isr_count++;
		if (osc_isr_count >= 2*PULSEWIDTH){
			osc_isr_count = 0;
                        osc_isr_state = true;
			packet_bitmask  = packet_bitmask >> 1;
		}
		
	}
}

unsigned long ir_get_mask(){
  return packet_bitmask;
}


