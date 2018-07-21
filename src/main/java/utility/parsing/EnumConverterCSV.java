package utility.parsing;

import com.univocity.parsers.conversions.Conversion;
import utility.GlobalEnums.*;

import java.util.Arrays;

public class EnumConverterCSV implements Conversion {

    private final Region[] regions = {Region.AUCKLAND, Region.CANTERBURY, Region.GISBORNE, Region.BAYOFPLENTY, Region.HAWKESBAY,
                                Region.MANAWATU, Region.MARLBOROUGH, Region.NELSON, Region.NORTHLAND, Region.OTAGO, Region.SOUTHLAND,
                                Region.TARANAKI, Region.TASMAN, Region.WAIKATO, Region.WELLINGTON, Region.WESTCOAST};
    @Override
    public Object execute(Object o) {
        if (o != null) {
            switch(o.toString().toLowerCase()) {
                case "female":
                case "f":{
                    return "Woman";
                }
                case "male":
                case "m":{
                    return "Man";
                }
            }
            return like(o.toString());
        }
        return null;
    }

    @Override
    public Object revert(Object o) {
        return null;
    }

    private String like(String str) {
        char[] chars1 = str.toLowerCase().replaceAll(" ","").toCharArray();
        for (Region region : regions) {
            char[] chars2 = region.toString().toLowerCase().replaceAll(" ","").toCharArray();
            if (Arrays.equals(chars1, chars2)){
                return region.toString();
            }
        }
        return null;
    }
}
