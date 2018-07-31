package DataAccess;

import model.Disease;
import utility.GlobalEnums.DiseaseState;
import utility.ResourceManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DiseaseDAO extends DataAccessBase implements IDiseaseDataAccess {

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public List<Disease> select(String nhi) {
        List<Disease> diseases = new ArrayList<>();
        try (Connection connection = getConnectionInstance()) {
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_DISEASES_QUERY"));
            connection.setAutoCommit(false);
            statement.setString(1, nhi);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                LocalDate diagnosed = LocalDate.parse(resultSet.getString("DateDiagnosed"));
                String recordState = resultSet.getString("State");
                DiseaseState state = recordState.equals("0") ? null : (recordState.equals("1") ? DiseaseState.CHRONIC : DiseaseState.CURED);
                Disease disease = new Disease(name, state);
                disease.setDateDiagnosed(diagnosed, null); //todo temp - Need to have patient object, or modify setDateDiagnosed in Disease
                diseases.add(disease);
            }
            return diseases;
        } catch (SQLException | IOException e) {
            return null;
        }
    }

    @Override
    public boolean delete() {
        return false;
    }
}
