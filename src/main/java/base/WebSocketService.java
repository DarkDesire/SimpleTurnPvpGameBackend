package base;

import frontend.GameWebSocket;
import services.AccountService.UserDataSet;

/**
 * @author v.chibrikov
 */
public interface WebSocketService {

    void addUser(GameWebSocket userWebSocket);

    void notifyStartGame(UserDataSet user, String enemyName);

    void notifyUserNewState(UserDataSet user, UserState newState);

    void notifyNewRound(UserDataSet user, Integer roundId);

    void notifyRoundTick(UserDataSet user, Integer seconds);

    void notifyRoundOver(UserDataSet user, String myResult, String enemyChoice);

    void notifyGameOver(UserDataSet user, String result);
}
