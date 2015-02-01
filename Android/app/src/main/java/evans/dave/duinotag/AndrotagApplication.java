package evans.dave.duinotag;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by Dave on 25/01/2015.
 */
public class AndrotagApplication extends Application {

    public int[] loadout;
    public Game game;
    public int pid;
    public int tid;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
