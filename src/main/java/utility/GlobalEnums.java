package utility;

public class GlobalEnums {

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

    public enum BloodGroup {
        A_POSITIVE("A positive"), A_NEGATIVE("A negative"), B_POSITIVE("B positive"), B_NEGATIVE("B negative"), AB_POSITIVE("AB positive"), AB_NEGATIVE(
                "AB negative"), O_POSITIVE("O positive"), O_NEGATIVE("O negative");

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

    public enum UndoableScreen {
        CLINICIANSEARCHPATIENTS("clinicianSearchPatients"), CLINICIANPROFILEUPDATE("clinicianProfileUpdate"), PATIENTMEDICATIONS("patientMedications"),
        PATIENTREGISTER("patientRegister"), PATIENTUPDATECONTACTS("patientUpdateContacts"), PATIENTUPDATEDONATIONS("patientUpdateDonations"),
        PATIENTUPDATEPROFILE("patientUpdateProfile"), PATIENTUPDATEDIAGNOSIS("patientUpdateDiagnosis"), PATIENTUPDATEREQUIREMENTS("patientUpdateRequirements"),
        CLINICIANDIAGNOSIS("clinicianDiagnosis");

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


        public static Enum getEnumFromString(String value) {
            try {
                return BloodGroup.valueOf(value.toUpperCase().replaceAll("\\s+", "_"));
            }
            catch (IllegalArgumentException e) {
                return null;
            }
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

        public static Enum getEnumFromString(String value) {
            try {
                return DiseaseState.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

    }

}
