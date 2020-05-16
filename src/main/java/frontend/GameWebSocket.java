package frontend;

import base.AccountService.AccountService;
import base.GameMechanics;
import base.UserState;
import base.WebSocketService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.AccountService.UserDataSet;


@WebSocket
public class GameWebSocket {
    static final Logger logger = LogManager.getLogger(GameWebSocket.class.getName());

    private UserDataSet user;
    private Session session;
    private GameMechanics gameMechanics;
    private WebSocketService webSocketService;

    public GameWebSocket(UserDataSet user, GameMechanics gameMechanics, WebSocketService webSocketService) {
        this.user = user;
        this.gameMechanics = gameMechanics;
        this.webSocketService = webSocketService;
        logger.info("GameWebSocket for name: " + user.getName() + " is created");
    }

    public UserDataSet getUser(){
        return user;
    }

    public void updateInfo() {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("myName", user.getName());
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void startGame(String enemyName) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("status", "start");
            jsonStart.put("enemyName", enemyName);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void userNewState(String state) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("userNewState", state);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void newRound(String roundId) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("newRound", roundId);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void roundTick(String seconds) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("seconds", seconds);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void roundOver(String result, String enemyChoice) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("status", "roundOver");
            jsonStart.put("result", result);
            jsonStart.put("enemyChoice", enemyChoice);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void gameOver(String result) {
        try {
            JSONObject jsonStart = new JSONObject();
            jsonStart.put("status", "gameOver");
            jsonStart.put("result", result);
            session.getRemote().sendString(jsonStart.toJSONString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        logger.info("Recieved msg: " + data);
        if (data.equals("UPDATE INFO")) {
            updateInfo();
        } else if (data.equals("FINDING_MATCH") || data.equals("ACCEPT_MATCH") || data.equals("IDLE")) {
            gameMechanics.setUserState(user, UserState.fromString(data));
        } else {
            gameMechanics.setChoice(user, data);
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        setSession(session);
        webSocketService.addUser(this);
        gameMechanics.addUser(user);
        updateInfo();
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {

    }
}
