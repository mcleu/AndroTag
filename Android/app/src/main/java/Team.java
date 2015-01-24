/**
 * Created by Dave on 19/01/2015.
 */

import android.graphics.Color;

import java.util.Arrays;

import evans.dave.duinotag.R;


public class Team {

    public int id;
    public Color color;
    public String name;

    public static final Team NO_TEAM = new Team(255,new Color(), "NO TEAM");
	
	public GeneralPlayer[] players;
	private boolean[] slots;

	/** Constructor */
    public Team(){
        this(255,new Color(), "NO_TEAM");

    }
	public Team(int id, Color color, String name){
		this.id = id;
		this.color = color;
		this.name = name;
        players = new Player[255];
        slots = new boolean[255];
		Arrays.fill(slots,false);
		Arrays.fill(players,null);
	}
	
	/** Adding/removing players from a team */
	public int getNextSlot() {
		for (int i = 0; i<slots.length; i++)
			if (slots[i])
				return i;
		return -1;
	}

	public boolean add(GeneralPlayer p){
        // Always can add to no team
        if (this == Team.NO_TEAM)
            return true;

		int slot = getNextSlot();
		if (slot < 0 )
            return false;
		
		slots[slot] = true;
		players[slot] = p;
		p.team = this;
        return true;
	}
	public boolean kick(GeneralPlayer p){
        // Always allow leaving NO_TEAM
        if (this == NO_TEAM)
            return true;

        // Add the player to the next available slot
		for (int i = 0; i< players.length; i++){
			if (players[i] == p) {
				players[i] = null;
				slots[i] = false;
				p.team = this;
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPlayer(GeneralPlayer p){
		for (GeneralPlayer p2 : players)
			if (p2==p)
				return true;
		return false;
	}
    public String nameFromID(int id){
        for (GeneralPlayer p : players)
            if (p.id == id)
                return p.user.name;
        return "UNKNOWN_PLAYER";
    }
	
	/** Team Stats */
	public int getKills(){
		int stat = 0;
		for (GeneralPlayer p : players)
			stat += p.kills;
		return stat;
	}
	
	public int getDeaths(){
		int stat = 0;
		for (GeneralPlayer p : players)
			stat += p.deaths;
		return stat;
	}
	
	public int getAssists(){
		int stat = 0;
		for (GeneralPlayer p : players)
			stat += p.assists;
		return stat;
	}

	public int getScore(){
		int stat = 0;
		for (GeneralPlayer p : players)
			stat += p.score;
		return stat;
	}
	

}
