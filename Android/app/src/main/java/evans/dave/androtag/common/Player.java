package evans.dave.androtag.common;

/**
 * Created by Dave on 19/01/2015.
 */
public class Player extends GeneralPlayer implements Scoring {

	final static long SHIELD_DELAY = 3000;
	final static long MS_PER_SHIELD = 50;
	
	public int shield;
	
	public int activeGun;
	public long lastDamage;
    private long lastUpdate;
	
	public  Gun[] loadout;
	
	/** Constructor */
    public Player(User u){ this(u, Team.NO_TEAM,10); }
	public Player(User u, Team t) { this(u, t, 10); }
	public Player(User u, Team t, int lives) {
		this.name = u.name;
        this.uid = u.uid;
		this.team = t;
		this.lives = lives;
		
		score = 0;
		kills = 0;
		deaths = 0;
		assists = 0;
		inactive = false;
		respawnTime = 0;
        shield = 100;
		
		activeGun = 0;
		loadout = new Gun[] {Gun.getLancer()};
		lastDamage = System.currentTimeMillis();
        lastUpdate = System.currentTimeMillis();
    }

	/** Standard get/set functions */
	public void setShield(int s) { 
		shield = s;  
		fixShield();
        lastUpdate = System.currentTimeMillis();
	}
	public void modShield(int m) {
        setShield(getShield()+m);
        lastUpdate = System.currentTimeMillis();
    }
	public int getShield() { return shield; }
	
	
	/** ------------------   GUN MANAGEMENT   ------------------ */
	public Gun getGun() {
		return loadout[activeGun];
	}
	
	public void swap() {
        activeGun = (activeGun+1)%loadout.length;
	}

    public void swap(int id){activeGun = (id)%loadout.length;}
	
	public boolean reload() {
        return getGun().reload();
	}

	public void reloadAll() {
		for (Gun g : loadout)
			g.reload();
	}

    public boolean fire() {
        if (inactive | isDead())
            return false;
        return getGun().fire();
    }
	
	
	/** ------------------  DAMAGE MANAGEMENT ------------------ */
	private void fixShield(){ 
		shield = (shield<0)?0:shield;
		shield = (shield>100)?100:shield;
	}
	public void damage(int amount) {
		modShield(-amount);
		lastDamage = System.currentTimeMillis();
        lastUpdate = lastDamage;
		
		/*if (shield <= 0)
			this.kill();*/
	}

	public void update(){
		// Update shield
        /*
        long currentTime = System.currentTimeMillis();
		long timeSinceLastDamage = currentTime-lastDamage;
        long timeSinceLastUpdate = currentTime-lastUpdate;
		
		if (shield <100 && timeSinceLastDamage >= SHIELD_DELAY && !isDead())
			modShield((int) (timeSinceLastUpdate/ MS_PER_SHIELD));
		*/

        lastUpdate = System.currentTimeMillis();
			
	}



	
	
	
	

}
