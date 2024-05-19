package cosmo.interpreter;

import java.util.HashMap;
import java.util.Stack;

public class VariableEntry {
    private String identifier;
    private String value;
    private Stack<HashMap<String, String>> scopes;

    public VariableEntry(String identifier, String value, Stack<HashMap<String, String>> scopes) {
        this.identifier = identifier;
        this.value = value;
        this.scopes = scopes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getValue() {
        return value;
    }

    public Stack<HashMap<String, String>> getScope() {
        return scopes;
    }

    @Override
    public String toString() {
        return "VariableEntry{" +
                "value='" + value + '\'' +
                ", scopes=" + scopes +
                '}';
    }
}
