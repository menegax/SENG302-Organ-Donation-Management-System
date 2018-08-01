package utility;

import java.util.Arrays;

/**
 * Enumerations for the entire app to use
 */
public class GlobalEnums {

//	public enum dbFields {
//		FNAME("FName"), MNAMES("MName"), LNAME("LName"), STREET1("Street1"), STREET2("Street2"),
//		SUBURB("Suburb"), REGION("Region"), ZIP("Zip"), MODIFIED("Modified"), BIRTHDATE("Birth"),
//        CREATED("Created"), DEATHDATE("Death"), BIRTHGENDER("BirthGender"), PREFGENDER("PrefGender"),
//        PREFNAME("PrefName"), HEIGHT("Height"), WEIGHT("Weight"), BLOODGROUP("BloodType"),
//        DONATINGORGANS("DonatingOrgans"), RECEIVINGORGANS("ReceivingOrgans"), HOMEPHONE("HomePhone"),
//        WORKPHONE("WorkPhone"), MOBILEPHONE("MobilePhone"), EMAIL("Email"), ECNAME("ECName"),
//        ECRELATIONSHIP("ECRelationship"), ECHOMEPHONE("ECHomePhone"), ECWORKPHONE("ECWorkPhone"),
//        ECMOBILEPHONE("ECMobilePhone"), ECEMAIL("ECEmail"), TIME("Time"), LEVEL("Level"),
//        MESSAGE("Message"), ACTION("Action"), NAME("Name"), DATEDIAGNOSED("DateDiagnosed"),
//        STATE("State"), SUMMARY("Summary"), DESCRIPTION("Description"), PROCEDUREDATE("ProDate"),
//        AFFECTEDORGANS("AffectedOrgans"), WAITLISTORGAN("Organ");
//
//        private String value;
//
//        dbFields(final String value) {
//            this.value = value;
//        }
//
//
//        public String getValue() {
//            return value != null ? value : "Not set";
//        }
//
//
//        @Override
//        public String toString() {
//            return this.getValue() != null ? this.getValue() : "Not set";
//        }
//
//
//        public static dbFields getEnumFromString(String value) {
//            try {
//                return dbFields.valueOf(value.toUpperCase());
//            }
//            catch (IllegalArgumentException e) {
//                return null;
//            }
//        }
//	}

	public enum UIRegex {

		FNAME("[a-z|A-Z|-]{1,35}"),              MNAME("[a-z|A-Z| |-]{0,70}"),     LNAME("[a-z|A-Z|-]{1,35}"),
		STREET("[a-z|A-Z| |-|,]{0,100}"),        SUBURB("[a-z|A-Z |-]{0,100}"),    STAFFID("[0-9]{1,7}"),
		NHI("[A-Z]{3}[0-9]{4}"),                 HOMEPHONE("0[0-9]{8}"),           WORKPHONE("0[0-9]{8}"),
		MOBILEPHONE("(\\+[0-9]{11}|0[0-9]{9})"), EMAIL("([0-9|a-z|A-Z|.|_|-]+[@][a-z]+([.][a-z])+){0,254}"),
		RELATIONSHIP("[a-z|-|A-Z]{0,30}"),        DISEASENAME("[a-z|-|A-Z]{1,50}"), ZIP("[0-9]{4}"),
		WEIGHT("[0-9]+([.][0-9])?"),             HEIGHT("[0-9]+([.][0-9])?"),      USERNAME("[A-Z|0-9|_|-]{0,30}");

		private String value;

		UIRegex(final String value) {
			this.value = value;
		}

        public String getValue() {
            return value != null ? value : "Not set";
        }

        @Override
        public String toString() {
            return this.getValue() != null ? this.getValue() : "Not set";
        }
	}

    public final static String NONE_ID = "None";

    public enum FilterOption {
        REQUESTEDDONATIONS, DONATIONS, REGION, AGEUPPER, AGELOWER, BIRTHGENDER, DONOR, RECIEVER
    }


    /**
     * Enumerates all options for preferred gender
     */
    public enum PreferredGender {
        MAN("Man"), WOMAN("Woman"), NONBINARY("Non-binary");

        private String value;

        PreferredGender(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value != null ? value : "Not set";
        }

        @Override
        public String toString() {
            return this.getValue() != null ? this.getValue() : "Not set";
        }


