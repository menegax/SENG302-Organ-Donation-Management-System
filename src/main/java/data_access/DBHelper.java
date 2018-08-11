package data_access;

import data_access.factories.MySqlFactory;
import utility.ResourceManager;
import utility.SystemLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DBHelper {
    private MySqlFactory factory = MySqlFactory.getMySqlFactory();

    public void reset() {
        try(Connection connection = factory.getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("CLEAR_DATABASE"));
            statement.executeUpdate();
        } catch (SQLException e) {
            SystemLogger.systemLogger.log(Level.SEVERE, "Couldn't reset the database");
        }
    }
}
