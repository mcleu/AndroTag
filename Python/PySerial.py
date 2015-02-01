# -*- coding: utf-8 -*-
"""
Created on Fri Jan 23 20:24:18 2015

@author: Michael
"""

import time
import serial
import strut # C structures

class AndroTag(serial.Serial):
    """ Class definition for serial communication to Arduino

        Notes:
        1 - AndroTag is inherited form serial.Serial
        2 - Initialization does not open the serial connection
        3 - Do not directly call the inherited open() method, as
            the Arduino requires reboot time, the connect() method
            allows for this.
        """
    opCode = {
    'SET_LIVES':       0,/
    'SET_RESPAWN':     1,/
    'SET_SHIELD':      2,/
    'SET_ACTIVE':      3,/
    'TRY_RELOAD':      4,/
    'TRY_FIRE':        5,/
    'SET_NUM_GUNS':    6,/
    'SET_TEAM':        7,/
    'SET_COLOR':       8,/
    'SET_PLAYER':      9,/
    'SET_GAME':       10,/
    'SET_STARTTIME':  11,/
    'SET_ENDTIME':    12,/
    'SET_DISABLED':   13,/
    'END_GAME':       14,/
    'GUN_PROPERTIES': 15,/
    'FIRE_SUCCESS':   16,/
    'RELOAD_SUCCESS': 17,/
    'NO_LIVES':       18,/
    'HITBY':          19,/
    'KILLEDBY':       20,/
    'ADD_ENEMY':      21,/
    'CLEAR_ENEMIES':  22/
     }

    fmtCode = {
        'ubyte':    'B', \ # 1 unsigned byte
        'ushort':   'H', \ # 2 unsigned Short
        'ulong':    'L', \ # 4 unsigned long
        'double':   'd'  \ # 8 double
        }

    def __init__(self):
        """Intialize the serial class but do not open connection"""
        serial.Serial.__init__(self, port=None, baudrate=115200, timeout=1.0, writeTimeout=1.0)

    def connect(self, portID):
        """Connect to the specified port and pause for Arduino reboot"""
        portID -= 1
        self.port = portID
        self.open()
        time.sleep(2.5)
        return 1

    def _send(self, key_value, arg, fmt):
        """Private function used to send serial data"""

        cmdString      = struct.pack(self.fmtCode['ubyte'], cmd)
        self.write(cmdString)

        dataString     = struct.pack(fmt, data)
        self.write(dataString)

        return self.readline()


    def setTeam(self, team_id):
        """ Set the team_id, [1, 256] """
        if f >= 0 and f <= 255:
            self._send(self.opCode['SET_TEAM'], team_id, self.fmtCode['ubyte'])

    def setPlayer(self, player_id):
        """ Set the player_id, [1, 256] """
        if f >= 0 and f <= 255:
            self._send(self.opCode['SET_PLAYER'], player_id, self.fmtCode['ubyte'])

    def setGame(self, game_id):
        """ Set the team_id, [1, 256] """
        if f >= 0 and f <= 255:
            self._send(self.opCode['SET_GAME'], game_id, self.fmtCode['ubyte'])

