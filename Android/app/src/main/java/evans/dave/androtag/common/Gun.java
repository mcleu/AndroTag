package evans.dave.androtag.common;

import evans.dave.androtag.R;

/**
 * Created by Dave on 19/01/2015.
 */


/**
LOADOUT 1	- 	STANDARD
	2 slots
	Lancer
	Mercury Rifle
	Plasma Coil
	Accelerator
	Magnum
	Pulsar
	Shotgun
	
LOADOUT 2   -   STD EQUIPMENT
	3 slots
	Lancer
	Mercury Rifle
	Plasma Coil
	Accelerator
	Magnum
	Pulsar
	Shotgun
	Shield lock
	Energy Sink
	
LOADOUT 3   -   EXTENDED
	2 slots
	STD +
	Sonos
	Sniper
	Heat Pistol

LOADOUT 4    -   EXT EQuipment
	3 slots
	EXT +
	All equipment except arc charger

LOADOUT 5    -   Human
	
6 Alien?

7 All

8 CUSTOM 1

9 CUSTOM 2

	



*/

/**
 * PLANNED GUN LIST
 *
 * RIFLES ---------------
 *
 *      Lancer          Boring rifle
 *                      Allows headshots
 *                      medium ROF
 *                      ~12 shots/clip
 *                      ~5 shots to kill
 *
 *      Mercury Rifle   Uses heat instead of reload/ammo, on overhead goes on cd for a long while
 *                      Allows headshots
 *                      Can fire very quickly, but overheats quickly if you do so
 *
 *      Plasma Coil     Laser type weapon
 *                      No headshots
 *                      Very high ROF
 *                      Starts with low damage (5-10)
 *                      increased damage if subsequent shots hit (eg. +10 dmg/hit)
 *
 *      Sonos     		Rifle + shotgun combo
 *                      Allows headshots (sniper)
 *                      Lower damage than either shotgun or sniper
 *                      Fires both packets at the same-ish time
 *
 * HE RIFLES ----------
 *
 *      Accelerator     Sniper rifle
 *                      Allows headshots
 *                      2-3 hits normally
 *                      Can hold trigger to charge for firing the remaining clip at once (OHKO usually)
 *
 *
 *      Sniper Rifle    Standard sniper
 *                      Allows headshots
 *                      Needed for people who don't want the cool-better rifles
 *
 * HAND CANNONS ---------
 *
 *      Magnum          Close range pistol
 *                      Allows headshots
 *                      Use packet overlapping to limit range
 *                      Quite high damage and ROF because of limitation
 *
 *      Heat Pistol		Close range pistol
 *						No headshots
 *						Packet overlapping
 *						Heat based
 *						Full Auto
 *
 *		Arc Pistol		Drains shields to give a little more punch
 *						Headshots
 *						Packet overlapping
 *		
 *
 * PARTY KILLERS ---------
 *
 *      Pulsar          Close ranged machine gun
 *                      No headshots
 *                      Random hit
 *
 *      Shotgun         Shotgun
 *                      No headshots
 *                      Incremental reload
 *
 * EQUIPMENT ------------
 *
 *      Shield lock     Invulnerability for 5s, regen if lock too OP
 *
 *		Arc Charger		Medic
 *						Heals allies in burst 10 or some small number
 *
 *      Booster         Higher damage/ always headshot
 *
 *		Energy sink		No outward lights (effectively camo)
 *
 */




public class Gun {

	/** evans.dave.duinotag.Gun descriptor variables */
	/* NOTE: READ ONLY */
	public String name;
	public String desc;

	public int icon;
    public int firingSound;
	
	public int id;
	
	public int damage; 
	
	public int ammo;
	public int MAX_AMMO;
	
	public int fireTime;
	public int reloadTime;
	
	public int fireMode; // 0, 1, 2
	
	// Gun current state variables
	private long gunAvailable;

    public static final Gun NO_GUN = new Gun("","",255, R.drawable.no_gun,0,0,0,0,0,0);

    /** Constructors */
    private static final Gun[] allGuns = {
            Gun.getLancer(),
            Gun.getRepeater(),
            Gun.getVaporizer(),
            Gun.getBoomstick(),
            Gun.getPhaser(),
            Gun.getDelta()};

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

    public static final String[] getAllNames(Gun[] gs){
        String s[] = new String[gs.length];
        for (int i=0; i<gs.length; i++)
            s[i] = gs[i].name;
        return s;
    }


	public Gun(String name, String desc, int id, int icon, int damage, int MAX_AMMO,
               int fireTime, int reloadTime, int fireMode, int firingSound){
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
        this.firingSound = firingSound;
	}

    public Gun(String name, String desc, int id, int damage, int MAX_AMMO, int fireTime, int reloadTime, int fireMode){
        this(name, desc, id, R.drawable.no_gun, damage, MAX_AMMO, fireTime, reloadTime, fireMode,0);
    }

    public Gun(Gun g){
        this(g.name, g.desc, g.id, g.icon, g.damage, g.MAX_AMMO, g.fireTime, g.reloadTime, g.fireMode, g.firingSound);
    }

    public static Gun[] getNewLoadout(int[] loadoutInds){
        Gun ret[] = new Gun[loadoutInds.length];
        for (int i = 0; i < loadoutInds.length; i++)
            ret[i] = getNewForID(loadoutInds[i]);
        return ret;
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
                0,
                R.raw.lancer_fire);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getRepeater(){
        return new Gun( "Repeater",
                "A modified Lancer with larger induction coils in a feedback loop, "+
                "giving this weapon a significantly higher rate of fire.",
                01,         // Identifier
                R.drawable.repeater_small,
                7,          // Damage/shot
                100,         // Shots/clip
                50,         // time(ms)/shot
                1000,       // time(ms) for reload
                0,
                R.raw.repeater_fire);         // Firing mode (0 is normal, 1 is burst)
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
                0,
                R.raw.vaporizer_fire);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getBoomstick(){
        return new Gun("Boomstick",
                "The acoustic amplifier in this weapon isn't strong enough to hit anything at a distance," +
                " but once close... big boom, big fun.",
                03,         // Identifier
                R.drawable.burster,
                60,         // Damage/shot
                5,          // Shots/clip
                550,        // time(ms)/shot
                2100,       // time(ms) for reload
                1,
                R.raw.boomstick_fire);         // Firing mode (0 is normal, 1 is burst)
    }

    public static Gun getPhaser(){
        return new Gun("Phaser",
                "A small power cell is enough to keep this low powered blaster charged a long time. "+
                "Remember to turn off 'stun'.",
                04,
                R.drawable.phaser_small,
                15,
                100,
                300,
                1000,
                1,
                R.raw.lancer_fire);
    }

    public static Gun getDelta(){
        return new Gun("Delta",
                "A heavy hitting pulse rifle using belt fed power cells to keep it hitting hard",
                05,         // Identifier
                R.drawable.delta,
                12,         // Damage/shot
                40,         // Shots/clip
                150,        // time(ms)/shot
                2100,       // time(ms) for reload
                1,
                R.raw.delta_fire);         // Firing mode (0 is normal, 1 is burst)
    }


}
