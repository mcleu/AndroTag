package evans.dave.androtag;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class JoinGameActivity extends ActionBarActivity {

    private static final String TAG = "JoinGameActivity";

    private AndrotagApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        app = (AndrotagApplication) getApplication();
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

    public int parseIntFromID(int id, int radix){
        EditText edit = (EditText) findViewById(id);
        //Log.v(TAG, "Parsing: " + edit.getText().toString());
        return Integer.parseInt(edit.getText().toString(),radix);
    }
    public void startLoadout(View view){

        //For now, we will send the game ID, Team ID, and player ID
        // Eventually this will happen at the next screen, automatically

        Intent intent = new Intent(this,LoadoutConfigActivity.class);
        int gid = parseIntFromID(R.id.gidEdit,16);
        int tid = parseIntFromID(R.id.tidEdit,16);
        int pid = parseIntFromID(R.id.pidEdit, 16);
        //intent.putExtra("gid",(int) gid);

        // TODO: Request game join
        // TODO: Get game from server
        // Instead just make the game here
        app.game = new Game(gid);
        app.game.teams.add(new Team("Read Team", Color.RED));
        app.game.teams.add(new Team("Blew Team", Color.BLUE));
        app.game.loadoutSize = 4;

        startActivity(intent);

    }

}
