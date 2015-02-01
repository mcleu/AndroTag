package evans.dave.duinotag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainGameActivity extends ActionBarActivity {

    private int ui_update_interval = 100;
    private Handler updateHandler;

    AndrotagApplication app;
    User user;
    Player player;
    
    TextView infoText;
    TextView livesText;
    TextView shieldText;
    TextView timeText;
   	ProgressBar shieldBar;
   	ProgressBar ammoBar;
   	
   	ImageButton loadoutButtons[];
   	TextView loadoutAmmos[];
   	FrameLayout loadoutFrames[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        // Set update handler
        updateHandler = new Handler();

        // Get gid, tid, pid, from intent
        Intent intent = getIntent();
        int gid = intent.getIntExtra("gid", 0xffff);
        int tid = intent.getIntExtra("tid", 0xff);
        int pid = intent.getIntExtra("pid", 0xff);

        // Set up application resources
        app = (AndrotagApplication)getApplication();
        app.tid = tid;
        app.pid = pid;

        // Load User account from the preferences file
        user = User.getFromPrefs(this);
        player = new Player(user, Team.NO_TEAM);
        player.id = pid;
        player.loadout = app.game.getNewLoadout(app.loadout);

        app.game.addPlayerToTeam(player, tid);


		// Get various layout items
		infoText 	= (TextView) findViewById(R.id.textView);
		timeText 	= (TextView) findViewById(R.id.textView2);
		shieldText 	= (TextView) findViewById(R.id.textView4);
		livesText 	= (TextView) findViewById(R.id.textView3);
		
		shieldBar 	= (ProgressBar) findViewById(R.id.progressBar);
		ammoBar 	= (ProgressBar) findViewById(R.id.progressBar2);
		
		// Layout Guns
        LinearLayout loadoutLayout = (LinearLayout) findViewById(R.id.loadoutLayout);
        loadoutFrames=  new FrameLayout[app.loadout.length];

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
            loadoutFrames[i] = frameLayout;
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
            loadoutButtons[i].setBackgroundResource(player.loadout[i].icon);

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
			
		// Update the user interface with the data
        setGameInfo();
		updateSimpleUI();
        updateLoadout();

        // Start cts updates
        startRepeatingUpdate();
		
    }

    public void setGameInfo(){
        // Write game info
        infoText.setText(String.format("%04x:%02x:%02x - %s",
                app.game.id,player.team.id, player.id, player.user.name));
    }

    public void updateGameTime(){
        timeText.setText(app.game.getTimeStr());
    }
    public void updateShield(){
        player.update();
        shieldText.setText(String.format("%2d",player.getShield()));
        shieldBar.setProgress(player.getShield());
    }
    public void updateAmmo(){
        player.update();
        ammoBar.setMax(player.getGun().MAX_AMMO);
        ammoBar.setProgress(player.getGun().ammo);
    }

    public void updateLives(){
        if (player.lives == 255)
            livesText.setText("-");
        else
            livesText.setText(String.format("%2d",player.lives));
    }

    public void updateSimpleUI(){
    	updateGameTime();
        updateShield();
        updateLives();
    	// Write player values
    	ammoBar.setMax(player.getGun().MAX_AMMO);
    	ammoBar.setProgress(player.getGun().ammo);
    }
    
    public void updateUI(){
    	
    	// Write game info
    	infoText.setText(String.format("%04x:%02x:%02x - %s",
    					app.game.id,player.team.id, player.id, player.user.name));
    	timeText.setText(app.game.getTimeStr());
    	
    	// Write player values
    	shieldText.setText(String.format("%2d",player.getShield()));
    	livesText.setText(String.format("%2d",player.lives));
    	shieldBar.setProgress(player.getShield());
    	ammoBar.setMax(player.getGun().MAX_AMMO);
    	ammoBar.setProgress(player.getGun().ammo);
    	
    	// TODO: Small leaderboard
    	
    	// TODO: Fragment for log and/or game stats
    	
    }
    
    public void clickShield(View v){
    	player.update();
    	player.damage(15);
    	updateSimpleUI();
    }
    public void clickLives(View v){
    	if (player.lives == 0)
    		player.lives = GeneralPlayer.INFINITE_LIVES;
    	player.kill();
    	updateSimpleUI();
    }
    public void clickAmmo(View v){
    	// Just update the UI here for now
    	updateSimpleUI();
    }

    Runnable updateStatusChecker = new Runnable(){
        @Override
        public void run(){
                updateGameTime();
                updateShield();
                updateHandler.postDelayed(updateStatusChecker, ui_update_interval);
            }
    };

    public void loadoutButtonClick(View v, int buttonID) {
        if (player.loadout[buttonID] == player.getGun()) {
            player.fire();

        } else {
            player.swap();
        }
        updateLoadout();
        updateAmmo();
    }

    private void updateLoadout(){
        ImageButton button;
        Gun gun;
        for (int i = 0; i<player.loadout.length; i++){
            button = loadoutButtons[i];
            gun = player.loadout[i];
            if (gun==player.getGun())
                loadoutFrames[i].setBackgroundResource(R.color.dt_light_gray);
            else
                loadoutFrames[i].setBackgroundResource(R.color.dt_dark_gray);
        }
    }

    void startRepeatingUpdate(){updateStatusChecker.run();}
    void stopRepeatingUpdate() {updateHandler.removeCallbacks(updateStatusChecker);}
}
