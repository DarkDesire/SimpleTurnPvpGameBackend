package mechanics;

import base.UserChoice;
import services.AccountService.UserDataSet;
import base.WebSocketService;
import base.GameState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by darkDesire on 28.09.2015.
 */
public class GameRound implements Runnable {
    static final Logger logger = LogManager.getLogger(GameRound.class.getName());

    public static final int MAX_ROUND = 3; // players have 5 rounds
    private static final int MAX_ROUND_SECONDS = 10;
    private WebSocketService webSocketService;
    private GameSession gameSession;

    private final int roundId;
    private UserDataSet first;
    private UserDataSet second;
    private long startTime;
    private long endTime;

    public String getFirstResult() {
        return firstResult;
    }

    public String getSecondResult() {
        return secondResult;
    }

    public long getStartTime() { return startTime;}

    public long getEndTime() { return endTime;}

    public int getRoundId() { return roundId;}

    private String firstResult = "";
    private String secondResult = "";

    GameRound(WebSocketService webSocketService, GameSession gameSession, int round, UserDataSet first, UserDataSet second) {
        this.webSocketService = webSocketService;
        this.gameSession = gameSession;
        this.roundId = round;
        this.first = first;
        this.second = second;
    }

    @Override
    public void run() {
        startTime = new Date().getTime();
        logger.info("Round: " + this.roundId + " is started at: "+startTime);
        webSocketService.notifyNewRound(first, roundId);
        webSocketService.notifyNewRound(second, roundId);
        try {
            for (int seconds = 0; seconds < MAX_ROUND_SECONDS; seconds++) {
                webSocketService.notifyRoundTick(first, seconds);
                webSocketService.notifyRoundTick(second, seconds);
                Thread.sleep(1000);
            }
            getRoundResults();
            webSocketService.notifyRoundOver(first, firstResult, gameSession.getLastChoice(second).getValue());
            webSocketService.notifyRoundOver(second, secondResult, gameSession.getLastChoice(first).getValue());
            Thread.sleep(3000);

            endTime = new Date().getTime();
            logger.info("Round: " + this.roundId + " is finished at: "+endTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getRoundResults() {
        String secondChoiceCache = gameSession.getLastChoice(second).getValue();
        switch (gameSession.getLastChoice(first).getValue()) {
            case "ROCK":
                switch (secondChoiceCache) {
                    case "ROCK"://player one ROCK AND player two ROCK
                        firstResult = "DRAW";
                        break;
                    case "PAPER"://player one ROCK AND player two PAPER
                        firstResult = "LOSE";
                        break;
                    case "SCISSORS"://player one ROCK AND player two SCISSORS
                        firstResult = "WIN";
                        break;
                }
                break;
            case "PAPER":
                switch (secondChoiceCache) {
                    case "ROCK"://player one PAPER AND player two ROCK
                        firstResult = "WIN";
                        break;
                    case "PAPER"://player one PAPER AND player two PAPER
                        firstResult = "DRAW";
                        break;
                    case "SCISSORS"://player one PAPER AND player two SCISSORS
                        firstResult = "LOSE";
                        break;
                }
                break;
            case "SCISSORS":
                switch (secondChoiceCache) {
                    case "ROCK"://player one SCISSORS AND player two ROCK
                        firstResult = "LOSE";
                        break;
                    case "PAPER"://player one SCISSORS AND player two PAPER
                        firstResult = "WIN";
                        break;
                    case "SCISSORS"://player one SCISSORS AND player two SCISSORS
                        firstResult = "DRAW";
                        break;
                }
                break;
        }
        if (firstResult.equals("DRAW")) {
            secondResult = firstResult;
        } else if (firstResult.equals("WIN")) {
            secondResult = "LOSE";
        } else if (firstResult.equals("LOSE")) {
            secondResult = "WIN";
        }
    }
}