package evans.dave.androtag;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class AccountActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Load from preferences file
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        String default_id = getResources().getString(R.string.saved_id_default);
        String default_name = getResources().getString(R.string.saved_name_default);

        String id = sharedPref.getString(getString(R.string.saved_id),default_id);
        String name = sharedPref.getString(getString(R.string.saved_name),default_name);

        ((EditText)findViewById(R.id.IDEdit)).setText(id);
        ((EditText)findViewById(R.id.unameEdit)).setText(name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
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

    /** return to main screen */
    public void startMain(View view){
        // Read name and uid from prompt
        EditText nameEdit = (EditText) findViewById(R.id.unameEdit);
        EditText idEdit   = (EditText) findViewById(R.id.IDEdit);

        String name = nameEdit.getText().toString();
        String id = idEdit.getText().toString();

        // Save new values
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_name), name);
        editor.putString(getString(R.string.saved_id), id);
        editor.commit();

        // Start main game screen
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
