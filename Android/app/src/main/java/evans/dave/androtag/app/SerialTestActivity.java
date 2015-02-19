package evans.dave.androtag.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import evans.dave.androtag.R;

public class SerialTestActivity extends ActionBarActivity {

    private final static String DATA_RECEIVED_INTENT = "primavera.arduino.intent.action.DATA_RECEIVED";
    private final static String SEND_DATA_INTENT = "primavera.arduino.intent.action.SEND_DATA";
    private final static String DATA_EXTRA = "primavera.arduino.intent.extra.DATA";
    private final static byte[] DATA = {0x48,0x65,0x6c,0x6c,0x6f,0x20,0x77,0x6f,0x72,0x6c,0x64,0x21};

    private StringBuilder sb = new StringBuilder();
    TextView text;


    SerialManager serialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_test);

        text = (TextView) findViewById(R.id.received_data);

            /* Set up Seriallll OH BOY! */
        serialManager = new SerialManager(this) {
            @Override
            void setShield(int a, int b, int c, int d) {
                sb.append(String.format("SET_SHIELDS:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());
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

            }

            @Override
            void tryReload(int a, int b, int c, int d) {

            }

            @Override
            void reloadSuccess(int a, int b, int c, int d) {

            }

            @Override
            void setActive(int a, int b, int c, int d) {
                sb.append(String.format("SET_ACTIVE:\t%02x %02x %02x %02x\n",a,b,c,d));
                text.setText(sb.toString());

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
    }

    public void sendDataPressed(View view) {
        Intent intent = new Intent(SEND_DATA_INTENT);
        intent.putExtra(DATA_EXTRA, DATA);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serial_test, menu);
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
}
