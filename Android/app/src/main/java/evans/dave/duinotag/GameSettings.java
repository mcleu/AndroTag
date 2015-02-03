package evans.dave.duinotag;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.TimeUnit;

public class GameSettings extends GameInfo{

    public int loadoutSize;
    public int[] availableGuns = {0,1,2,3,4,5};

    public int lives;

    public int maxScore;


    /** Constructor */
    public GameSettings(){
        this(0, 4, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*1000,
                25);
    }
    public GameSettings(int id){
        this(id, 4, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*1000,
                25);
    }
    public GameSettings(int id, int loadoutSize, int lives, long startTime, long endTime,
                        int maxScore) {
        this.id = id;
        this.loadoutSize = loadoutSize;
        this.lives = lives;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxScore = maxScore;
    }


}

