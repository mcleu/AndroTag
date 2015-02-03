package evans.dave.duinotag;

import android.graphics.Color;

public class Game extends GameSettings{
    public Team[] teams;

    /** Constructor */
    public Game(){
        this(0, 4, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*1000,
                new Team[] {new Team(0,Color.RED,"Red Team"),new Team(1,Color.BLUE,"Blue Team")},
                25);
    }
    public Game(GameSettings g, Team[] teams){
        this(g.id, g.loadoutSize, g.lives,g.startTime, g.endTime,
               teams, g.maxScore);
    }
    public Game(int id, int loadoutSize, int lives, long startTime, long endTime,
                Team[] teams, int maxScore){
        this.id = id;
        this.loadoutSize = loadoutSize;
        this.lives = lives;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teams = teams;
        this.maxScore = maxScore;
    }
    public Game(int id, int loadoutSize, int lives, long startTime, long endTime,
                Team[] teams, int maxScore, User creator){
        this.id = id;
        this.loadoutSize = loadoutSize;
        this.lives = lives;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teams = teams;
        this.maxScore = maxScore;
        this.creator = creator;
    }

    public boolean isOver(){
        if (System.currentTimeMillis() > endTime)
            return true;
        for (Team t : teams)
            if (t.getScore()>=maxScore)
                return true;
        return false;
    }

    public boolean addPlayerToTeam(GeneralPlayer p, int id) {
        for (Team t : teams){
            if (t.id == id) {
                p.team = t;
                return t.add(p);
            }
        }

        return false;
    }

}

