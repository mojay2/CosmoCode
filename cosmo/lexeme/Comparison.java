package cosmo.lexeme;

import java.util.HashMap;

public class Comparison {
    public static HashMap<String, String> createComparisonMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("==", "eq");
        map.put(">=", "great_eq");
        map.put("<=", "less_eq");
        map.put("!=", "not");
        map.put(">", "great");
        map.put("<", "less");
        return map;
    }
}
