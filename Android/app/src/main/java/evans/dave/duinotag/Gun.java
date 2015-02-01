package evans.dave.duinotag;

import java.io.File;

/**
 * Created by Dave on 19/01/2015.
 */
public class Gun {

	/** evans.dave.duinotag.Gun descriptor variables */
	/* NOTE: READ ONLY */
	public String name;
	public String desc;
	public int icon;
	
	public int id;
	
	public int damage; 
	
	public int ammo;
	public int MAX_AMMO;
	
	public int fireTime;
	public int reloadTime;
	
	public int fireMode; // 0, 1, 2
	
	// Gun current state variables
	private long gunAvailable;

    public static final Gun NO_GUN = new Gun("","",255,R.drawable.no_gun,0,0,0,0,0);

    /** Constructors */
    private static final Gun[] allGuns = {
            Gun.getLancer(),
            Gun.getRepeater(),
            Gun.getVaporizer(),
            Gun.getBoomstick()};

    public static final Gun getNewForID(int id){
        for (Gun g : allGuns)
            if (g.id == id)
                return new Gun(g);
        return new Gun(Gun.NO_GUN);
    }
	
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

    public String getStatsAsString(){
        return String.format("%d-%d-%4.2f-%3.1f",damage,MAX_AMMO, 1000.0f/((float)fireTime), ((float) reloadTime )/1000.0 );
    }
    public static final String[] getAllNames(Gun[] gs){
        String s[] = new String[gs.length];
        for (int i=0; i<gs.length; i++)
            s[i] = gs[i].name;
        return s;
    }


	public Gun(String name, String desc, int id, int icon, int damage, int MAX_AMMO, int fireTime, int reloadTime, int fireMode){
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
        this(name, desc, id, R.drawable.no_gun, damage, MAX_AMMO, fireTime, reloadTime, fireMode);
    }

    public Gun(Gun g){
        this(g.name, g.desc, g.id, g.icon, g.damage, g.MAX_AMMO, g.fireTime, g.reloadTime, g.fireMode);
    }


    public static Gun getLancer(){
        return new Gun( "Lancer",
                "Induction coils prevent excess EM emission to give this rifle moderate damage while " +
                "keeping power usage to a reasonable level.",
                00,         // Identifier
                R.drawable.lancer_small,
                20,         // Damage/shot
                12,         // Shots/clip
                500,        // time(ms)/shot
                1000,       // time(ms) for reload
                0);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getRepeater(){
        return new Gun( "Repeater",
                "A modified Lancer with larger induction coils in a feedback loop, "+
                "giving this weapon a significantly higher rate of fire.",
                01,         // Identifier
                R.drawable.repeater_small,
                7,          // Damage/shot
                99,         // Shots/clip
                50,         // time(ms)/shot
                1000,       // time(ms) for reload
                0);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getVaporizer() {
        return new Gun("Vaporizer",
                "Magnetically accelerates a heavy metal slug is to hypersonic speeds. " +
                "The heavy slugs are slow to reload.",
                02,         // Identifier
                R.drawable.vaporizer_small,
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
                R.drawable.burster,
                60,         // Damage/shot
                6,          // Shots/clip
                500,        // time(ms)/shot
                2000,       // time(ms) for reload
                1);         // Firing mode (0 is normal, 1 is burst)
    }

}
