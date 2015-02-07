package evans.dave.androtag.common;

import java.util.List;

/**
 * Created by Dave on 05/02/2015.
 */
public class ScoringFactory {
    public static String[] getNames(List<Scoring> s){
        String[] str = new String[s.size()];
        for (int i = 0; i<s.size(); i++){
            str[i] = s.get(i).getName();
        }
        return str;
    }
}
