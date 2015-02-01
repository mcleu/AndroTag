package evans.dave.duinotag;

/**
 * Created by Dave on 29/01/2015.
 */
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class GunOptionAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final Gun[] values;

    public GunOptionAdapter(Context context, Gun[] values) {
        super(context, R.layout.rowlayout, Gun.getAllNames(values));
        this.context = context;
        this.values = values;
    }

    @Override
    public long getItemId(int position){
        return values[position].id;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.loadout_select_item, parent, false);

        //rowView.setMinimumHeight(200);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView descView = (TextView) rowView.findViewById(R.id.description);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        ProgressBar statD = (ProgressBar) rowView.findViewById(R.id.statD);
        ProgressBar statF = (ProgressBar) rowView.findViewById(R.id.statF);
        ProgressBar statA = (ProgressBar) rowView.findViewById(R.id.statA);
        ProgressBar statR = (ProgressBar) rowView.findViewById(R.id.statR);

        imageView.setImageResource(values[position].icon);
        textView.setText(values[position].name);
        descView.setText(values[position].desc);

        Gun gun = values[position];


        // Damage
        statD.setMax(100);
        double dd = gun.damage; // Linear scaling
        statD.setProgress((int) dd);

        // Ammo
        statA.setMax(100); // goes for A=0 to 150 logarithmically
        statA.setProgress((int) (20*Math.log(gun.MAX_AMMO)));

        // Reload
        statR.setMax(100); // goes for R=0 to 5 linearly
        statR.setProgress(gun.reloadTime/50); // Originally in ms

        // Firing Rate
        statF.setMax(100); // goes for F=0 to 50 shots per second logarithmically
        statF.setProgress(  (int)  (25*Math.log(1000/((double)gun.fireTime))) );



        return rowView;
    }
}
