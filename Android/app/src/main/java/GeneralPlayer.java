import java.util.Date;

/**
 * Created by Dave on 19/01/2015.
 */
public class GeneralPlayer {

    public int RESPAWN_TIMER = 5000;

	public int score;
	public int kills;
    public int deaths;
    public int assists;
    public int lives;
    public boolean inactive;
    public int id;
    public boolean onTeam;
	
	public long respawnTime;
	
	public Team team;
	public User user;
	
	/** Constructor */
    public GeneralPlayer(){
        this(User.NO_USER, Team.NO_TEAM);
    }
	public GeneralPlayer(User u, Team t) { this(u,t,0); }
	public GeneralPlayer(User u, Team t, int lives) {
		this.user = u;
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
	}

	public void killUntil(long respawnTime){
		this.respawnTime = respawnTime;
	}

    public void kill() {
        kill(RESPAWN_TIMER);
    }






}
