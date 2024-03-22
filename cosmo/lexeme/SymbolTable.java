package cosmo.lexeme;

import java.util.HashMap;

public class SymbolTable {
    public static HashMap<String, String> createSymbolTableMap() {
        HashMap<String, String> map = new HashMap<>();
        map.putAll(Separator.createSeparatorMap());
        map.putAll(LogicalOperators.createLogicalMap());
        map.putAll(ArithmeticOperators.createArithmeticMap());
        map.putAll(Comparison.createComparisonMap());
        map.putAll(ReservedWords.createReservedWordsMap()); // If reserved words map is also needed
        return map;
    }
}
