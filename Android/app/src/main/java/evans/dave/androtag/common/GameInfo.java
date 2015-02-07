package evans.dave.androtag.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dave on 01/02/2015.
 */
public class GameInfo implements Serializable {
    public int id;
    public long startTime;
    public long endTime;
    public User creator;

    private static final long serialVersionUID = 0x76000000L;


    public GameInfo getSuper(){
        return new GameInfo(id,startTime,endTime,creator);
    }
    public GameInfo(){
        this(0,System.currentTimeMillis(),
                System.currentTimeMillis()+20*60*1000,
                User.NO_USER);
    }
    public GameInfo(int id, long startTime, long endTime, User creator){
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.creator = creator;
    }

    public String getTimeStr(){
        long currentTime = System.currentTimeMillis();
        if (currentTime < startTime){
            long millis = startTime - currentTime;
            return  String.format("-%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else if (currentTime < endTime)  {
            long millis = endTime - currentTime;
            return  String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            return "OVER";
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }


}
