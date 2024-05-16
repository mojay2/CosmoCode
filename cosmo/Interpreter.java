package cosmo;

import java.util.HashMap;
import java.util.Scanner;

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
            case "assignStmt":
                assignment(root, valueTable);
                break;
            case "transmissionStmt":
                transmission(root, valueTable);
                break;
            case "receptionStmt":
                reception(root, valueTable);
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
                    value = getLeafValue(child);
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
                    System.out.println("DECLARATION ERROR: " + value + " has not yet been been declared.");
                }
            } else {
                System.out.println("DECLARATION ERROR: " + identifier + " has already been declared.");
            }
        }
    }

    private static void assignment(ParseTreeNode node, HashMap<String, String> valueTable) {
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
                    value = getLeafValue(child);
                    break;  
            }
        }
    
        if (identifier != null && value != null) {
            if (valueTable.containsKey(identifier)) {
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    valueTable.put(identifier, value);
                    System.out.println("Assignment: " + identifier + " = " + value);
                    identifier = value;
                } else if (valueTable.containsKey(value)) {
                    String assignedValue = valueTable.get(value);
                    valueTable.put(identifier, assignedValue);
                    System.out.println("Assignment: " + identifier + " = " + assignedValue);
                } else {
                    System.out.println("ASSIGNMENT ERROR: " + value + " has not yet been been declared.");
                }
            } else {
                System.out.println("ASSIGNMENT ERROR: " + identifier + " has not yet been declared.");
            }
        }
    }

    private static void transmission(ParseTreeNode node, HashMap<String, String> valueTable) {
        String value = null;
    
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    value = getLeafValue(child);
                    break;
                case "string":
                    // Handle string
                    value = getLeafValue(child);
                    value = value.replace("\"", "");
                    break;
            }
        }

        if (value != null) {
            if (valueTable.containsKey(value)) {
                String assignedValue = valueTable.get(value);
                System.out.println("Transmission: " + assignedValue);
            } else {
                System.out.println("Transmission: " + value);
            }
        }
    }

    private static void reception(ParseTreeNode node, HashMap<String, String> valueTable) {
        String statement = null;
        String identifier = null;
        String value = null;

        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    identifier = getLeafValue(child);
                    if (valueTable.containsKey(identifier)) {
                        identifier = getLeafValue(child);
                    } else {
                        System.out.println("RECEPTION ERROR: " + identifier + " has not yet been declared.");
                    }
                    break;
                case "string":
                    statement = getLeafValue(child);
                    statement = statement.replace("\"", "");
                    break;
            }
        }

        
        try (Scanner myObj = new Scanner(System.in)) {
            if (valueTable.containsKey(identifier)) {
                System.out.print(statement); // Print user prompt
                value = myObj.nextLine();  // Read user input

            }
        }
        
        
        if (identifier != null && value != null) {
            if (valueTable.containsKey(identifier)) {
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    valueTable.put(identifier, value);
                    System.out.println("Reception: " + identifier + " = " + value);
                    identifier = value;
                } else {
                    System.out.println("RECEPTION ERROR: Reception input should be a Comet (integer).");
                }
            } else {
                System.out.println("RECEPTION ERROR: " + identifier + " has not yet been declared.");
            }
        }
    }
    
}
