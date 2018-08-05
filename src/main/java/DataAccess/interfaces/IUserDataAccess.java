package DataAccess.interfaces;

import model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUserDataAccess {

    void addUser(User user);

    void deleteUser(User user);

    Set<User> getUsers();

    Map<Integer, List<User>> searchUsers(String searchTerm);
}
