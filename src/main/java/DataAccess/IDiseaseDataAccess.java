package DataAccess;

import model.Disease;

import java.util.List;

public interface IDiseaseDataAccess {

    int update(String nhi, Disease disease);

    List<Disease> select(String nhi);
}
