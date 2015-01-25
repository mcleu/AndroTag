package evans.dave.duinotag;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent intent = new Intent(this,MainGameActivity.class);
        startActivity(intent);
    }


    public void startTest2(View view){
        // Start main game screen
        Intent intent = new Intent(this,TestActivity.class);
        startActivity(intent);
    }

    /** Start testing mode */
    public void startAccount(View view){
        // Start main game screen
        Intent intent = new Intent(this,AccountActivity.class);
        startActivity(intent);
    }
}
