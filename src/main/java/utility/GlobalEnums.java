package utility;

public class GlobalEnums {

	public enum dbFields {
		FNAME("FName"), MNAMES("MName"), LNAME("LName"), STREET1("Street1"), STREET2("Street2"),
		SUBURB("Suburb"), REGION("Region"), ZIP("Zip"), MODIFIED("Modified");
		
        private String value;

        dbFields(final String value) {
            this.value = value;
        }


        public String getValue() {
            return value != null ? value : "Not set";
        }


        @Override
        public String toString() {
            return this.getValue() != null ? this.getValue() : "Not set";
        }


        public static Enum getEnumFromString(String value) {
            try {
                return dbFields.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
	}
	
    /**
     *
     */
    public enum PreferredGender {
        MAN("Man"), WOMAN("Woman"), NONBINARY("Non-binary");

        private String value;

        PreferredGender(final String value) { this.value = value; }

        public String getValue() {
            return value != null ? value : "Not set";
        }

        @Override
        public String toString() {
            return this.getValue() != null ? this.getValue() : "Not set";
        }

        public static Enum getEnumFromString(String value) {
            try {
                return PreferredGender.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     *
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

        public static Enum getEnumFromString(String value) {
            try {
                return BirthGender.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     *
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

        public static Enum getEnumFromString(String value) {
            try {
                return Region.valueOf(value.toUpperCase().replaceAll("\\s+", ""));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     *
     */
    public enum Organ {
        LIVER("liver"), KIDNEY("kidney"), PANCREAS("pancreas"), HEART("heart"), LUNG("lung"), INTESTINE("intestine"), CORNEA("cornea"), MIDDLEEAR(
                "middle ear"), SKIN("skin"), BONE("bone"), BONE_MARROW("bone marrow"), CONNECTIVETISSUE("connective tissue");

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

        public static Enum getEnumFromString(String value) {
            try {
                return Organ.valueOf(value.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     *
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

        public static Enum getEnumFromString(String value) {
            try {
                return BloodGroup.valueOf(value.toUpperCase().replaceAll("\\s+", "_"));
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     *
     */
    public enum UndoableScreen {
        CLINICIANSEARCHPATIENTS("clinicianSearchPatients"), CLINICIANPROFILEUPDATE("clinicianProfileUpdate"), PATIENTMEDICATIONS("patientMedications"),
        PATIENTREGISTER("patientRegister"), PATIENTUPDATECONTACTS("patientUpdateContacts"), PATIENTUPDATEDONATIONS("patientUpdateDonations"),
        PATIENTUPDATEPROFILE("patientUpdateProfile"), PATIENTUPDATEDIAGNOSIS("patientUpdateDiagnosis"), PATIENTUPDATEREQUIREMENTS("patientUpdateRequirements"),
        CLINICIANDIAGNOSIS("clinicianDiagnosis"),PATIENTPROCEDUREFORM( "patientProcedureForm" ),PATIENTPROCEDURES("patientProcedures");

        private String value;

        UndoableScreen( final String value ) {
            this.value = value;
        }
    }

    public enum DiseaseState {
        CURED ("cured"),
        CHRONIC ("chronic");

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

    }

    /**
     *
     *
     */
    public enum DeregistrationReason {
        ERROR ("Registering of the organ was an error"),
        CURED ("Treatment has cured the disease, no longer requiring an organ transplant"),
        DIED ("Receiver has died"),
        RECEIVED ("Receiver has received an organ transplant");

        private String value;

        DeregistrationReason(final String value) {
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
}
