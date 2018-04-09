package utility;

@SuppressWarnings("unused")
public class GlobalEnums {

    public enum Gender {
        MALE ("male"),
        FEMALE ("female"),
        OTHER ("other");

        private String value;

        Gender(final String value) {
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
                return Gender.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum Region {
        NORTHLAND ("northland"),
        AUCKLAND ("auckland"),
        WAIKATO ("waikato"),
        BAYOFPLENTY ("bay of plenty"),
        GISBORNE  ("gisborne"),
        HAWKESBAY ("hawkesbay"),
        TARANAKI ("taranaki"),
        MANAWATU ("manawatu"),
        WELLINGTON ("wellington"),
        TASMAN ("tasman"),
        NELSON ("nelson"),
        MARLBOROUGH ("marlborough"),
        WESTCOAST ("west coast"),
        CANTERBURY ("canterbury"),
        OTAGO ("otago"),
        SOUTHLAND ("southland");

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
                return Region.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum Organ {
        LIVER ("liver"),
        KIDNEY ("kidney"),
        PANCREAS ("pancreas"),
        HEART ("heart"),
        LUNG ("lung"),
        INTESTINE ("intestine"),
        CORNEA ("cornea"),
        MIDDLEEAR ("middle ear"),
        SKIN ("skin"),
        BONE ("bone"),
        BONE_MARROW ("bone marrow"),
        CONNECTIVETISSUE ("connective tissue");

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
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum BloodGroup {
        A_POSITIVE ("a positive"),
        A_NEGATIVE ("a negative"),
        B_POSITIVE ("b positive"),
        B_NEGATIVE ("b negative"),
        AB_POSITIVE ("ab positive"),
        AB_NEGATIVE ("ab negative"),
        O_POSITIVE ("o positive"),
        O_NEGATIVE ("o negative");

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
                return BloodGroup.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}
