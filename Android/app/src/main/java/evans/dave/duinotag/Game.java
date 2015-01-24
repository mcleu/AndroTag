package evans.dave.duinotag;

import android.graphics.Color;

import java.util.concurrent.TimeUnit;

public class Game{
    public int id;

    public int loadoutSize;
    public Gun[] availableGuns = {
            Gun.getLancer(),
            Gun.getRepeater(),
            Gun.getVaporizer(),
            Gun.getBoomstick()};

    public int lives;
    public long startTime;
    public long endTime;
    public Team[] teams;

    public int maxScore;

    public Player player;

    /** Constructor */
    public Game(){
        this(0, 2, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*60*1000,
                new Team[] {new Team(0,Color.RED,"Red evans.dave.duinotag.Team"),new Team(1,Color.BLUE,"Blue evans.dave.duinotag.Team")},
                25,
                new Player(new User("UNKNOWN_PLAYER",0), Team.NO_TEAM));
    }
    public Game(int id, int loadoutSize, int lives, long startTime, long endTime,
                Team[] teams, int maxScore, Player player){
        this.id = id;
        this.loadoutSize = loadoutSize;
        this.lives = lives;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teams = teams;
        this.maxScore = maxScore;
        this.player = player;
    }

    public String getTimeStr(){
        long currentTime = System.currentTimeMillis();
        if (currentTime < startTime){
            long millis = startTime - currentTime;
            return  String.format("-%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else if (currentTime < endTime)  {
            long millis = endTime - currentTime;
            return  String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            return "OVER";
        }
    }

    public boolean isOver(){
        if (System.currentTimeMillis() > endTime)
            return true;
        for (Team t : teams)
            if (t.getScore()>=maxScore)
                return true;
        return false;
    }


}

