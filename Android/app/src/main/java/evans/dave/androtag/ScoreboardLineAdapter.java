package evans.dave.androtag;

/**
 * Created by Dave on 29/01/2015.
 */
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScoreboardLineAdapter extends ArrayAdapter<String> {
    private final Context ctx;
    private final Scored[] items;

    public ScoreboardLineAdapter(Context ctx, Scored[] items) {
        super(ctx, R.layout.rowlayout, ScoredFactory.getNames(items)); // I don't want to have to call this...
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater
        		.inflate(R.layout.scoreboard_item, parent, false);

        //rowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
        
        // Get the various components
        Scored item = items[position];
        
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView kills = (TextView) rowView.findViewById(R.id.kills);
        TextView deaths = (TextView) rowView.findViewById(R.id.deaths);
        TextView assists = (TextView) rowView.findViewById(R.id.assists);
        TextView score = (TextView) rowView.findViewById(R.id.score);
        LinearLayout bg = (LinearLayout) rowView.findViewById(R.id.background);
        
        // Set the text
        name.setText(item.getName());
        kills.setText(""+item.getKills());
        deaths.setText(""+item.getDeaths());
        assists.setText(""+item.getAssists());
        score.setText(""+item.getScore());
        // and overlay color
        /**name.getBackground().setColorFilter(item.getColor(), PorterDuff.Mode.SCREEN);
        kills.getBackground().setColorFilter(item.getColor(),PorterDuff.Mode.SCREEN);
        deaths.getBackground().setColorFilter(item.getColor(),PorterDuff.Mode.SCREEN);
        assists.getBackground().setColorFilter(item.getColor(),PorterDuff.Mode.SCREEN);
        score.getBackground().setColorFilter(item.getColor(),PorterDuff.Mode.SCREEN);**/
        bg.getBackground().setColorFilter(item.getColor(),PorterDuff.Mode.SCREEN);
        
        // Add separators between teams and players
        if (position < items.length-1){
            Scored item2 = items[position+1];
            if ( ( item instanceof Team && item2 instanceof GeneralPlayer ) || //Team padding
                    item instanceof GeneralPlayer && item2 instanceof GeneralPlayer && item.getColor() != item2.getColor()) { // Team separator
                rowView.setPadding(0, 0, 0, 20);
            }
        }
        
        return rowView;
    }
}
