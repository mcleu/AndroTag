/**
 * Created by Dave on 19/01/2015.
 */
public class Player extends GeneralPlayer {

	final static long SHIELD_DELAY = 3000;
	final static long SHIELD_RATE  = 5;
	
	protected int shield;
	
	public int activeGun;
	public long lastDamage;
	
	public  Gun[] loadout;
	
	/** Constructor */
	public Player(User u, Team t) { this(u, t, 0); }
	public Player(User u, Team t, int lives) {
		this.user = u;
		this.team = t;
		this.lives = lives;
		
		score = 0;
		kills = 0;
		deaths = 0;
		assists = 0;
		inactive = false;
		respawnTime = 0;
		
		activeGun = 0;
		loadout = new Gun[] {Gun.getLancer()};
		lastDamage = System.currentTimeMillis();
	}
	
	
	/** Standard get/set functions */
	public void setShield(int s) { 
		shield = s;  
		fixShield();
	}
	public void modShield(int m) { setShield(getShield()+m); }
	public int getShield() { return shield; }
	
	
	/** ------------------   GUN MANAGEMENT   ------------------ */
	public Gun getGun() {
		return loadout[activeGun];
	}
	
	public void swap() {
        activeGun = (activeGun+1)%loadout.length;
	}
	
	public void reload() {
        getGun().reload();
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
		
		if (shield <= 0)
			this.kill();
	}

	public void update(){
		// Update shield
		long timeSinceLastDamage = System.currentTimeMillis()-lastDamage;
		
		if (shield <100 && timeSinceLastDamage >= SHIELD_DELAY)
			modShield((int) (SHIELD_RATE*(timeSinceLastDamage - SHIELD_DELAY)));
			
	}



	
	
	
	

}
