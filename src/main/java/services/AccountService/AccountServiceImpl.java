package services.AccountService;

import base.AccountService.AccountService;
import base.DatabaseService.DatabaseService;
import base.LongId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by darkDesire on 23.09.2015.
 */
public class AccountServiceImpl implements AccountService {
    static final Logger logger = LogManager.getLogger(AccountServiceImpl.class.getName());
    private DatabaseService databaseService;
    private final ConcurrentHashMap<String, LongId<UserDataSet>> activeSessions;

    public AccountServiceImpl(DatabaseService databaseService) {
        logger.info("AccountServiceImpl is created");
        this.databaseService = databaseService;
        activeSessions = new ConcurrentHashMap<>();
    }

    public synchronized boolean addUser(String login, String email, String hash, String salt) {
        UserDataSet user = new UserDataSet(login, email, hash, salt);
        try {
            databaseService.addUser(user);
            return true;
        } catch (SQLException e) {
            return false;
        } catch (ConstraintViolationException e) {
            return false;
        }
    }

    // can return null
    public UserDataSet getUser(String login) throws SQLException {
        return databaseService.getUser(login);
    }

    public synchronized void addSession(String sessionID, LongId<UserDataSet> userID) {
        activeSessions.put(sessionID, userID);
    }

    public synchronized void deleteSession(String sessionID) {
        activeSessions.remove(sessionID);
    }

    // can return null
    public synchronized UserDataSet getUserBySession(String sessionID) {
        LongId<UserDataSet> userID = activeSessions.get(sessionID);
        if (userID == null)
            return null;
        return databaseService.getUser(userID);
    }

    public synchronized String getUserNameBySession(String sessionID) throws SQLException {
        UserDataSet user = getUserBySession(sessionID);
        return user == null ? null : user.getName();
    }

    public void updateUser(UserDataSet user) throws SQLException {
        databaseService.updateUser(user);
    }


    public Integer getCountUsers() {
        return databaseService.countUsers();
    }


    public Integer getCountLoggedInUsers() {
        return activeSessions.size();
    }

    public boolean isAuthorised(String sessionID) {
        return activeSessions.containsKey(sessionID);
    }


    public synchronized boolean deleteUser(String username) {
    /*    try {
            UserDataSet user = getUserByName(username);
            if (user == null) {
                return false;
            }
            databaseService.deleteUser(user);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException();
        }*/
        return false;
    }

    public synchronized ArrayList<UserDataSet> getAllUsers() throws SQLException {
        ArrayList<UserDataSet> users = new ArrayList<UserDataSet>(databaseService.getAllUsers());
        users.sort(new UsersScoreComparator()); // sory by score
        return users;
    }

    public synchronized ArrayList<UserDataSet> getFiveScoreUsers() throws SQLException {
        return new ArrayList<UserDataSet>(databaseService.getFiveScoreUsers());
    }

    class UsersScoreComparator implements Comparator<UserDataSet> {

        public int compare(UserDataSet obj1, UserDataSet obj2) {
            if (obj1.getScore() < obj2.getScore())
                return -1;
            else if (obj1.getScore() > obj2.getScore())
                return 1;
            else return 0;
        }
    }

    public synchronized boolean addGameSession(GameDataSet game) throws SQLException {
        try {
            databaseService.addGameSession(game);
            return true;
        } catch (SQLException e) {
            return false;
        } catch (ConstraintViolationException e) {
            return false;
        }
    }

    public void updateGameSession(GameDataSet game) throws SQLException {
        databaseService.updateGameSession(game);
    }

    public synchronized ArrayList<GameDataSet> getAllGameSessionsWithUser(LongId<UserDataSet> userId) {
        return new ArrayList<GameDataSet>(databaseService.getAllGameSessionsWithUser(userId));
    }

    // can return null
    public synchronized GameDataSet getGameSession(LongId<GameDataSet> gameId) {
        return databaseService.getGameSession(gameId);
    }

}
