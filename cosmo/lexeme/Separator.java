package cosmo.lexeme;

import java.util.HashMap;

public class Separator {
    public static HashMap<String, String> createSeparatorMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("(", "op_par");
        map.put(")", "cl_par");
        map.put("{", "op_brac");
        map.put("}", "cl_brac");
        map.put(",", "comma");
        map.put(";", "semicolon");
        return map;
    }
}
