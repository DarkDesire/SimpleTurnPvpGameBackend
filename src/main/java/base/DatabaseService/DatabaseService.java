package base.DatabaseService;

import base.LongId;
import org.hibernate.exception.ConstraintViolationException;
import services.AccountService.GameDataSet;
import services.AccountService.UserDataSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by darkDesire on 01.10.2015.
 */
public interface DatabaseService {

    // UserDataSet
    void addUser(UserDataSet user) throws SQLException, ConstraintViolationException;
    UserDataSet getUser(String login);
    UserDataSet getUser(LongId<UserDataSet> userId);
    void updateUser(UserDataSet user) throws SQLException, ConstraintViolationException;
    List<UserDataSet> getAllUsers() throws SQLException;
    List<UserDataSet> getFiveScoreUsers() throws SQLException;
    void deleteUser(UserDataSet user) throws SQLException;
    int countUsers();
    // GameDataSet
    void addGameSession(GameDataSet game) throws SQLException, ConstraintViolationException;
    void updateGameSession(GameDataSet game) throws SQLException, ConstraintViolationException;
    List<GameDataSet> getAllGameSessionsWithUser(LongId<UserDataSet> userId);
    GameDataSet getGameSession(LongId<GameDataSet> gameId);

}
