# Name, Identifier, dtype, ArduinoVarName
OpCodes = [['SET_LIVES', 0, 1],
          ['SET_RESPAWN', 1, 4],
          ['SET_SHIELD', 2, 1],
          ['SET_ACTIVE', 3, 1],
          ['TRY_RELOAD', 4, 0],
          ['TRY_FIRE', 5, 0],
          ['SET_NUM_GUNS', 6, 1],
          ['SET_TEAM', 7, 1],
          ['SET_COLOR', 8, 1],
          ['SET_PLAYER', 9, 1],
          ['SET_GAME', 10, 1],
          ['SET_STARTTIME', 11, 4],
          ['SET_ENDTIME', 12, 4],
          ['SET_DISABLED', 13, 1],
          ['END_GAME', 14, 0],
          ['GUN_PROPERTIES', 15, 1],
          ['FIRE_SUCCESS', 16, 1],
          ['RELOAD_SUCCESS', 17, 1],
          ['NO_LIVES', 18, 1],
          ['HITBY', 19, 1],
          ['KILLEDBY', 20, 1],
          ['ADD_ENEMY', 21, 1],
          ['CLEAR_ENEMIES', 22, 0]]

ArdVars = ['my_life',
          'my_respawn',
          'my_shield',
          'my_active',
          'gunReload',
          'gunFire',
          'my_num_guns',
          'my_team',
          'my_color',
          'my_player',
          'my_game',
          'my_starttime',
          'my_endtime',
          'my_disabled',
          '',
          'my_properties',
          '',
          '',
          '',
          '',
          '',
          '',
          '',
          '',
          '']

#==============================================================================
# Create define for Arduino
#==============================================================================
for OpCode in OpCodes:
    print '#define', OpCode[0], OpCode[1]


#==============================================================================
# Create SerialHandles
#==============================================================================

for OpCode in OpCodes:
    print "//", OpCode[0], OpCode[1]
    print "case", OpCode[0]+":"
    if (OpCode[2]==0):
        if (ArdVars[OpCode[1]]==''):
            print "  // #TODO", OpCode[0]
        else:
            print "  " + ArdVars[OpCode[1]] + "();"

    if (OpCode[2]!=0):
        print "  // Error check for correct number of bytes"
        if (OpCode[2]== 1):
            print "  if (Serial.available() == 1){"
            print "    dataVal = Serial.read();"
            print "    // Error check for valid value (min, max)"
            print "    if(dataVal >= 1 && dataVal <= 255){"
        if (OpCode[2]== 4):
            print "  if (Serial.available() == 4){"
            print "    dataVal = serialReadULong();"
            print "    // Error check for valid value (min, max)"
            print "    if(dataVal >= 1 && dataVal <= 4294967295){"

        print "      " + ArdVars[OpCode[1]] + " = dataVal;"
        print "      Serial.write("+ OpCode[0] +"); // write returns received OpCode"
        print "    }"
        print "    else{ // outside allowed range"
        print '      Serial.println("Serial Data Error: '+ OpCode[0] +' value outside limits.");'
        print "    }"
        print "  }"
        if (OpCode[2]== 1):
            print "  else{ // Serial.available != 1"
            print '    Serial.println("Serial Data Error: '+ OpCode[0] +' expecting byte (1 byte).");'
        if (OpCode[2]== 4):
            print "  else{ // Serial.available != 4"
            print '    Serial.println("Serial Data Error: '+ OpCode[0] +' expecting long (4 bytes).");'
        print "}"
    print "break;"
    print ""