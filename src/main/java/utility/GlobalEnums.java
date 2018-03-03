package utility;

@SuppressWarnings("unused") //is used through the static methods of each enum but, not explicitly
public class GlobalEnums {

    public enum Gender {
        MALE,
        FEMALE,
        OTHER;

        public static Enum getEnumFromString(String value) {
            try{ return Gender.valueOf(value.toUpperCase()); }
            catch (IllegalArgumentException e) {return null;}
        }
    }

    public enum Region {
        NORTHLAND,
        AUCKLAND,
        WAIKATO,
        BAYOFPLENTY,
        GISBORNE,
        HAWKESBAY,
        TARANAKI,
        MANAWATU,
        WELLINGTON,
        TASMAN,
        NELSON,
        MARLBOROUGH,
        WESTCOAST,
        CANTERBURY,
        OTAGO,
        SOUTHLAND;

        public static Enum getEnumFromString(String value) {
            try{ return Region.valueOf(value.toUpperCase()); }
            catch (IllegalArgumentException e) {return null;}
        }
    }

    public enum Organ {
        LIVER,
        KIDNEY,
        PANCREAS,
        HEART,
        LUNG,
        INTESTINE,
        CORNEA,
        MIDDLEEAR,
        SKIN,
        BONE,
        BONEMARROW,
        CONNECTIVETISSUE;

        public static Enum getEnumFromString(String value) {
            try{ return Organ.valueOf(value.toUpperCase()); }
            catch (IllegalArgumentException e) {return null;}
        }
    }

    public enum BloodGroup {
        A_POSITIVE,
        A_NEGATIVE,
        B_POSITIVE,
        B_NEGATIVE,
        AB_POSITIVE,
        AB_NEGATIVE,
        O_POSITIVE,
        O_NEGATIVE;

        public static Enum getEnumFromString(String value) {
            try{ return BloodGroup.valueOf(value.toUpperCase()); }
            catch (IllegalArgumentException e) {return null;}
        }
    }

}
