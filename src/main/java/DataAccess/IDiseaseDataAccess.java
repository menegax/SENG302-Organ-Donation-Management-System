package DataAccess;

import model.Disease;

import java.util.List;

public interface IDiseaseDataAccess {

    boolean update();

    List<Disease> select();

    boolean delete();

}
