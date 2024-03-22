package cosmo.lexeme;

import java.util.HashMap;

public class LogicalOperators {
    public static HashMap<String, String> createLogicalMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("&&", "and");
        map.put("||", "or");
        return map;
    }
}
