# -*- coding: utf-8 -*-
"""
Spyder Editor

This temporary script file is located here:
C:\Users\Dave\.spyder2\.temp.py
"""

import serial; 
import sys;

ser = serial.Serial('COM8',9600);

while True:
    s = ord(ser.read());
    if (s==255):
        sys.stdout.write( "\n" )
    else:
        sys.stdout.write("%03d "%s)