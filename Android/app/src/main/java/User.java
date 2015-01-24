/**
 * Created by Dave on 19/01/2015.
 */
public class User {

    public String name;// User name
    public int id;      // Name identifier

    public static User NO_USER = new User("NO_USER", Short.MAX_VALUE);

    public User(String username,int id){
        this.name = username;
        this.id = id;
    }

    public void setID(String idStr){
        id = (int) Short.parseShort(idStr);
    }
    public String getNameFull(){
        return name+Integer.toHexString(id);
    }
}
