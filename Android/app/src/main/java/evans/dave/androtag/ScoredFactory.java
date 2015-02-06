package evans.dave.androtag;

/**
 * Created by Dave on 05/02/2015.
 */
public class ScoredFactory {
    public static String[] getNames(Scored[] s){
        String[] str = new String[s.length];
        for (int i = 0; i<s.length; i++){
            str[i] = s[i].getName();
        }
        return str;
    }
}
