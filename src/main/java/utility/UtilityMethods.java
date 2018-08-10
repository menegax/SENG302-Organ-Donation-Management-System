package utility;

public class UtilityMethods {

    private static Object[] shiftArrayLeft(Object[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        Object start = array[0];
        System.arraycopy(array, 1, array, 0, array.length - 1);
        array[array.length - 1] = start;
        return array;
    }
}
