package utility.CSVParsing;

import com.univocity.parsers.conversions.Conversion;
import utility.GlobalEnums.*;

public class EnumConverterCSV implements Conversion {

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
        }
        return null;
    }

    @Override
    public Object revert(Object o) {
        return null;
    }
}
