package evans.dave.androtag.common; /**
 * Created by Dave on 19/01/2015.
 */

import android.graphics.Color;

import java.util.Arrays;
import java.util.List;


public class Team implements Scoring {

    public int color;
    public String name;

    public static final Team NO_TEAM = new Team("NO TEAM", Color.BLACK);
	
	public List<GeneralPlayer> players = Arrays.asList(new GeneralPlayer[254]);
	private boolean[] slots;

	/** Constructor */
    public Team(){
        this("NO_TEAM", Color.BLACK);
    }
	public Team(String name, int color){
		this.color = color;
		this.name = name;
        slots = new boolean[254];
		Arrays.fill(slots,false);
        for (int i = 0; i<players.size(); i++)
            players.set(i,GeneralPlayer.NO_PLAYER);
	}


	/** Adding/removing players from a team */
	public synchronized int getNextSlot() {
		for (int i = 0; i<slots.length; i++)
			if (!slots[i])
				return i;
		return -1;
	}

	public int countPlayers(){
		int count = 0;
		for (boolean slot : slots)
			if (slot) count++;
		return count;
	}

	public synchronized boolean add(GeneralPlayer p){
        // Always can add to no team
        if (this == Team.NO_TEAM)
            return true;

		int slot = getNextSlot();
		if (slot < 0 )
            return false;

		slots[slot] = true;
		players.set(slot, p);
		p.team = this;
        return true;
	}
	public synchronized boolean kick(GeneralPlayer p){
        // Always allow leaving NO_TEAM
        if (this == NO_TEAM)
            return true;

        // Add the player to the next available slot
        int ind = players.indexOf(p);
        if (ind < 0 || ind == 255) return false; //Player not on team

        p.team = Team.NO_TEAM;
        players.set(ind,GeneralPlayer.NO_PLAYER);
        slots[ind] = false;

        return true;
	}
	
	public boolean hasPlayer(GeneralPlayer p){
        if (players.indexOf(p) >= 0)
            return true;
        return false;
	}
    public String nameFromID(int id){
        if (players.get(id) != GeneralPlayer.NO_PLAYER)
            return players.get(id).name;
        return GeneralPlayer.NO_PLAYER.name;
    }
	
	/** Scoring interface functions */
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

    public int getColor(){ return color;}
    public String getName() { return name; }
    public boolean isDead() {return false; }


}
