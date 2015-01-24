import java.io.File;

/**
 * Created by Dave on 19/01/2015.
 */
public class Gun {

	/** Gun descriptor variables */
	/* NOTE: READ ONLY */
	public String name;
	public String desc;
	public File icon;
	
	public int id;
	
	public int damage; 
	
	public int ammo;
	public int MAX_AMMO;
	
	public int fireTime;
	public int reloadTime;
	
	public int fireMode; // 0, 1, 2
	
	// Gun current state variables
	private long gunAvailable;
	
	public boolean fire(){
		if (System.currentTimeMillis() < gunAvailable)
			return false;
		if (ammo <=0)
			return false;
		
		ammo -= 1;
		gunAvailable = System.currentTimeMillis();
		if (ammo > 0 ) 
			gunAvailable += fireTime;
		
		return true;
	}
	
	public boolean reload(){
		if (System.currentTimeMillis() < gunAvailable)
			return false;
		if (ammo == MAX_AMMO)
			return false;
		
		gunAvailable = System.currentTimeMillis() + reloadTime;
		ammo = MAX_AMMO;
        return true;
	}


	
	public Gun(String name, String desc, int id, File icon, int damage, int MAX_AMMO, int fireTime, int reloadTime, int fireMode){
		this.name = name;
		this.desc = desc;
		this.id = id;
		this.icon = icon;
		this.damage = damage;
		this.ammo = MAX_AMMO;
		this.MAX_AMMO = MAX_AMMO;
		this.fireTime = fireTime;
		this.reloadTime = reloadTime;
		this.gunAvailable = 0;
        this.fireMode = fireMode;
	}

    public Gun(String name, String desc, int id, int damage, int MAX_AMMO, int fireTime, int reloadTime, int fireMode){
        this(name, desc, id, new File("img/no_icon.png"), damage, MAX_AMMO, fireTime, reloadTime, fireMode);
    }

    public Gun(Gun g){
        this(g.name, g.desc, g.id, g.icon, g.damage, g.MAX_AMMO, g.fireTime, g.reloadTime, g.fireMode);
    }


    public static Gun getLancer(){
        return new Gun( "Lancer",
                "Induction coils prevent excess EM emission to give this rifle moderate damage while " +
                "keeping power usage to a reasonable level.",
                00,         // Identifier
                20,         // Damage/shot
                12,         // Shots/clip
                500,        // time(ms)/shot
                1000,       // time(ms) for reload
                0);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getRepeater(){
        return new Gun( "Repeater",
                "A modified Lancer with larger induction coils passed back into the generation chamber, "+
                "giving this weapon a significantly higher rate of fire at a much lower impact",
                01,         // Identifier
                7,          // Damage/shot
                99,         // Shots/clip
                50,         // time(ms)/shot
                1000,       // time(ms) for reload
                0);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getVaporizer() {
        return new Gun("Vaporizer",
                "Aptly named, a heavy metal slug is magnetically accelerated to hypersonic speeds. " +
                "The heavy slugs are slow to reload, but anyone hit by won't have time to consider that.",
                02,         // Identifier
                45,         // Damage/shot
                4,          // Shots/clip
                750,        // time(ms)/shot
                2000,       // time(ms) for reload
                0);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getBoomstick(){
        return new Gun("Boomstick",
                "The acoustic amplifier in this weapon isn't strong enough to hit anything at a distance," +
                " but once close... big boom, big fun.",
                03,         // Identifier
                60,         // Damage/shot
                6,          // Shots/clip
                500,        // time(ms)/shot
                2000,       // time(ms) for reload
                1);         // Firing mode (0 is normal, 1 is burst)
    }

}
