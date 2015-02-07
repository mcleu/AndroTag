package evans.dave.androtag.app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import evans.dave.androtag.R;
import evans.dave.androtag.common.Game;
import evans.dave.androtag.common.Team;


public class MainActivity extends ActionBarActivity {

    AndrotagApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get application and initialize everything
        app = (AndrotagApplication) getApplication();
        Team teams[] = new Team[2];
        teams[0] = new Team("Red Team", Color.RED);
        teams[1] = new Team("Blue Team", Color.BLUE);
        app.game = new Game(0xFFFF, teams); //TODO: Use better constructor here
        app.loadout = new int[2];
        app.loadout[0] = 0;
        app.loadout[1] = 1;


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


    /** Start Join Game */
    public void startJoin(View view){
        // Start main game screen
        Intent intent = new Intent(this,JoinGameActivity.class);
        startActivity(intent);
    }

    /** Start testing mode */
    public void startTest(View view){
        // Start main game screen
        Intent intent = new Intent(this,ListViewExampleActivity.class);
        intent.putExtra("gid",(int) 15);
        startActivity(intent);
    }


    /** Start testing mode */
    public void startAccount(View view){
        // Start main game screen
        Intent intent = new Intent(this,AccountActivity.class);
        startActivity(intent);
    }
}
