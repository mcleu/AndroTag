package evans.dave.duinotag;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ListView;


public class ListViewExampleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listviewexampleactivity);

        final ListView listview = (ListView) findViewById(R.id.listview);
        Gun[] values = new Gun[] { Gun.getLancer(), Gun.getRepeater(), Gun.getVaporizer(), Gun.getBoomstick() };

        final GunOptionAdapter adapter = new GunOptionAdapter(this, values);
        listview.setAdapter(adapter);

    }


}
