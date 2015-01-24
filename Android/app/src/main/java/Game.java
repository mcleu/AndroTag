
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




}

