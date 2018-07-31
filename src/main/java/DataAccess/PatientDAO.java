package DataAccess;

import model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PatientDAO extends DataAccessBase implements IPatientDataAccess{


    @Override
    public int update(List<Patient> patients) {
        try (Connection connection = getConnectionInstance()) {
            for (Patient patient : patients) {
                connection.setAutoCommit(false);
                PreparedStatement command = connection.prepareStatement("");
                command.executeUpdate();
                connection.commit();

            }
        } catch (SQLException e) {

        }
        return 0;
    }

    @Override
    public boolean insert(Patient patient) {
        return false;
    }

    @Override
    public boolean insert(List<Patient> patient) {
        return false;
    }

    @Override
    public List<Patient> select() {
        return null;
    }
}
