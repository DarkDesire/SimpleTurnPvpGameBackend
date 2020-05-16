package services.DatabaseService;

import base.DatabaseService.DatabaseService;
import base.LongId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import services.AccountService.GameDataSet;
import services.AccountService.UserDataSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by darkDesire on 01.10.2015.
 */
public class DatabaseServiceImpl implements DatabaseService {
    private SessionFactory sessionFactory;

    public DatabaseServiceImpl() {
        this.sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public synchronized void addUser(UserDataSet user) throws SQLException, ConstraintViolationException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    // can return null
    public synchronized UserDataSet getUser(String login){
        Session session = null;
        UserDataSet result = null;
        try {
            session = sessionFactory.openSession();
            result = (UserDataSet) session.createCriteria(UserDataSet.class)
                    .add(Restrictions.eq("login", login))
                    .uniqueResult();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }

    public synchronized UserDataSet getUser(LongId<UserDataSet> userId){
        Session session = null;
        UserDataSet result = null;
        try {
            session = sessionFactory.openSession();
            result = session.get(UserDataSet.class, userId.get());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }

    public synchronized void updateUser(UserDataSet user) throws SQLException, ConstraintViolationException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }


    public synchronized List<UserDataSet> getAllUsers() throws SQLException {
        Session session = null;
        List<UserDataSet> result = null;
        try {
            session = sessionFactory.openSession();
            result = session.createCriteria(UserDataSet.class).list();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    } //

    public synchronized List<UserDataSet> getFiveScoreUsers() throws SQLException {
        Session session = null;
        List<UserDataSet> result = null;
        try {
            session = sessionFactory.openSession();
            result = session.createCriteria(UserDataSet.class)
                    .addOrder( Property.forName("score").desc())
                    .setMaxResults(5)
                    .list();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }

    public synchronized void deleteUser(UserDataSet user) throws SQLException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    public synchronized void addGameSession(GameDataSet game) throws SQLException, ConstraintViolationException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.save(game);
            transaction.commit();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    public synchronized void updateGameSession(GameDataSet game) throws SQLException, ConstraintViolationException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.update(game);
            transaction.commit();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }
    public synchronized List<GameDataSet> getAllGameSessionsWithUser(LongId<UserDataSet> userId)  {
        Session session = null;
        List<GameDataSet> result = null;
        try {
            session = sessionFactory.openSession();
            Disjunction or = Restrictions.disjunction();
            or.add(Restrictions.eq("user1", userId));
            or.add(Restrictions.eq("user2", userId));
            result = session.createCriteria(GameDataSet.class)
                        .add(or)
                        .list();
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }

    public synchronized GameDataSet getGameSession(LongId<GameDataSet> gameId){
        Session session = null;
        GameDataSet result = null;
        try {
            session = sessionFactory.openSession();
            result = session.get(GameDataSet.class, gameId.get());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
        return result;
    }


    public int countUsers() {
        try {
            return getAllUsers().size();
        } catch (SQLException e) {
            return 0;
        }
    }
}
