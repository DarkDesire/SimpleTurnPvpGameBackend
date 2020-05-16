package services;

import services.AccountService.UserDataSet;
import base.UserState;
import base.WebSocketService;
import frontend.GameWebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v.chibrikov
 */
public class WebSocketServiceImpl implements WebSocketService {
    private Map<String, GameWebSocket> userSockets = new HashMap<>();

    public void addUser(GameWebSocket userWebSocket) {
        userSockets.put(userWebSocket.getUser().getName(), userWebSocket);
    }

    public void notifyStartGame(UserDataSet user, String enemyName) {
        userSockets.get(user.getName()).startGame(enemyName);
    }

    public void notifyUserNewState(UserDataSet user, UserState newState){
        userSockets.get(user.getName()).userNewState(newState.name());
    }

    public void notifyNewRound(UserDataSet user, Integer roundId){
        userSockets.get(user.getName()).newRound(roundId.toString());
    }

    public void notifyRoundTick(UserDataSet user, Integer seconds) {
        userSockets.get(user.getName()).roundTick(seconds.toString());
    }

    public void notifyRoundOver(UserDataSet user, String win, String enemyChoice) {
        userSockets.get(user.getName()).roundOver( win, enemyChoice);
    }

    public void notifyGameOver(UserDataSet user, String result) {
        userSockets.get(user.getName()).gameOver(result);
    }

}
