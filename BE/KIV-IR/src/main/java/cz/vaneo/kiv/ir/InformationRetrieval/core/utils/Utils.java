package cz.vaneo.kiv.ir.InformationRetrieval.core.utils;

public class Utils {

    public static String removeLast(String data, String toBeReplaced, String replacement) {
        if (data.endsWith(toBeReplaced)) {
            return data.substring(0, data.length() - 1) + replacement;
        } else {
            return data;
        }
    }
}
