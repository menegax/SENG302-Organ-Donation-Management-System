package DataAccess.interfaces;

import java.util.List;

public interface ILogDataAccess<T> {

    void saveLogs(List<T> records, String id);

    List<T> getAllLogsByUserId(String id);

    void deleteLogsByUserId(String id);
}
