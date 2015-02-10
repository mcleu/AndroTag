package evans.dave.androtag.common;

/**
 * Created by Dave on 09/02/2015.
 */
public enum SerialMessage {

    SET_LIVES (0),
    SET_RESPAWN (1),
    SET_SHIELD (2),
    SET_ACTIVE (3),
    TRY_RELOAD (4),
    TRY_FIRE (5),
    SET_NUM_GUNS (6),
    SET_TEAM (7),
    SET_COLOR (8),
    SET_PLAYER (9),
    SET_GAME (10),
    SET_STARTTIME (11),
    SET_ENDTIME (12),
    SET_DISABLED (13),
    END_GAME (14),
    GUN_PROPERTIES (15),
    FIRE_SUCCESS (16),
    RELOAD_SUCCESS (17),
    NO_LIVES (18),
    HIT_BY (19),
    KILLED_BY (20),
    ADD_ENEMY (21),
    CLEAR_ENEMIES (22),
    FLUSH_SERIAL (254),
    ACK (255);


    private final byte id;

    SerialMessage(int id){
        this.id = (byte) id;
    }

    public boolean equals(byte i){ return id==i; }

    public static SerialMessage getFromByte(byte i){
        for (SerialMessage msg : SerialMessage.values())
            if (msg.equals(i))
                return msg; // return it
        return null; // Not a message
    }




}
