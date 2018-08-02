package DataAccess.interfaces;

import model.User;

public interface IUserDataAccess {

    public boolean addUser(User user);

    public boolean deleteUser(User user);
}
