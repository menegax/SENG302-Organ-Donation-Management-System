package DataAccess.interfaces;

import model.Disease;

import java.util.List;

public interface IDiseaseDataAccess {

    int updateDisease(String nhi, Disease disease);

    List<Disease> getDiseaseByNhi(String nhi);

    public void deleteAllDiseasesByNhi(String nhi);

}
