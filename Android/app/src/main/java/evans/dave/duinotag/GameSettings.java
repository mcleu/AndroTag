package evans.dave.duinotag;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameSettings implements Parcelable {

    public int id;

    public int loadoutSize;
    public int[] availableGuns = {0,1,2,3};

    public int lives;
    public long startTime;
    public long endTime;

    public int maxScore;


    /** Constructor */
    public GameSettings(){
        this(0, 4, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*60*1000,
                25);
    }
    public GameSettings(int id){
        this(id, 4, 10,
                System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*60*1000,
                25);
    }
    public GameSettings(int id, int loadoutSize, int lives, long startTime, long endTime,
                        int maxScore){
        this.id = id;
        this.loadoutSize = loadoutSize;
        this.lives = lives;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxScore = maxScore;
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

    /** Parcelable functions */
    public int describeContents(){
        return 0;
    }

    // Writing function
    public void writeToParcel(Parcel out, int flags){
        out.writeInt(id);
        out.writeInt(loadoutSize);
        out.writeIntArray(availableGuns);
        out.writeInt(lives);
        out.writeLong(startTime);
        out.writeLong(endTime);
        out.writeInt(maxScore);
    }

    // Reading Function
    public static final Parcelable.Creator<GameSettings> CREATOR = new Parcelable.Creator<GameSettings>() {
        public GameSettings createFromParcel(Parcel in) {
            return new GameSettings(in);
        }

        public GameSettings[] newArray(int size) {
            return new GameSettings[size];
        }
    };

    private GameSettings(Parcel in) {
        id = in.readInt();
        loadoutSize = in.readInt();
        availableGuns = in.createIntArray();
        lives = in.readInt();
        startTime = in.readLong();
        endTime = in.readLong();
        maxScore = in.readInt();
    }


}

