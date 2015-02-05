package evans.dave.androtag;

/**
 * Created by Dave on 19/01/2015.
 */
public class GeneralPlayer extends User {
	
	public static final int INFINITE_LIVES = 255;

    public int RESPAWN_TIMER = 5000;

	public int score;
	public int kills;
    public int deaths;
    public int assists;
    public int lives;
    public boolean inactive;
    public boolean onTeam;
	
	public long respawnTime;
	
	public Team team;

    public final static GeneralPlayer NO_PLAYER = new GeneralPlayer(User.NO_USER, Team.NO_TEAM);
	
	/** Constructor */
    public GeneralPlayer(){
        this(User.NO_USER, Team.NO_TEAM);
    }
    public GeneralPlayer(User u){ this(u, Team.NO_TEAM, 0); }
	public GeneralPlayer(User u, Team t) { this(u,t,0); }
	public GeneralPlayer(User u, Team t, int lives) {
		this.name = u.name;
        this.uid = u.uid;
        this.team = t;
        this.lives = lives;
		
		score = 0;
		kills = 0;
		deaths = 0;
		assists = 0;
		inactive = false;
		onTeam = false;
		respawnTime = 0;
	}

	/** API functions */
	// Checks if a player is dead based on their respawn time
	public boolean isDead() {
		long currentTime = System.currentTimeMillis();
		return currentTime <= respawnTime;
	}

	// Kills a player given the specified milliseconds dead
	public void kill(long millisDead){
		long currentTime = System.currentTimeMillis();
		respawnTime = currentTime + millisDead;
		if (lives != INFINITE_LIVES)
			lives -= (lives>0)?1:0;
	}

	public void killUntil(long respawnTime){
		this.respawnTime = respawnTime;
		if (lives != INFINITE_LIVES)
			lives -= (lives>0)?1:0;
	}

    public void kill() {
        kill(RESPAWN_TIMER);
    }

    public int getID(){
        if (this==GeneralPlayer.NO_PLAYER || team == Team.NO_TEAM){
            return 255;
        }
        int ind = team.players.indexOf(this);
        if (ind < 0 || ind > 254)
            return 255;
        return ind;
    }




}
