package evans.dave.duinotag;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dave on 19/01/2015.
 */
public class User {

    public String name;// evans.dave.duinotag.User name
    public int id;      // Name identifier

    public static User NO_USER = new User("NO_USER", Short.MAX_VALUE);

    public User(String username,int id){
        this.name = username;
        this.id = id;
    }
    public User(String username, String idStr){
        this.name = username;
        this.id = Integer.decode("0x"+idStr);
    }

    public void setID(String idStr){
        id = (int) Short.parseShort(idStr);
    }
    public String getNameFull(){
        return name+Integer.toHexString(id);
    }

    public static final User getFromPrefs(Context ctx){
        return User.getFromPrefs(ctx, ctx.getSharedPreferences(
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
