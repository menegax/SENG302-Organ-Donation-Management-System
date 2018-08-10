package service.interfaces;

public interface IUserDataService {


    /**
     * Will push local db to remote db (every object)
     */
    public void save();

    /**
     * Resets and truncates local db + remote
     */
    void clear();
}
