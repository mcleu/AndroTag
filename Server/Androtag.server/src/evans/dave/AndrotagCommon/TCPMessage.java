package evans.dave.AndrotagCommon;


public class TCPMessage {
	
	public static final byte GET_GAME_LIST = 1; // CS game list pull
	
	public static final byte REQ_JOIN_GAME = 2;  // CS Request to join game
	
	public static final byte REQ_JOIN_TEAM = 3;  // CS Request to join team in game
	
	public static final byte GET_GAME = 4;    // CS Get game for a specific id
	
	public static final byte GAME_UPDATE = 5; // CS game update (deaths, isDead, messages)
	                                          // SC game update (teams, isOver, messages)
	
	public static final byte MAKE_GAME = 6;
	
    public static final byte STRING_MSG = 0; // String escape
    public static final byte CLOSE = -1;
}
