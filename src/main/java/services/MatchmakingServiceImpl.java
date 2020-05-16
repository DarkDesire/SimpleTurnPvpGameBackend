package services;

import base.*;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.AccountService.UserDataSet;

/**
 * Created by darkDesire on 26.09.2015.
 */
public class MatchmakingServiceImpl implements MatchmakingService {
    static final Logger logger = LogManager.getLogger(MatchmakingServiceImpl.class.getName());
    private static final int TICK = 1000; //1s
    private WebSocketService webSocketService;
    private GameMechanics gameMechanics;

    private UserDataSet first;
    private UserDataSet second;
    private boolean isFirstExists = false;

    public MatchmakingServiceImpl(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void setGameMechanics(GameMechanics gameMechanics) {
        this.gameMechanics = gameMechanics;
    }

    public void run() {
        Map<String, UserDataSet> users = gameMechanics.getUsers();

        try {
            while (true) {
                Thread.sleep(TICK);
                //do smth
                if (users.size() > 0) { // check size
                    for (UserDataSet gameUser : users.values()) {
                        if (gameUser.getState() == UserState.FINDING_MATCH) {
                            if (!isFirstExists) {
                                first = gameUser;
                                isFirstExists = true;
                            } else if (!gameUser.equals(first)) {
                                second = gameUser;
                                first.setState(UserState.MATCH_FOUND);
                                second.setState(UserState.MATCH_FOUND);
                                webSocketService.notifyUserNewState(first, first.getState());
                                webSocketService.notifyUserNewState(second, second.getState());
                                isFirstExists = false;
                                gameMechanics.starGame(first, second);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}