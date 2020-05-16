package base;

/**
 * Created by darkDesire on 02.02.2016.
 */
public enum GameState {
    LOSE (0),
    DRAW (1),
    WIN  (3);

    private final int value;

    private GameState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