        public static PreferredGender getEnumFromString(String value) {
            try {
                return PreferredGender.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum UserTypes {
    	PATIENT("PATIENT"), CLINICIAN("CLINICIAN"), ADMIN("ADMIN");

        private String value;

        UserTypes(final String value) { this.value = value; }

        public String getValue() {
            return value != null ? value : "Not set";
        }

        public static UserTypes getEnumFromString(String value) {
            try {
                return UserTypes.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     * Enumerates all options for birth gender
     */
    public enum BirthGender {
        MALE("Male"), FEMALE("Female");

        private String value;


        BirthGender(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value != null ? value : "Not set";
        }


        @Override
        public String toString() {
            return this.getValue() != null ? this.getValue() : "Not set";
        }


        public static BirthGender getEnumFromString(String value) {
            try {
                return BirthGender.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     * Enumerates all options for region
     */
    public enum Region {
        NORTHLAND("Northland"), AUCKLAND("Auckland"), WAIKATO("Waikato"), BAYOFPLENTY("Bay of Plenty"), GISBORNE("Gisborne"), HAWKESBAY("Hawkes Bay"), TARANAKI(
                "Taranaki"), MANAWATU("Manawatu"), WELLINGTON("Wellington"), TASMAN("Tasman"), NELSON("Nelson"), MARLBOROUGH("Marlborough"), WESTCOAST(
                "West Coast"), CANTERBURY("Canterbury"), OTAGO("Otago"), SOUTHLAND("Southland");

        private String value;


        Region(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }


        @Override
        public String toString() {
            return this.getValue();
        }


        public static Region getEnumFromString(String value) {
            try {
                return Region.valueOf(value.toUpperCase()
                        .replaceAll("\\s+", ""));
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     * Enumerates all options for organs
     */

    /**
     *
     */
    public enum Organ {
        LIVER("liver"), KIDNEY("kidney"), PANCREAS("pancreas"), HEART("heart"), LUNG("lung"), INTESTINE("intestine"), CORNEA("cornea"), MIDDLEEAR(
                "middle ear"), SKIN("skin"), BONE("bone"), BONEMARROW("bone marrow"), CONNECTIVETISSUE("connective tissue");

        private String value;


        Organ(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }


        @Override
        public String toString() {
            return this.getValue();
        }


        public static Organ getEnumFromString(String value) {
            try {
                return Organ.valueOf(value.replaceAll(" ", "").toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     * Enumerates all options for blood group
     */
    public enum BloodGroup {
        A_POSITIVE("A+"), A_NEGATIVE("A-"), B_POSITIVE("B+"), B_NEGATIVE("B-"), AB_POSITIVE("AB+"), AB_NEGATIVE(
                "AB-"), O_POSITIVE("O+"), O_NEGATIVE("O-");

        private String value;


        BloodGroup(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }


        @Override
        public String toString() {
            return this.getValue();
        }


        public static BloodGroup getEnumFromString(String value) {
            return Arrays.stream(BloodGroup.values())
                    .filter(v -> v.value.equals(value))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException(""));
        }
    }

    /**
     * Enumerates all options for undoable screens
     */
    public enum UndoableScreen {
        CLINICIANSEARCHPATIENTS("clinicianSearchPatients"), CLINICIANPROFILEUPDATE("clinicianProfileUpdate"), PATIENTMEDICATIONS("patientMedications"), PATIENTREGISTER(
                "patientRegister"), PATIENTUPDATECONTACTS("patientUpdateContacts"), PATIENTUPDATEDONATIONS("patientUpdateDonations"), PATIENTUPDATEPROFILE(
                "patientUpdateProfile"), PATIENTUPDATEDIAGNOSIS("patientUpdateDiagnosis"), PATIENTUPDATEREQUIREMENTS("patientUpdateRequirements"), CLINICIANDIAGNOSIS(
                "clinicianDiagnosis"), PATIENTPROCEDUREFORM("patientProcedureForm"), PATIENTPROCEDURES("patientProcedures"), ADMINISTRATORUSERREGISTER("administratorUserRegister"),
        ADMINISTRATORSEARCHUSERS("administratorSearchUsers"), ADMINISTRATORPROFILEUPDATE("administratorProfileUpdate");

        private String value;

        UndoableScreen(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.getValue();
        }
    }

    /**
     * Enumerates all possible disease states
     */
    public enum DiseaseState {
        CURED("cured"), CHRONIC("chronic");

        private String value;


        DiseaseState(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }


        @Override
        public String toString() {
            return this.getValue();
        }


        public static DiseaseState getEnumFromString(String value) {
            try {
                return DiseaseState.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }

    }

    /**
     * Enumerates all possible deregistration reasons
     */
    public enum DeregistrationReason {
        ERROR("Registering of the organ was an error"), CURED("Treatment has cured the disease, no longer requiring an organ transplant"), DIED(
                "Receiver has died"), RECEIVED("Receiver has received an organ transplant");

        private String value;


        DeregistrationReason(final String value) {
            this.value = value;
        }
    }

    /**
     * Enum for a patient status; whether donating, receiving, or both
     */
    public enum Status {
        RECEIVING("receiving"), DONATING("donating"), BOTH("both");

        private String value;


        Status(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value;
        }


        @Override
        public String toString() {
            return this.getValue();
        }

        public static Enum getEnumFromString(String value) {
            try {
                return Status.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum MedicationStatus {
        CURRENT(0), HISTORY(1);

        private int value;

        MedicationStatus(int value) { this.value = value; }

        public int getValue() {
            return value;
        }

    }

    public enum FactoryType {
        MYSQL(1),LOCAL(1);
        private int value;

        FactoryType(int value) { this.value = value; }

        public int getValue() {
            return value;
        }
    }
}
