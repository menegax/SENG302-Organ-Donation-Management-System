package utility;

import java.util.ResourceBundle;

public class ResourceManager {

    public static String getStringForQuery(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("queries");
        return bundle.getString(key);
    }
}