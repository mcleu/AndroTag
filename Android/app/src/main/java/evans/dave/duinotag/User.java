package evans.dave.duinotag;

/**
 * Created by Dave on 19/01/2015.
 */
public class User {

    public String name;// evans.dave.duinotag.User name
    public int id;      // Name identifier

    public static User NO_USER = new User("NO_USER", Short.MAX_VALUE);

    public User(String username,int id){
        this.name = username;
        this.id = id;
    }
    public User(String username, String idStr){
        this.name = username;
        this.id = Integer.decode("0x"+idStr);
    }

    public void setID(String idStr){
        id = (int) Short.parseShort(idStr);
    }
    public String getNameFull(){
        return name+Integer.toHexString(id);
    }
}
