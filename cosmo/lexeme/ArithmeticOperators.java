package cosmo.lexeme;

import java.util.HashMap;

public class ArithmeticOperators {
    public static HashMap<String, String> createArithmeticMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("+", "plus");
        map.put("-", "minus");
        map.put("*", "mult");
        map.put("/", "div");
        map.put("++", "incr");
        map.put("--", "decr");
        map.put("=", "assign");
        return map;
    }
}
