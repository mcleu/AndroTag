package evans.dave.androtag.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import evans.dave.androtag.R;
import evans.dave.androtag.common.Game;
import evans.dave.androtag.common.Player;
import evans.dave.androtag.common.User;

/**
 * Created by Dave on 25/01/2015.
 */
public class AndrotagApplication extends Application {

    public int[] loadout;
    public Game game;
    public Player player;

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

    public static final User loadUser(Context ctx){
        return getFromPrefs(ctx, ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE));
    }
    public static final User getFromPrefs(Context ctx, SharedPreferences sharedPref){

        String default_id = ctx.getResources().getString(R.string.saved_id_default);
        String default_name = ctx.getResources().getString(R.string.saved_name_default);

        String id = sharedPref.getString(ctx.getString(R.string.saved_id),default_id);
        String name = sharedPref.getString(ctx.getString(R.string.saved_name),default_name);

        return new User(name,id);
    }

}
