package evans.dave.duinotag;

import android.app.ActionBar;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.util.LinkedHashMap;


public class LoadoutConfigActivity extends ActionBarActivity {

    private GameSettings game;
    private int[] loadout;
    private int gid;
    private int tid;
    private int pid;

    private ScrollView availableScroll;
    private LinearLayout pickerLayout;
    private LinearLayout loadoutLayout;

    private ImageButton[] loadoutButtons;
    private ImageView[] imageOverlays;

    // Adds the specified gun to the loadout, if possible
    public boolean addGun(int gunInt){
        if (gunInt == 255)
            return false;
        for (int i: loadout)
            if (gunInt == i)
                return false;
        for (int i = 0; i<loadout.length; i++)
            if (loadout[i] == 255){
                loadout[i] = gunInt;
                return true;
            }
        return false;
    }

    // Clears the specified loadout position, if possible
    public boolean removeGun(int gunInd){
        if (loadout[gunInd] != 255) {
            loadout[gunInd] = 255;
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadout_config);

        // Get intent extras
        Bundle extras = getIntent().getExtras();
        gid = extras.getInt("GID");
        tid = extras.getInt("TID");
        pid = extras.getInt("PID");

        // In the future, this will get the game info from the server, for now just create one
        // TODO: Load game config from server
        game = new GameSettings(gid);

        loadout = new int[game.loadoutSize];
        for (int i = 0; i<loadout.length; i++)
            loadout[i] = i;

        // Find the available gun scroll and loadout locations
        availableScroll = (ScrollView) findViewById(R.id.availableScroll);
        pickerLayout = (LinearLayout) findViewById(R.id.pickerLayout);
        loadoutLayout = (LinearLayout) findViewById(R.id.loadoutLayout);

        // Set the loadout buttons

        loadoutButtons = new ImageButton[loadout.length];
        //imageOverlays = new ImageView[loadout.length];
        for (int i = 0; i<loadout.length; i++) {

            // Make a linear layout for padding
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            linearParams.setMargins(7,7,7,7);
            linearParams.gravity = Gravity.CENTER;
            linearLayout.setLayoutParams( linearParams );
            linearLayout.setGravity(Gravity.CENTER);
            loadoutLayout.addView(linearLayout);

            // Add a frame to hold the button and overlayed image
            FrameLayout frameLayout = new FrameLayout(this);
            LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
                    210,210);
            frameParams.setMargins(7,7,7,7);
            frameParams.gravity = Gravity.CENTER;
            frameLayout.setLayoutParams( frameParams );
            frameLayout.setBackgroundResource(R.color.dt_dark_gray);
            linearLayout.addView(frameLayout);

            /*
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                    210, 210);
            frameParams.setMargins(7,7,7,7);*/

            // Add the button to the thing
            loadoutButtons[i] = new ImageButton(this);
            FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                    210, 210);
            buttonParams.gravity = Gravity.CENTER;
            loadoutButtons[i].setLayoutParams(buttonParams);

            frameLayout.addView(loadoutButtons[i]);

            // Set the click listener
            final int buttonID = i;
            loadoutButtons[i].setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadoutButtonClick(v, buttonID);
                }
            });

        }

        updateLoadout();


        // Set the gun picker up
        for (int i = 0; i < game.availableGuns.length; i++){
            // Set it up like thisL
            //          NAME
            // IMG      DESC
            //          STATS

            Gun gun = Gun.getNewForID(game.availableGuns[i]);

            // Make a horizontal linearlayout
            LinearLayout entry = new LinearLayout(this);
            entry.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,10,10,10);
            entry.setLayoutParams(params);
            entry.setBackgroundResource(R.color.dt_slate_gray);
            pickerLayout.addView(entry);

            // Add the gun imagebutton
            ImageButton gunimg = new ImageButton(this);
            gunimg.setBackgroundResource(gun.icon);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(250,250);
            imgParams.gravity = Gravity.CENTER_VERTICAL;
            gunimg.setLayoutParams(imgParams);

            final int GUN_ID = gun.id;
            gunimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    availableButtonClick(v, GUN_ID);
                }
            });

            entry.addView(gunimg);

            // Add the info
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            infoParams.gravity = Gravity.CENTER_VERTICAL;
            info.setLayoutParams(infoParams);
            entry.addView(info);

            TextView name = new TextView(this);
            name.setText(gun.name);
            name.setTextSize(25);
            name.setTextColor(Color.WHITE);
            info.addView(name);

            TextView desc = new TextView(this);
            desc.setText(gun.desc);
            desc.setTextSize(16);
            desc.setTextColor(Color.WHITE);
            info.addView(desc);

            TextView stats = new TextView(this);
            stats.setText(gun.getStatsAsString());
            stats.setTextSize(16);
            stats.setTextColor(Color.WHITE);
            info.addView(stats);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadoutButtonClick(View v, int buttonID){
        loadout[buttonID] = 255;
        updateLoadout();
    }
    public void availableButtonClick(View v, int gunID){

        // Check if there is a free space, and the gun isn't already in the loadout
        int availableSlot = -1;
        for (int i= 0; i < loadout.length; i++){
            if (loadout[i] == 255 && availableSlot == -1)
                availableSlot = i;

            if (loadout[i] == gunID){
                availableSlot = -1;
                break;
            }
        }

        if (availableSlot == -1)
            return;

        loadout[availableSlot] = gunID;
        updateLoadout();
    }

    private void updateLoadout(){
        ImageButton button;
        Gun gun;
        for (int i = 0; i<game.loadoutSize; i++){
            button = loadoutButtons[i];
            gun = Gun.getNewForID(loadout[i]);
            loadoutButtons[i].setBackgroundResource(gun.icon);

        }

    }
}
