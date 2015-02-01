package evans.dave.duinotag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class LoadoutConfigActivity2 extends ActionBarActivity {

    AndrotagApplication app;

    private int gid;
    private int currentTeam;

	private Button teamButton;	
	
    private LinearLayout pickerLayout;
    private LinearLayout loadoutLayout;

    private ImageButton[] loadoutButtons;

    // Adds the specified gun to the loadout, if possible
    public boolean addGun(int gunInt){
        if (gunInt == 255)
            return false;
        for (int i: app.loadout)
            if (gunInt == i)
                return false;
        for (int i = 0; i<app.loadout.length; i++)
            if (app.loadout[i] == 255){
                app.loadout[i] = gunInt;
                return true;
            }
        return false;
    }

    // Clears the specified loadout position, if possible
    public boolean removeGun(int gunInd){
        if (app.loadout[gunInd] != 255) {
            app.loadout[gunInd] = 255;
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadout_config);

        // Get application
        app = (AndrotagApplication) getApplication();

        // Get intent extras
        Bundle extras = getIntent().getExtras();
        gid = extras.getInt("gid");

        // In the future, this will get the game info from the server, for now just create one
        // TODO: Load game config from server
        Team teams[] = new Team[2];
        teams[0] = new Team(0,Color.RED,"Red Team");
        teams[1] = new Team(1,Color.BLUE,"Blue Team");
        app.game = new Game(new GameSettings(gid), teams);


        // Update the team button
        teamButton = (Button) findViewById(R.id.teamButton);
        currentTeam = 0;
        updateTeamButton();
        

        app.loadout = new int[app.game.loadoutSize];
        for (int i = 0; i<app.loadout.length; i++)
            app.loadout[i] = i;

        // Find the available gun scroll and loadout locations
        pickerLayout = (LinearLayout) findViewById(R.id.pickerLayout);
        loadoutLayout = (LinearLayout) findViewById(R.id.loadoutLayout);

        // Set the loadout buttons

        loadoutButtons = new ImageButton[app.loadout.length];
        for (int i = 0; i<app.loadout.length; i++) {

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

        /*
        // Set the gun picker up
        for (int i = 0; i < app.game.availableGuns.length; i++){
            // Set it up like thisL
            //          NAME
            // IMG      DESC
            //          STATS

            Gun gun = Gun.getNewForID(app.game.availableGuns[i]);

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
            desc.setTextSize(12);
            desc.setTextColor(Color.WHITE);
            info.addView(desc);

            TextView stats = new TextView(this);
            stats.setText(gun.getStatsAsString());
            stats.setTextSize(12);
            stats.setTextColor(Color.WHITE);
            info.addView(stats);
        }*/

        // Construct listview
        final ListView listview = (ListView) findViewById(R.id.listview);
        Log.v("LoadoutActivity2",listview.toString());
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });



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
        app.loadout[buttonID] = 255;
        updateLoadout();
    }
    public void availableButtonClick(View v, int gunID){
        availableButtonClick(gunID);
    }
    public void availableButtonClick(int gunID){

        // Check if there is a free space, and the gun isn't already in the loadout
        int availableSlot = -1;
        for (int i= 0; i < app.loadout.length; i++){
            if (app.loadout[i] == 255 && availableSlot == -1)
                availableSlot = i;

            if (app.loadout[i] == gunID){
                availableSlot = -1;
                break;
            }
        }

        if (availableSlot == -1)
            return;

        app.loadout[availableSlot] = gunID;
        updateLoadout();
    }

    private void updateLoadout(){
        ImageButton button;
        Gun gun;
        for (int i = 0; i<app.game.loadoutSize; i++){
            button = loadoutButtons[i];
            gun = Gun.getNewForID(app.loadout[i]);
            loadoutButtons[i].setBackgroundResource(gun.icon);

        }

    }
    
    public void nextTeam(View view){
    	currentTeam += 1;
    	currentTeam %= app.game.teams.length;
    	updateTeamButton();
    }
    public void randomTeam(View view){
    	currentTeam = -1;
    	updateTeamButton();
    }
    private void updateTeamButton(){
    	// Make the button either display RANDOM, or the team name + color
    	
    	if (currentTeam == -1){
    		teamButton.setText("Random Team");
            int bgColor = getResources().getColor(R.color.dt_slate_gray);
			teamButton.getBackground().setColorFilter(bgColor,PorterDuff.Mode.SCREEN);

    	} else {
			Team team = app.game.teams[currentTeam];
			// Set button text
			teamButton.setText(team.name + " (" + team.countPlayers() + ")");
			teamButton.getBackground().setColorFilter(team.color,PorterDuff.Mode.SCREEN);
    	}

        teamButton.setTextColor(Color.BLACK);
    }

    public void startGame(View v){
        // TODO: Ping server and ask for a position on team
        // Get team
        Random rand = new Random();
        if (currentTeam==-1){
            // Random select
            currentTeam = rand.nextInt(app.game.teams.length);
        }
        int tid = app.game.teams[currentTeam].id;
        int pid = rand.nextInt(256);

        // Start the Game screen
        Intent intent = new Intent(this,MainGameActivity.class);
        intent.putExtra("gid",(int) gid);
        intent.putExtra("tid",(int) tid);
        intent.putExtra("pid",(int) pid);

        app.tid = tid;
        app.pid = pid;

        startActivity(intent);
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
