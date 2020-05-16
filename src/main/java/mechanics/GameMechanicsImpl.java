package mechanics;

import base.*;
import base.AccountService.AccountService;
import services.AccountService.UserDataSet;
import utils.TimeHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author v.chibrikov
 */
public class GameMechanicsImpl implements GameMechanics {
    private static final int TICK = 100;

    private WebSocketService webSocketService;
    private MatchmakingService matchmakingService;
    private AccountService accountService;

    private Map<String, GameSession> nameToSession = new HashMap<>();

    private Set<GameSession> allGameSessions = new HashSet<>();

    public Map<String, UserDataSet> getUsers() {
        return users;
    }

    private Map<String, UserDataSet> users = new HashMap<>();

    public GameMechanicsImpl(WebSocketService webSocketService, MatchmakingService matchmakingService, AccountService accountService) {
        this.webSocketService = webSocketService;
        this.matchmakingService = matchmakingService;
        this.accountService = accountService;
        this.matchmakingService.setGameMechanics(this); // hate this place
        Thread mmsThread = new Thread(matchmakingService);
        mmsThread.start();
    }

    public void addUser(UserDataSet user) {
        users.put(user.getName(), user);
    }

    public void setUserState(UserDataSet user, UserState state) {
        user.setState(state);
    }

    public void setChoice(UserDataSet user, String choice) {
        String name = user.getName(); // actually it's not a name, here should be login
        GameSession gameSession = nameToSession.get(name);
        gameSession.setChoice(user, choice);
    }

    @Override
    public void run() {
        while (true) {
            TimeHelper.sleep(TICK);
            // do smth?
        }
    }

    public void starGame(UserDataSet first, UserDataSet second) {
        GameSession gameSession = new GameSession(webSocketService, accountService, first, second);
        String firstName = first.getName();
        String secondName = second.getName();
        allGameSessions.add(gameSession);
        nameToSession.put(firstName, gameSession);
        nameToSession.put(secondName, gameSession);

        webSocketService.notifyStartGame(gameSession.getSelf(firstName), gameSession.getEnemyTo(firstName).getName());
        webSocketService.notifyStartGame(gameSession.getSelf(secondName), gameSession.getEnemyTo(secondName).getName());
        gameSession.start();
    }
}
