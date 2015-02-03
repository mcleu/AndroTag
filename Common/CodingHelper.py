# Name, Identifier, dtype, ArduinoVarName
OpCodes = [['SET_STATE', 9, 1],
          ['SET_LIVES', 10, 1],
          ['SET_SHIELD', 11, 1],
          ['SET_RESPAWN', 12, 4],
          ['SET_NUM_GUNS', 16, 1],
          ['SET_PLAYER', 20, 1],
          ['SET_TEAM', 21, 1],
          ['SET_COLOR', 22, 1],
          ['SET_GAME', 23, 1],
          ['ADD_ENEMY', 28, 1],
          ['CLEAR_ENEMIES', 29, 0],
          ['SET_GUN_INDEX', 30, 1],

          ['TRY_FIRE', 40, 0],
          ['FIRE_SUCCESS', 41, 1],
          ['TRY_RELOAD', 42, 0],
          ['RELOAD_SUCCESS', 43, 1],

          ['SET_ACTIVE', 45, 1],
          ['NO_LIVES', 46, 1],
          ['HITBY', 48, 1],
          ['KILLEDBY', 49, 1],

          ['SET_TIME', 50, 4],
          ['SET_STARTTIME', 51, 4],
          ['SET_ENDTIME', 52, 4],
          ['END_GAME', 59, 0]]

ArdVars = ['my_state',
          'my_life',
          'my_shield',
          'my_respawn',
          'my_num_guns',
          'my_player',
          'my_team',
          'my_color',
          'my_game',
          '',
          '',
          'my_active',
          'gunFire',
          '',
          'gunReload',
          '',
          'my_active',
          'my_life',
          '',
          '',
          'my_endtime',
          'my_disabled',
          '',
          'my_properties',
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