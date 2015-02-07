package evans.dave.androtag.common;

import java.util.ArrayList;

public class Game extends GameInfo{
    public ArrayList<Team> teams = new ArrayList<>();
    public int loadoutSize;
    public int[] availableGuns = {0,1,2,3,4,5};
    public int lives;
    public int maxScore;

    /** Constructors */

    // Standard
    public Game(int id, int loadoutSize, int lives, long startTime, long endTime, ArrayList<Team> teams, int maxScore, User creator){
        this.id = id;
        this.loadoutSize = loadoutSize;
        this.lives = lives;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teams = teams;
        this.maxScore = maxScore;
        this.creator = creator;
    }

    // Default
    public Game(){
        this(0, 4, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*1000,
                new ArrayList<Team>(),
                25,
                User.NO_USER);
    }

    // Just gid
    public Game(int gid){
        this();
        this.id = gid;
    }

    // Just teams + gid
    public Game(int gid, Team[] teams){
        this();
        this.id = gid;
        for (Team t : teams) this.teams.add(t);
    }


    // No team delcaration
    public Game(int id, int loadoutSize, int lives, long startTime, long endTime,int maxScore, User creator){
        this (id, loadoutSize,lives,startTime,endTime,new ArrayList<Team>(),maxScore,creator);
    }

    // Array implementation
    public Game(int id, int loadoutSize, int lives, long startTime, long endTime,
                Team[] teams, int maxScore, User creator){
        this (id, loadoutSize,lives,startTime,endTime,maxScore,creator);
        for (Team t : teams) this.teams.add(t);

    }

    // ** Other methods **/
    public boolean isOver(){
        if (System.currentTimeMillis() > endTime)
            return true;
        for (Team t : teams)
            if (t.getScore()>=maxScore)
                return true;
        return false;
    }

    public boolean addPlayerToTeam(GeneralPlayer p, int id) {
        if (id >=0 && id < teams.size() ) {
            return teams.get(id).add(p);
        } else {
            return false;
        }
    }

    public Team getTeam(int i){
        return teams.get(i);
    }
    public int getTeamID(Team t) { return teams.indexOf(t); }
    public int numTeams(){
        return teams.size();
    }

}

