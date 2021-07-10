/**
 * @author 86171
 */
public class TransformUtils {
    public static Object toNum(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e1) {
            try {
                return Double.parseDouble(str);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static boolean isNum(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (Exception e1) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static boolean isNum(Object oj){
        return oj instanceof Number;
    }

    public static double toDouble(Object num) {
        if (num instanceof Double) {
            return (double) num;
        } else {
            return ((Number) num).doubleValue();
        }
    }

    public static long toLong(Object num) {
        if (num instanceof Integer) {
            return (long) (int) num;
        } else if (num instanceof Long) {
            return (long) num;
        }
        return ((Number) num).longValue();
    }

    public static boolean isString(String str){
        if(str.startsWith("'")){
            return true;
        }else {
            return str.startsWith("\"") && str.endsWith("\"");
        }
    }

    public static String getString(String val){
        if(val.startsWith("\"")){
           return val.substring(1,val.length()-1);
        }else {
            return val.substring(1);
        }
    }
    public static void main(String[] args) {
        System.out.println(isNum(1.0));
    }
}
