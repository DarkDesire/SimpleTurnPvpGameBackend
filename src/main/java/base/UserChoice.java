package base;

/**
 * Created by darkDesire on 05.02.2016.
 */
public class UserChoice {

    private long time;
    private String value; // TODO: переделать на GameState

    public UserChoice(long time, String value){
        this.time = time;
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public long getTime(){
        return time;
    }

}
