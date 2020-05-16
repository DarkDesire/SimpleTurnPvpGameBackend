package services.AccountService;

import base.LongId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "games")
public class GameDataSet implements Serializable {
    private static final long serialVersionUID = 05022016L;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    @Column(name = "user1")
    private long user1;

    @Column(name = "user2")
    private long user2;

    @Column(name = "result")
    private String result;

    @Column(name = "rounds", columnDefinition="TEXT")
    private String rounds;

    @Column(name = "choices", columnDefinition="TEXT")
    private String choices;

    @Column(name = "start_time")
    private long startTime;

    @Column(name = "end_time")
    private long endTime;

    public LongId<GameDataSet> getId() {
        return new LongId<GameDataSet>(id);
    }

    public void setUser1(LongId<UserDataSet> userId) {
        this.user1 = userId.get();
    }

    public void setUser2(LongId<UserDataSet> userId) {
        this.user2 = userId.get();
    }

    public void setRounds(String rounds) {
        this.rounds = rounds;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}