package evans.dave.androtag.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import evans.dave.androtag.common.SerialMessage;
import evans.dave.androtag.common.Team;


public class MainGameActivity extends ActionBarActivity {

    private int ui_update_interval = 100;
    private Handler updateHandler;

    AndrotagApplication app;
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
   	FrameLayout loadoutFrames[];

    /* Sound management */
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

        }

        /* Write config to gun */
        sendPacket(SerialMessage.SET_NUM_GUNS.getId(), app.loadout.length);
        for (int i = 0; i<app.loadout.length; i++) {
            sendPacket(SerialMessage.SET_GUN_0.getId()+i, player.loadout[i].id);
        }
        sendPacket(SerialMessage.SET_GID.getId(), app.game.id);
        sendPacket(SerialMessage.SET_TID.getId(), app.game.getTeamID(player.team));
        sendPacket(SerialMessage.SET_PID.getId(), player.getID());

        /* Set up Seriallll OH BOY! */
        serialManager = new SerialManager(this) {
            @Override
            void runPacket(int a0, int a1, int a2, int a3) {

                switch (SerialMessage.getFromByte((byte) a0)){
                    case SET_SHIELD:
                        sb.append(String.format("SET_SHIELDS:\t%02x %02x %02x %02x\n",a0,a1,a2,a3));
                        text.setText(sb.toString());
                        player.shield = a1;
                        updateShield();
                        break;

                    case FIRE_SUCCESS:
                        if (a1!=0)
                            if (player.getGun().firingSound != 0)
                                soundPool.play(gunSoundIds[player.activeGun],1,1,1,0,1);
                        break;

                    case RELOAD_SUCCESS:
                        if (a1 != 0)
                            soundPool.play(reloadSoundId,1,1,1,0,1);
                        break;

                    case SET_ACTIVE:
                        player.swap(a1);
                        updateAmmo();
                        updateLoadout();
                        break;

                    case SET_AMMO:
                        player.getGun().ammo = a1;
                        updateAmmo();
                        break;

                    case HIT_BY:
                        sb.append(String.format("HIT_BY:\t%02x %02x %02x %02x\n",a0,a1,a2,a3));
                        text.setText(sb.toString());
                        break;

                    case KILLED_BY:
                        sb.append(String.format("KILLED_BY:\t%02x %02x %02x %02x\n",a0,a1,a2,a3));
                        text.setText(sb.toString());
                        app.game.getTeam(a1).getPlayer(a2).kills += 1;
                        player.kill(5000);
                        break;

                }

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
        ArrayList<Scoring> boardItems = new ArrayList<>();
        for (Team t: app.game.teams)
                boardItems.add(t);
        for (Team t: app.game.teams)
                for (GeneralPlayer p: t.players)
                    if (p != Player.NO_PLAYER)
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

    public void sendPacket(int a0, int a1) { sendPacket((byte) a0,(byte) a1,(byte)0,(byte)0);}
    public void sendPacket(byte a0, byte a1, byte a2, byte a3) {
        Intent intent = new Intent(SEND_DATA_INTENT);
        final byte[] data = {a0,a1,a2,a3};
        intent.putExtra(DATA_EXTRA, data);
        sendBroadcast(intent);
    }



}
