#include <TimerOne.h>

#define IR_FAR_MASK 0b01
#define IR_NEAR_MASK 0b10
#define IR_OFF_MASK ~(IR_FAR_MASK | IR_NEAR_MASK)

#define PULSEWIDTH 7 //Defined in number of cycles of the IR carrier wave

/* Very important section for interfacing the IR oscillator interrupt */
#define IR_QUEUED
#define IR_SENDING
#define IR_DONE
#define IR_EMPTY
volatile int packet_ir_mask = 0;
volatile int packet_status = IR_EMPTY;
volatile int packet_ptr = 0;
#define PACKET_SIZE 32
volatile long packet_data; // Arduino has 32 bit long

void ir_init(){
	Timer1.initialize();
	Timer1.setPeriod(13);
	Timer1.attachInterrupt(oscillator_isr);
	PORTB &= IR_OFF_MASK; // set IR off
	
}

/* Packet setup: 
	
	START	2	31-30	(11)
	GID 	8	29-22
	TID 	4	21-18
	PID		8	17-10
	GUNID	5	09-05
	EXTRAS	3	04-01
	END		2	01-00	(P1)
	
	P is adjusted so the packet will always have even parity
*/

void send_packet(int sensor, long gid, long tid, long pid, long gunid, long extras){
	
	// Spin until the last packet is done sending 
	// if we overwrote anything it would be very very bad
	while (packet_status == IR_SENDING || packet_status == IR_QUEUED);

	// Set oscillator port mask
	packet_ir_mask = 0;
	packet_ir_mask |= (sensor==1 || sensor == 3)? IR_FAR_MASK : 0; 
	packet_ir_mask |= (sensor==2 || sensor == 3)? IR_NEAR_MASK: 0;
	
	// Set packet data
	// TODO: Check that shift amounts are correct
	packet_data = 	( 11 << 30) |
					(gid << 22) | 
					(tid << 18) |
					(pid << 10) |
					(gunid << 05) |
					(extras << 01) |
					(01);
					
	// Adjust to even parity, getParity returns 0 for even and 1 for odd
	// TODO: Check that parity is properly done
	packet_data |= getParity(packet_data) << 1; // Parity bit is second last
	
	// Reset packet pointer
	packet_ptr = 0;
	
	// Flag the interrupt to fire the packet
	packet_status = IR_QUEUED;
}

void wait_packet(){
	while (packet_status == IR_SENDING || packet_status == IR_QUEUED);
}


int getParity(long x){
	x ^= x >> 16;
	x ^= x >> 8;
	x ^= x >> 4;
	x ^= x >> 2;
	x ^= x >> 1;
	return (~x) & 1;
}

/*  Handles all package sending a recieving  */
// DO NOT MODIFY THE LOCAL VARIABLES BELOW
boolean osc_isr_state = true;
int osc_isr_count = 0;
void oscillator_isr(){
	
	/*-------- Check if a packet has been queued --------*/
	if (packet_status == IR_QUEUED){
		// Prep the packet to send
		packet_status = IR_SENDING;
		osc_isr_count = 0;
		osc_isr_state = true;
	}
	
	
	/*-------- Check if we are sending --------*/
	if (packet_status == IR_SEND {
	
		// Quit if at end of packet
		if (packet_ptr == PACKET_SIZE){
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
			if (osc_isr_state && ((1<<packet_ptr) & packet_data) ) // isolates and checkes current bit
				PORTB |= packet_ir_mask; // Sets masked bits
			else
				PORTB &= IR_OFF_MASK; // Sending a 0
			
		}
		
		// Invert for next time
		osc_isr_state = !osc_isr_state; 
		
		// Increment the counter with rollover
		osc_isr_count++;
		if (osc_isr_count == 2*PULSE_WIDTH){
			osc_isr_count = 0;
			packet_ptr++;
		}
		
	}
}

