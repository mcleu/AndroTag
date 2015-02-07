package evans.dave.androtag.app;

/**
 * Created by Dave on 29/01/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import evans.dave.androtag.R;
import evans.dave.androtag.common.GeneralPlayer;
import evans.dave.androtag.common.Scoring;
import evans.dave.androtag.common.ScoringFactory;
import evans.dave.androtag.common.Team;

public class ScoreboardLineAdapter extends ArrayAdapter<String> {
    private final Context ctx;
    private final AndrotagApplication app;
    private ArrayList<Scoring> items;

    public ScoreboardLineAdapter(Context ctx, ArrayList<Scoring> items) {
        super(ctx, R.layout.rowlayout, ScoringFactory.getNames(items)); // I don't want to have to call this...
        this.ctx = ctx;
        this.items = items;
        app = (AndrotagApplication) ((Activity) ctx).getApplication();
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    public void setItems(ArrayList<Scoring> items) { this.items = items;}


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater
        		.inflate(R.layout.scoreboard_item, parent, false);

        // Get the various components
        Scoring item = items.get(position);
        
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

        // and overlay the background color
        if (item == app.player)
            bg.setBackgroundResource(R.drawable.dt_scoreboard_background_player);
        bg.setBackgroundResource(R.drawable.dt_scoreboard_background_alive);

        if(!item.isDead())
            name.setTextColor(Color.BLACK);
        else
            name.setTextColor(Color.GRAY);

        bg.getBackground().setColorFilter(item.getColor(),PorterDuff.Mode.SCREEN);
        
        // Add separators between teams and players
        if (position < items.size()-1){
            Scoring item2 = items.get(position+1);
            if ( ( item instanceof Team && item2 instanceof GeneralPlayer) || //Team padding
                    item instanceof GeneralPlayer && item2 instanceof GeneralPlayer && item.getColor() != item2.getColor()) { // Team separator
                rowView.setPadding(0, 0, 0, 20);
            }
        }
        
        return rowView;
    }
}
