package mechanics;

import base.*;
import base.AccountService.AccountService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import services.AccountService.GameDataSet;
import services.AccountService.UserDataSet;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameSession extends Thread {
    static final Logger logger = LogManager.getLogger(GameSession.class.getName());
    private static final int DELAY = 5000; // players have 5 seconds to change their status to accept

    private WebSocketService webSocketService;
    private AccountService accountService;

    private SessionState state = SessionState.IS_ON;
    private final long startTime;
    private long endTime;
    private final UserDataSet first;
    private final UserDataSet second;
    private int firstWIN;
    private int DRAWS;
    private int secondWIN;
    private GameDataSet gameDataSet;

    private Map<String, UserDataSet> users = new HashMap<>();
    private Set<GameRound> rounds = new HashSet<>();
    private LinkedList<UserChoice> firstChoices = new LinkedList<>();
    private LinkedList<UserChoice> secondChoices = new LinkedList<>();

    public GameSession(WebSocketService webSocketService, AccountService accountService, UserDataSet user1, UserDataSet user2) {
        this.webSocketService = webSocketService;
        this.accountService = accountService;
        this.startTime = new Date().getTime();
        this.first = user1;
        this.second = user2;
        this.gameDataSet = new GameDataSet();
        gameDataSet.setUser1(first.getId());
        gameDataSet.setUser2(second.getId());
        gameDataSet.setStartTime(startTime);
        users.put(first.getName(), first);
        users.put(second.getName(), second);
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public UserDataSet getEnemyTo(String user) {
        if (user.equals(first.getName())) {
            return second;
        } else return first;
    }

    public void run() {
        try {
            if (accountService.addGameSession(gameDataSet))
            {
                Thread.sleep(DELAY);

                if (first.getState() == UserState.ACCEPT_MATCH && second.getState() == UserState.ACCEPT_MATCH) {
                    first.setState(UserState.PLAYING_MATCH);
                    second.setState(UserState.PLAYING_MATCH);
                    firstChoices.clear(); //wtf1
                    secondChoices.clear(); // wtf2
                    long date = new Date().getTime(); // wtf3
                    firstChoices.add(new UserChoice(date,"ROCK")); // wtf4
                    secondChoices.add(new UserChoice(date,"ROCK")); // wtf5
                    webSocketService.notifyUserNewState(first, first.getState());
                    webSocketService.notifyUserNewState(second, second.getState());
                    logger.info("GameSession is started. " + "Player one:" + first.getName() + " player two:" + second.getName());
                    ExecutorService executor = Executors.newFixedThreadPool(1);
                    for (int i = 0; i < GameRound.MAX_ROUND; i++) {
                        GameRound gameRound = new GameRound(webSocketService, this, i, first, second);
                        rounds.add(gameRound);
                        executor.execute(gameRound);
                    }
                    executor.shutdown();
                    // Wait until all threads are finish
                    while (!executor.isTerminated()) {
                    }

                    getGameResult();
                    updateUsersResults();
                    webSocketService.notifyGameOver(first, getFirstResult());
                    webSocketService.notifyGameOver(second, getSecondResult());
                    Thread.sleep(DELAY);
                }
            }

            first.setState(UserState.IDLE);
            second.setState(UserState.IDLE);
            webSocketService.notifyUserNewState(first, first.getState());
            webSocketService.notifyUserNewState(second, second.getState());

            setState(SessionState.FINISHED);
            endTime = new Date().getTime();
            gameDataSet.setEndTime(endTime);
            updateGameRecord();
            logger.info("GameSession is finished");

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }


    public UserDataSet getSelf(String user) {
        return users.get(user);
    }

    private void getGameResult() {
        firstWIN = 0;
        DRAWS = 0;
        secondWIN = 0;
        for (GameRound round : rounds) {
            if (round.getFirstResult().equals("WIN")) firstWIN++;
            else if (round.getSecondResult().equals("WIN")) secondWIN++;
            else DRAWS++;
        }
    }

    private void updateUsersResults(){
        first.addScore(firstWIN*GameState.WIN.getValue()+DRAWS*GameState.DRAW.getValue());
        first.addGames(rounds.size());
        second.addScore(secondWIN*GameState.WIN.getValue()+DRAWS*GameState.DRAW.getValue());
        second.addGames(rounds.size());
        try {
            accountService.updateUser(first);
            accountService.updateUser(second);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFirstResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(secondWIN).append("-").append(DRAWS).append("-").append(firstWIN);
        return sb.toString();
    }

    public String getSecondResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstWIN).append("-").append(DRAWS).append("-").append(secondWIN);
        return sb.toString();
    }

    public void setChoice(UserDataSet user, String choice) {
        long date = new Date().getTime();
        if (user.equals(first)) {
            firstChoices.add(new UserChoice(date, choice));
        } else secondChoices.add(new UserChoice(date, choice));
    }

    public UserChoice getLastChoice(UserDataSet user){
        if (user.equals(first)){
            return firstChoices.getLast();
        } else return secondChoices.getLast();
    }

    public void updateGameRecord(){

        JSONObject objRounds = new JSONObject();
        JSONArray listRounds = new JSONArray();
        for(GameRound round : rounds){
            JSONObject objRound = new JSONObject();
            objRound.put("startTime", round.getStartTime());
            objRound.put("endTime", round.getEndTime());
            objRound.put("first", round.getFirstResult());
            objRound.put("second", round.getSecondResult());
            listRounds.add(objRound);
        }
        objRounds.put("rounds",listRounds);
        gameDataSet.setRounds(objRounds.toJSONString());


        JSONObject objChoices = new JSONObject();
        // first user choices, creating JSON
        JSONArray listFirstChoices = new JSONArray();
        for(UserChoice userChoice: firstChoices){
            JSONObject firstChoice = new JSONObject();
            firstChoice.put("time", userChoice.getTime());
            firstChoice.put("value", userChoice.getValue());
            listFirstChoices.add(firstChoice);
        }
        objChoices.put(String.valueOf(first.getId()),listFirstChoices);

        // second user choices, creating JSON
        JSONArray listSecondChoices = new JSONArray();
        for(UserChoice userChoice: secondChoices){
            JSONObject secondChoice = new JSONObject();
            secondChoice.put("time", userChoice.getTime());
            secondChoice.put("value", userChoice.getValue());
            listSecondChoices.add(secondChoice);
        }
        objChoices.put(String.valueOf(second.getId()),listSecondChoices);
        gameDataSet.setChoices(objChoices.toJSONString());
        gameDataSet.setResult(getFirstResult());

        try {
            accountService.updateGameSession(gameDataSet);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
