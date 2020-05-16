package base;

/**
 * Created by darkDesire on 26.09.2015.
 */
public enum UserState {
    IDLE("IDLE"), FINDING_MATCH("FINDING_MATCH"), MATCH_FOUND("MATCH_FOUND"), ACCEPT_MATCH("ACCEPT_MATCH"), PLAYING_MATCH("PLAYING_MATCH");

    private String state;

    UserState(String state) {
        this.state = state;
    }

    public String toString() {
        return state;
    }

    public static UserState fromString(String state) {
        if (state != null) {
            for (UserState b : UserState.values()) {
                if (state.equalsIgnoreCase(b.state)) {
                    return b;
                }
            }
        }
        return null;
    }
}