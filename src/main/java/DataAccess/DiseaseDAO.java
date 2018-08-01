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

public class DiseaseDAO implements IDiseaseDataAccess {


    private DataAccessHelper dataAccessHelper;

    DiseaseDAO () {
        dataAccessHelper = DataAccessHelper.getDataAccessHelper();
    }

    @Override
    public int update(String nhi, Disease disease) {
        try (Connection connection = dataAccessHelper.getConnectionInstance()) {
            deleteAll(connection, nhi);
            PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("UPDATE_PATIENT_DISEASES_QUERY"));
            connection.setAutoCommit(false);
            statement.setString(1, nhi);
            statement.setString(2, disease.getDiseaseName());
            statement.setString(3, disease.getDateDiagnosed().toString());
            String diseaseState = disease.getDiseaseState() == null ? "0" : (disease.getDiseaseState() == DiseaseState.CHRONIC ? "1" : "2");
            statement.setString(4, diseaseState);
            return statement.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public List<Disease> select(String nhi) {
        try (Connection connection1 = dataAccessHelper.getConnectionInstance()){
            connection1.setAutoCommit(false);
            List<Disease> diseases = new ArrayList<>();
            PreparedStatement statement = connection1.prepareStatement(ResourceManager.getStringForQuery("SELECT_PATIENT_DISEASES_QUERY"));
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

    private void deleteAll(Connection connection, String nhi) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(ResourceManager.getStringForQuery("DELETE_PATIENT_DISEASES_QUERY"));
        connection.setAutoCommit(false);
        statement.setString(1, nhi);
        statement.executeUpdate();
    }
}
