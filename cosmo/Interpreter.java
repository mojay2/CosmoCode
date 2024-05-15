package cosmo;

import java.util.HashMap;

public class Interpreter {   
    
    
    public static void interpret(ParseTreeNode root, HashMap<String, String> valueTable) {
        if (root == null) {
            return;
        }

        switch (root.getSymbol()) {
            case "Program":
                for (ParseTreeNode child : root.getChildren()) {
                    interpret(child, valueTable);
                }
                break;
            case "decStmt":
                declaration(root, valueTable);
                break;
            // Add more cases for other statement types
            default:
                for (ParseTreeNode child : root.getChildren()) {
                    interpret(child, valueTable);
                }
                break;
        }
    }

    private static String getLeafValue(ParseTreeNode node) {
        if (node.getChildren().isEmpty()) {
            String leafValue = node.getSymbol();
            if (leafValue.startsWith("id_")) {
                // Trim the "id_" prefix
                leafValue = leafValue.substring(3);
            } else if (leafValue.startsWith("cmt_")) {
                // Trim the "cmt_" prefix
                leafValue = leafValue.substring(4);
            }
            return leafValue;
        } else {
            return getLeafValue(node.getChildren().get(0));
        }
    } 

    private static void declaration(ParseTreeNode node, HashMap<String, String> valueTable) {
        String identifier = null;
        String value = null;
    
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    if (identifier == null) {
                        identifier = getLeafValue(child);
                    } else { // the second identifier encountered is the value (the one after the equal sign)
                        value = getLeafValue(child); 
                    }
                    break;
                case "comet_literal": 
                    if (getLeafValue(child) == null) {
                        break;
                    } else {
                        value = getLeafValue(child);
                    }
                    break;  
            }
        }
    
        if (identifier != null && value != null) {
            if (!valueTable.containsKey(identifier)) {
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    valueTable.put(identifier, value);
                    System.out.println("Declaration: " + identifier + " = " + value);
                    identifier = value;
                } else if (valueTable.containsKey(value)) {
                    String assignedValue = valueTable.get(value);
                    valueTable.put(identifier, assignedValue);
                    System.out.println("Declaration: " + identifier + " = " + assignedValue);
                } else {
                    System.out.println("ERROR: " + value + " has not yet been been declared.");
                }
            } else {
                System.out.println("ERROR: " + identifier + " has already been declared.");
            }
        }
    }
    
}
