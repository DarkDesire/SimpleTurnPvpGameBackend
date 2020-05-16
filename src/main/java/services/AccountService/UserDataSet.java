package services.AccountService;

import base.LongId;
import base.UserState;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserDataSet implements Serializable {
    private static final long serialVersionUID = 05022016L;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "hash")
    private String hash;

    @Column(name = "salt")
    private String salt;

    @Column(name = "registration_time")
    private long regTime;

    @Column(name = "score")
    private int score;

    @Column(name = "ban_start_time")
    private int banStartTime;

    @Column(name = "ban_end_time")
    private int banEndTime;

    @Column(name = "avatar_path")
    private String avPath;

    @Column(name = "games")
    private int games;

    @Column(name = "admin")
    private boolean admin;

    private UserState state;

    public UserDataSet() {
    }

    public UserDataSet(String login, String email, String hash, String salt) {
        this.login = login;
        this.name = login;
        this.email = email;
        this.hash = hash;
        this.salt = salt;
        regTime = new Date().getTime();
        score = 0;
        banStartTime = 0;
        banEndTime = 0;
        avPath = "";
        games = 0;
        admin = true;
        state = UserState.IDLE;
    }

    public LongId<UserDataSet> getId() {
        return new LongId<UserDataSet>(id);
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }

    public long getRegTime() {
        return regTime;
    }

    public boolean isAdmin() {
        return admin;
    }

    public int getBanStartTime() {
        return banStartTime;
    }

    public String getAvPath() {
        return avPath;
    }

    public int getBanEndTime() {
        return banEndTime;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int value) { score += value; }

    public int getGames() {
        return games;
    }

    public void addGames(int value) { games += value; }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}