package DataAccess;

import model.Disease;

import java.util.List;

public interface IDiseaseDataAccess {

    boolean update();

    boolean insert();

    List<Disease> select();

    boolean delete();

}
