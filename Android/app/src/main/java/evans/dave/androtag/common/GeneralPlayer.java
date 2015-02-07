package evans.dave.androtag.common;

import java.util.Random;

/**
 * Created by Dave on 19/01/2015.
 */
public class GeneralPlayer extends User implements Scoring {
	
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

    public final static GeneralPlayer NO_PLAYER = new GeneralPlayer(NO_USER, Team.NO_TEAM);
	
	/** Constructor */
    public GeneralPlayer(){
        this(NO_USER, Team.NO_TEAM);
    }
    public GeneralPlayer(String name) {this(new User(name,new Random().nextInt(255))); }
    public GeneralPlayer(String name, int id) {this(new User(name,id)); }
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
        deaths += 1;
		if (lives != INFINITE_LIVES)
			lives -= (lives>0)?1:0;
	}

	public void killUntil(long respawnTime){
		this.respawnTime = respawnTime;
        deaths += 1;
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

    /** Getters (Scoring interface) **/
    public int getScore(){ return score; }
    public int getKills(){ return kills; }
    public int getDeaths(){ return deaths; }
    public int getAssists(){ return assists;}
    public int getLives() {return lives; }
    public int getColor(){ return this.team.color; }
    public String getName(){ return this.name; }


    /** Setters **/
    public void setScore(int score) {
        this.score = score;
    }
    public void setKills(int kills) {
        this.kills = kills;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    public void setAssists(int assists) {
        this.assists = assists;
    }
    public void setLives(int lives) {
        this.lives = lives;
    }




}
