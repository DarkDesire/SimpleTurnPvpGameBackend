package frontend.servlets;

import base.AccountService.AccountService;
import base.GameMechanics;
import base.WebSocketService;
import frontend.GameWebSocketFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

/**
 * This class represents a servlet starting a webSocket application
 */
@WebServlet(name = "WebSocketGameServlet")
public class WebSocketGameServlet extends WebSocketServlet {
    public static final String webSocketGameServletURL = "/gameplay";
    static final Logger logger = LogManager.getLogger(WebSocketGameServlet.class.getName());
    private final static int IDLE_TIME = 60 * 60 * 1000;

    private AccountService accountService;
    private GameMechanics gameMechanics;
    private WebSocketService webSocketService;

    public WebSocketGameServlet(AccountService accountService,
                                GameMechanics gameMechanics,
                                WebSocketService webSocketService) {
        this.accountService = accountService;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(IDLE_TIME);
        factory.setCreator(new GameWebSocketFactory(accountService, gameMechanics, webSocketService));
    }
}
