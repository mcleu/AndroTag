# -*- coding: utf-8 -*-
"""
Spyder Editor

This temporary script file is located here:
C:\Users\Dave\.spyder2\.temp.py
"""

import serial; 
import sys;
import threading

ser = serial.Serial('COM8',9600);

MSG = {
'SET_STATE': 0,
'SET_LIVES': 10,
'SET_SHIELD': 11,
'SET_RESPAWN': 12,
'SET_PID': 20,
'SET_TID': 21,
'SET_GID': 22,
'SET_COLOR': 23,
'ADD_ENEMY': 28,
'CLEAR_ENEMIES': 29,
'SET_NUM_GUNS': 30,
'SET_GUN_0': 31,
'SET_GUN_1': 32,
'SET_GUN_2': 33,
'SET_GUN_3': 34,
'TRY_FIRE': 40,
'FIRE_SUCCESS': 41,
'TRY_RELOAD': 42,
'RELOAD_SUCCESS': 43,
'SET_AMMO': 44,
'SET_ACTIVE': 45,
'NO_LIVES': 46,
'HIT_BY': 48,
'KILLED_BY': 49,
'SET_START_TIME': 50,
'SET_END_TIME': 51,
'END_GAME': 59,
'ACK': 254,
'FLUSH': 255}

def tobytes(s):
    b = []
    for c in s:
        b.append(ord(c))
    return b

def print_verbose(packet):
    code = MSG.keys()[MSG.values().index(packet[0])]
    print '%-15s %3d %3d %3d'%(code,packet[1],packet[2],packet[3])

def readp():
    while ser.inWaiting()>=4:
        s = ser.read(4);
        print_verbose(tobytes(s))
def readc():
    s = ''
    while ser.inWaiting():
        s += ser.read()
    print s,
    
class Reader(threading.Thread):
    def __init__(self,cbf):
        self.__cbf = cbf
        self.__exit = False
        threading.Thread.__init__(self)
    def run (self):
          while(not self.__exit): 
              if ser.inWaiting():
                  self.__cbf()
    def stop (self):
        self.__exit = True

def send(b0,b1,b2=0,b3=0):
    ser.write(bytearray([b0,b1,b2,b3]))
    

