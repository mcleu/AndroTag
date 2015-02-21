#include <TimerOne.h>
#include <Arduino.h>
#include "ir_receiver.h"
#include "ir_sender.h" //We need some settings to help decode the messages

/* VARIABLES (SHARED) */
unsigned long packet_buffer[BUFFER_SIZE]; // A FILO buffer, eventually should change to FIFO (harder to implement)
volatile int tail = 0;

int isr_buffer[32];

void ir_body_isr();

/* INITIALIZATION CALLED IN SETUP */ 
void ir_recv_init(){
    
    // Set hardware interrupt for the specified pin
    pinMode(PIN_BODY_RECV,INPUT);
    attachInterrupt(INT_BODY_RECV, ir_body_isr,FALLING);
    
    
    memset(isr_buffer,0,sizeof(isr_buffer));
    memset(packet_buffer,0,sizeof(packet_buffer));
    
}

/* BODY ISR */
volatile unsigned long isr_last = 0;
unsigned long isr_start = 0;
int isr_ptr = 0;

void ir_body_isr(){
    long diff = micros()-isr_last;
    isr_last = micros();
    
    // Check time from last bit
    if (    (diff > MAX_BIT_DELAY) |                        // Exceeded delay for a single bit
            (diff < MIN_BIT_DELAY) |                        // Bit recieved too early
            (isr_last - isr_start > MAX_PACKET_MICROS) |   // Exceeded packet window
            (isr_ptr >= 32)                                 // Somehow packet has more than 32 bits
        )   {
                
        // Flush the buffer
        memset(isr_buffer,0,sizeof(isr_buffer));
        
        // Start a new packet, saving the start time
        isr_start = isr_last;
        // And adding a 1 as the first value
        isr_buffer[0] = 1;
        isr_ptr = 1;
        
        
    } else {
        // A valid bit to add
        // Divide the difference by the BIT_MICROS to get the number bits that have passed
        isr_buffer[isr_ptr] = (((float) diff)/BIT_MICROS+0.5);
        isr_ptr++;
        
        // Check if we're done
        if (isr_last-isr_start >= MIN_PACKET_MICROS){
            // Done, convert the packet to a long
            unsigned long packet = (long)0;
            int ind;
            
            for (ind = 1; ind<isr_ptr; ind++){
                if (isr_buffer[ind] < 1){
                    // Something went wrong
                    packet = NO_PACKET;
                    break;
                }
                packet |= 1L;
                packet = packet << ( isr_buffer[ind] );
            }
            
            // Last bit is always a 1
            packet |= 1L;
            
            // Send to buffer
            if (tail < BUFFER_SIZE){
                packet_buffer[tail] = packet;
                tail++;
            }
            
            // Clear this packet
            memset(isr_buffer,0,sizeof(isr_buffer));
            isr_last = 0;
            isr_buffer[0] = 1;
            isr_ptr = 1;
            
        } /* END READ PACKET */
    }
    
}/* END ISR */


unsigned long ir_pop_packet(){
    // Disable interrupts to we can modify the queue. Should be fast
    unsigned long packet;
    
    noInterrupts();
    //---------------------------------
    if (tail==0) {
        packet = NO_PACKET;
    } else {
        tail--;
        packet = packet_buffer[tail];
    }
    
    //---------------------------------
    interrupts();
    
    return packet;
    
       
}


/* Packet setup: 
	
	START	2	31-30	(11)
	GID 	8	29-22
	TID 	4	21-18
	PID	8	17-10
	GUNID	5	09-05
	EXTRAS	3	04-02
	END		2	01-00	(P1)
	
	P is adjusted so the packet will always have even parity
*/
int getPacketGid(unsigned long packet){ return (int) (packet>>22 & 0b11111111); }
int getPacketTid(unsigned long packet){ return (int) (packet>>18 & 0b1111); }
int getPacketPid(unsigned long packet){ return (int) (packet>>10 & 0b11111111); }
int getPacketGunid(unsigned long packet){ return (int) (packet>>5 & 0b11111); }
int getPacketExtras(unsigned long packet){ return (int) (packet>>2 & 0b111); }



