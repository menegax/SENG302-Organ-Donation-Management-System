package model_test;

import model.Disease;
import model.Patient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utility.GlobalEnums;

import java.io.InvalidObjectException;
import java.time.LocalDate;
import java.util.logging.Level;

import static java.util.logging.Level.OFF;
import static utility.UserActionHistory.userActions;

public class DiseaseTest {

    private Disease disease;
    private Patient diseaseCarrier;


    @Before
    public void setUp(){
        userActions.setLevel(Level.OFF);
        disease = new Disease("Aids", GlobalEnums.DiseaseState.CHRONIC);
        diseaseCarrier = new Patient("ABC1239", "Joe",null,"Bloggs",LocalDate.of(2018,01,01));
    }


    /**
     * Test that the disease constructor sets the default diagnosed date correctly
     */
    @Test
    public void getDateDiagnosedTest(){
        Assert.assertEquals(disease.getDateDiagnosed(),LocalDate.now());
    }

    /**
     * Check exception is thrown when setting date of disease being diagnosed to the future
     * @throws InvalidObjectException - Invalid date object supplied
     */
    @Test(expected = InvalidObjectException.class)
    public void setDateDiagnosedInFutureTest() throws InvalidObjectException{
        disease.setDateDiagnosed(LocalDate.of(2050,12,12), diseaseCarrier.getBirth());
    }


    /**
     * Check exception is thrown when setting date of disease being diagnosed to be before the birth of the carrier
     * @throws InvalidObjectException - Invalid date object supplied
     */
    @Test(expected = InvalidObjectException.class)
    public void setDateDiagnosedBeforePatientBirthTest() throws InvalidObjectException{
        disease.setDateDiagnosed(LocalDate.of(2000,12,12), diseaseCarrier.getBirth());
    }

    /**
     * Check that a correct date will be set correctly
     */
    @Test
    public void setDateDiagnosedBirthTest() throws InvalidObjectException{
        LocalDate dateDiagnosed = LocalDate.of(2018,02,12);
        disease.setDateDiagnosed(dateDiagnosed, diseaseCarrier.getBirth());
        Assert.assertEquals(disease.getDateDiagnosed(), dateDiagnosed);
    }

}
