package base.AccountService;

import base.LongId;
import services.AccountService.GameDataSet;
import services.AccountService.UserDataSet;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AccountService {
    boolean addUser(String login, String email, String hash, String salt);

    UserDataSet getUser(String login) throws SQLException;

    ArrayList<UserDataSet> getAllUsers() throws SQLException;

    ArrayList<UserDataSet> getFiveScoreUsers() throws SQLException;

    UserDataSet getUserBySession(String sessionID);

    String getUserNameBySession(String sessionID) throws SQLException;

    void addSession(String sessionID, LongId<UserDataSet> userId);

    void deleteSession(String sessionID);

    Integer getCountUsers();

    Integer getCountLoggedInUsers();

    boolean isAuthorised(String sessionID);

    boolean deleteUser(String username);

    void updateUser(UserDataSet user) throws SQLException;

    // game session stuff

    boolean addGameSession(GameDataSet game) throws SQLException;

    void updateGameSession(GameDataSet game) throws SQLException;

    ArrayList<GameDataSet> getAllGameSessionsWithUser(LongId<UserDataSet> userId);

    GameDataSet getGameSession(LongId<GameDataSet> gameId);


}