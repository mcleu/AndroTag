import android.graphics.Color;

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
                new Team[] {new Team(0,Color.RED,"Red Team"),new Team(1,Color.BLUE,"Blue Team")},
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




}

