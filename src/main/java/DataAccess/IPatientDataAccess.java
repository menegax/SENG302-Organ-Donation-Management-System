package DataAccess;

import model.Patient;

import java.util.List;

public interface IPatientDataAccess {

    /**
     *
     * @param patient -
     * @return -
     */
     public int update (List<Patient> patient);


    /**
     *
     * @param patient -
     * @return -
     */
    public boolean insert (Patient patient);

    /**
     *
     * @param patient -
     * @return -
     */
    public  boolean insert (List<Patient> patient);

    /**
     *
     * @return -
     */
    public List<Patient> select ();

}
