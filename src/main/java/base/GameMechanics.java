package base;

import services.AccountService.UserDataSet;

import java.util.Map;

public interface GameMechanics {

    void addUser(UserDataSet user);

    void setChoice(UserDataSet user, String choice);

    void starGame(UserDataSet first, UserDataSet second);

    void setUserState(UserDataSet user, UserState state);

    Map<String, UserDataSet> getUsers();

    void run();
}
