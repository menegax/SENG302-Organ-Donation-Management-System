package DataAccess.interfaces;

import model.User;

public interface IUserDataAccess {

    public void addUser(User user);

    public void deleteUser(User user);
}
