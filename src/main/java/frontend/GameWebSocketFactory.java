package frontend;

import base.AccountService.AccountService;
import base.GameMechanics;
import base.WebSocketService;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import services.AccountService.UserDataSet;

import java.sql.SQLException;

/**
 * @author v.chibrikov
 */
public class GameWebSocketFactory implements WebSocketCreator {
    private AccountService accountService;
    private GameMechanics gameMechanics;
    private WebSocketService webSocketService;

    public GameWebSocketFactory(AccountService accountService,
                                GameMechanics gameMechanics,
                                WebSocketService webSocketService) {
        this.accountService = accountService;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        String sessionId = req.getHttpServletRequest().getSession().getId();
        UserDataSet user = accountService.getUserBySession(sessionId);
        return new GameWebSocket(user, gameMechanics, webSocketService);
    }
}
