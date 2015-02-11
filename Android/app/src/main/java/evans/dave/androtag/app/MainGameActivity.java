package evans.dave.androtag.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import evans.dave.androtag.R;
import evans.dave.androtag.common.GeneralPlayer;
import evans.dave.androtag.common.Gun;
import evans.dave.androtag.common.Player;
import evans.dave.androtag.common.Scoring;
import evans.dave.androtag.common.Team;
import evans.dave.androtag.common.User;


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
    TextView ammoText;
    ListView scoreboard;
    ScoreboardLineAdapter scoreboardAdapter;
   	
   	ImageButton loadoutButtons[];
   	TextView loadoutAmmos[];
   	FrameLayout loadoutFrames[];

    /* Sound management */
    MediaPlayer gunAudioPlayer;
    SoundPool soundPool;
    int[] gunSoundIds = new int[4];
    int reloadSoundId;
    int clickSoundId;

    /* Serial Management */
    private final static String DATA_RECEIVED_INTENT = "primavera.arduino.intent.action.DATA_RECEIVED";
    private final static String SEND_DATA_INTENT = "primavera.arduino.intent.action.SEND_DATA";
    private final static String DATA_EXTRA = "primavera.arduino.intent.extra.DATA";

    SerialManager serialManager;
    private StringBuilder sb = new StringBuilder();
    TextView text;


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

        // Set up application resources
        app = (AndrotagApplication)getApplication();

        // Load User account from the preferences file
        player = app.player;

        // Load sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        for (int i= 0;  i<player.loadout.length; i++){
            gunSoundIds[i] = soundPool.load(this, player.loadout[i].firingSound, 1);
        }
        reloadSoundId = soundPool.load(this, R.raw.gun_reload, 1);
        clickSoundId = soundPool.load(this, R.raw.gun_click, 1);

		// Get various layout items
		infoText 	= (TextView) findViewById(R.id.textView);
		timeText 	= (TextView) findViewById(R.id.textView2);
		shieldText 	= (TextView) findViewById(R.id.shieldText);
		livesText 	= (TextView) findViewById(R.id.textView3);
        ammoText    = (TextView) findViewById(R.id.ammoText);
        text = (TextView) findViewById(R.id.debug);
		
		shieldBar 	= (ProgressBar) findViewById(R.id.progressBar);
		ammoBar 	= (ProgressBar) findViewById(R.id.progressBar2);

        scoreboard = (ListView) findViewById(R.id.scoreboard);

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
            /*loadoutButtons[i].setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadoutButtonClick(v, buttonID);
                }
            });*/
            loadoutButtons[i].setOnTouchListener(new RepeatListener(100, 100, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadoutButtonClickAuto(v, buttonID);
                }
            }));


            /* Set up Seriallll OH BOY! */
            /*
            serialManager = new SerialManager(this) {
                @Override
                void setShield(int w, int val, int y, int z) {
                    sb.append(String.format("SET_SHIELDS:\t%02x %02x %02x %02x\n",w,val,y,z));
                    text.setText(sb.toString());
                    player.shield = val;
                    shieldText.setText(String.format("%2d", player.shield));
                    shieldBar.setProgress(player.shield);

                }

                @Override
                void tryFire(int a, int b, int c, int d) {
                    sb.append(String.format("TRY_FIRE:\t%02x %02x %02x %02x\n",a,b,c,d));
                    text.setText(sb.toString());
                }

                @Override
                void fireSuccess(int w, int x, int y, int z) {
                    sb.append(String.format("FIRE_SUCC:\t%02x %02x %02x %02x\n",w,x,y,z));
                    text.setText(sb.toString());
                    if (player.fire()) {
                        if (player.getGun().firingSound != 0)
                            soundPool.play(gunSoundIds[player.activeGun],1,1,1,0,1);
                    }
                    updateLoadout();
                    updateAmmo();
                }

                @Override
                void setActive(int w, int x, int y, int z) {
                    sb.append(String.format("SET_ACT:\t%02x %02x %02x %02x\n",w,x,y,z));
                    text.setText(sb.toString());
                    player.swap();
                    updateLoadout();
                    updateAmmo();
                }

                @Override
                void hitBy(int w, int x, int y, int z) {
                    sb.append(String.format("HIT_BY:\t%02x %02x %02x %02x\n",w,x,y,z));
                    text.setText(sb.toString());
                }

                @Override
                void killedBy(int w, int tid, int pid, int z) {
                    sb.append(String.format("KILL_BY:\t%02x %02x %02x %02x\n",w,tid,pid,z));
                    text.setText(sb.toString());
                    player.kill(5000);
                    app.game.getTeam(tid).getPlayer(pid).kills += 1;
                }
            };*/


        }

        /* Set up Seriallll OH BOY! */
        serialManager = new SerialManager(this) {
            @Override
            void setShield(int a, int b, int c, int d) {
                sb.append(String.format("SET_SHIELDS:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());
                player.shield = b;
                updateShield();
            }

            @Override
            void tryFire(int a, int b, int c, int d) {
                sb.append(String.format("TRY_FIRE:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());

            }

            @Override
            void fireSuccess(int a, int b, int c, int d) {
                sb.append(String.format("FIRE_SUCC:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());
                if (player.fire()) {
                    if (player.getGun().firingSound != 0)
                        soundPool.play(gunSoundIds[player.activeGun],1,1,1,0,1);
                }
                updateLoadout();
                updateAmmo();

            }

            @Override
            void tryReload(int a, int b, int c, int d) {
            }

            @Override
            void reloadSuccess(int a, int b, int c, int d) {
                if (player.reload())
                    soundPool.play(reloadSoundId,1,1,1,0,1);
                updateAmmo();
            }

            @Override
            void setActive(int a, int b, int c, int d) {
                sb.append(String.format("SET_ACTIVE:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());
                player.swap();
                updateLoadout();
                updateAmmo();

            }

            @Override
            void hitBy(int a, int b, int c, int d) {
                sb.append(String.format("HIT_BY:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());

            }

            @Override
            void killedBy(int a, int b, int c, int d) {
                sb.append(String.format("KILL_BY:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());
                player.kill(5000);
                app.game.getTeam(b).getPlayer(c).kills += 1;

            }
        };



        IntentFilter filter = new IntentFilter();
        filter.addAction(DATA_RECEIVED_INTENT);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (DATA_RECEIVED_INTENT.equals(action)) {
                    final byte[] data = intent.getByteArrayExtra(DATA_EXTRA);
                    serialManager.addDataCallback(data);


                }
            }
        }, filter);

        /** Make the leaderboard **/
        // TEAM 1
        // TEAM 2
        // TEAM 1 Player 1
        // TEAM 1 Player 2 etc
        ArrayList<Scoring> boardItems = new ArrayList<Scoring>();
        for (Team t: app.game.teams)
                boardItems.add(t);
        for (Team t: app.game.teams)
                for (GeneralPlayer p: t.players)
                    if (p != player.NO_PLAYER)
                        boardItems.add(p);

        scoreboardAdapter = new ScoreboardLineAdapter(this, boardItems);
        scoreboard.setAdapter(scoreboardAdapter);
			
		// Update the user interface with the data
        setGameInfo();
		updateSimpleUI();
        updateLoadout();

        // Start cts UI updates
        //startRepeatingUpdate();


		
    }

    public void setGameInfo(){
        // Write game info
        infoText.setText(String.format("%04x:%02x:%02x - %s",
                app.game.id,app.game.getTeamID(player.team), player.getID(), player.name));
    }

    public void updateGameTime(){
        timeText.setText(app.game.getTimeStr());
    }
    public void updateShield(){
        shieldText.setText(String.format("%2d",player.getShield()));
        shieldBar.setProgress(player.getShield());

        // TODO: get this code working
        /*
        if (newShield == 100)
            shieldAudioPlayer.setState(0); //off
        else if (newShield > oldShield)
            shieldAudioPlayer.setState(2); //regenerating
        else
            shieldAudioPlayer.setState(1); //beeping */

    }
    public void updateAmmo(){
        player.update();
        ammoBar.setMax(player.getGun().MAX_AMMO);
        ammoBar.setProgress(player.getGun().ammo);
        //ammoText.setText(player.getGun().ammo + "/" + player.getGun().MAX_AMMO);
        ammoText.setText(""+player.getGun().ammo);
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
    					app.game.id,app.game.getTeamID(player.team), player.getID(), player.name));
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
    	if (player.reload())
            soundPool.play(reloadSoundId,1,1,1,0,1);//playGunAudio(R.raw.gun_reload);
    }

    Runnable updateStatusChecker = new Runnable(){
        @Override
        public void run(){
                updateGameTime();
                updateShield();
                updateAmmo();
                scoreboardAdapter.notifyDataSetChanged();
                updateHandler.postDelayed(updateStatusChecker, ui_update_interval);
            }
    };

    public void loadoutButtonClick(View v, int buttonID) {
        if (player.loadout[buttonID] == player.getGun()) {
            if (player.fire()) {
                if (player.getGun().firingSound != 0)
                    soundPool.play(gunSoundIds[player.activeGun],1,1,1,0,1);
            } else {
                soundPool.play(clickSoundId,1,1,1,0,1);
            }


        } else {
            player.swap();
        }
        updateLoadout();
        updateAmmo();
    }

    public void loadoutButtonClickAuto(View v, int buttonID) {
        if (player.loadout[buttonID] == player.getGun()) {
            if (player.fire()) {
                if (player.getGun().firingSound != 0)
                    soundPool.play(gunSoundIds[player.activeGun],1,1,1,0,1);
            } else {
                //soundPool.play(clickSoundId,1,1,1,0,1);
            }


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

    protected void playGunAudio(int res){
        if (gunAudioPlayer != null){
            gunAudioPlayer.reset();
            gunAudioPlayer.release();
        }
        Log.v("GunAudio","Playing "+res);
        gunAudioPlayer = MediaPlayer.create(this, res);
        gunAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp == gunAudioPlayer) {
                    gunAudioPlayer.start();
                }
            }
        });
    }

    private class ShieldAudioPlayer{

        private int status;
        private MediaPlayer mp;

        public ShieldAudioPlayer(){
            status = 0;
        }
        public void setState(int state) {
            Log.v("ShieldAudioPlayer","State "+ state + " - " + status + "(" + (mp!=null) + ")");
            if (state == 0){
                // OFF
                if (status != 0 && mp !=null) {
                    mp.reset();
                    mp.release();
                }
                status = 0;
                return;

            } else if (state == 1) {
                // BEEPING
                if (status != 1 && mp != null) {
                    mp.reset();
                    mp.release();
                }
                if (status != 1) {
                    Log.v("ShieldAudioPlayer","IF1 "+ state + " - " + status + "(" + (mp!=null) + ")");
                    mp = MediaPlayer.create(MainGameActivity.this,R.raw.shield_beep);
                    Log.v("ShieldAudioPlayer","IF2 "+ state + " - " + status + "(" + (mp!=null) + ")");
                    mp.start();
                    mp.setLooping(true);
                }
                status = 1;
                return;

            } else if (state == 2) {
                // REGENERATING
                if (status == 1 && mp != null){
                    mp.reset();
                    mp.release();
                    mp = MediaPlayer.create(MainGameActivity.this, R.raw.shield_regen);
                    mp.start();
                    status = 2;
                }
            }

        }




    }



}
