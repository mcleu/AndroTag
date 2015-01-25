package evans.dave.duinotag;

import android.app.ActionBar;
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

import org.xmlpull.v1.XmlPullParser;


public class LoadoutConfigActivity extends ActionBarActivity {

    private GameSettings game;
    private int[] loadout;
    private int gid;
    private int tid;
    private int pid;

    private ScrollView availableScroll;
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
        loadoutLayout = (LinearLayout) findViewById(R.id.loadoutLayout);

        // Set the loadout buttons

        loadoutButtons = new ImageButton[loadout.length];
        imageOverlays = new ImageView[loadout.length];
        for (int i = 0; i<loadout.length; i++) {

            // Make a linear layout for padding
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            linearParams.setMargins(7,7,7,7);
            linearParams.gravity = Gravity.CENTER;
            linearLayout.setLayoutParams( linearParams );
            loadoutLayout.addView(linearLayout);

            // Add a frame to hold the button and overlayed image
            FrameLayout frameLayout = new FrameLayout(this);
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                    210, 210);
            frameParams.setMargins(7,7,7,7);
            frameLayout.setLayoutParams( linearParams );
            frameLayout.setBackgroundResource(R.color.dt_dark_gray);
            linearLayout.addView(frameLayout);

            // Add the button to the thing
            loadoutButtons[i] = new ImageButton(this);
            FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
            loadoutButtons[i].setLayoutParams(frameParams);

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

        /*
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                210,210);
        params.setMargins(10,10,10,10);
        params.gravity= Gravity.CENTER;

        loadoutButtons = new ImageButton[loadout.length];
        for (int i = 0; i<loadout.length; i++) {
            loadoutButtons[i] = new ImageButton(this);

            // Properly sets button style
            loadoutButtons[i].setBackgroundResource(R.drawable.dt_button_2);
            // Does shows up as a grey square
            loadoutButtons[i].setImageResource(R.drawable.test);

            loadoutButtons[i].setLayoutParams(params);
            loadoutLayout.addView(loadoutButtons[i]);
        } */


        //updateAvailable();
        //updateLoadout();

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

    private void updateAvailable(){
        //TODO: Function stub
    }

    public void loadoutButtonClick(View v, int buttonID){
        loadout[buttonID] = 255;
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
