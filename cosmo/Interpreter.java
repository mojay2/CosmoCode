package cosmo;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {   
    private static Scanner scanner = new Scanner(System.in);

    private static void enterScope(Stack<HashMap<String, String>> scopes) {
        scopes.push(new HashMap<>());
    }
    
    private static void exitScope(Stack<HashMap<String, String>> scopes) {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    private static String lookupVariable(String identifier, Stack<HashMap<String, String>> scopes) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(identifier)) {
                return scopes.get(i).get(identifier);
            }
        }
        return null; // Variable not found in any scope
    }
    
    
    
    public static void interpret(ParseTreeNode root, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        if (root == null) {
            return;
        }

        switch (root.getSymbol()) {
            case "Program":
                enterScope(scopes);
                for (ParseTreeNode child : root.getChildren()) {
                    interpret(child, valueTable, scopes);
                }
                exitScope(scopes);
                break;
            case "orbitStmt1":
                orbit(root, valueTable, scopes);
                break;
            case "orbitStmt2":
                orbitPropel(root, valueTable, scopes);
                break;
            case "orbitStmt3":
                orbitNavigatePropel(root, valueTable, scopes);
                break;
            case "decStmt":
                declaration(root, valueTable, scopes);
                break;
            case "assignStmt":
                assignment(root, valueTable, scopes);
                break;
            case "transmissionStmt":
                transmission(root, valueTable);
                break;
            case "receptionStmt":
                reception(root, valueTable);
                break;
            case "whirlLoop":
                whirl(root, valueTable, scopes);
                break;
            case "launchWhirlLoop":
                launchWhirl(root, valueTable, scopes);
                break;

            // Add more cases for other statement types
            default:
                for (ParseTreeNode child : root.getChildren()) {
                    interpret(child, valueTable, scopes);
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

    private static void declaration(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        String identifier = null;
        String value = null;
    
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    if (identifier == null) {
                        identifier = getLeafValue(child);
                    } else {
                        value = getLeafValue(child);
                    }
                    break;
                case "comet_literal":
                    value = getLeafValue(child);
                    break;
            }
        }
    
        if (identifier != null && value != null) {
            // Check if the identifier is already declared in the current scope
            if (!scopes.peek().containsKey(identifier)) {
                // Check if the value is a valid number or a declared variable
                if (value.matches("-?\\d+(\\.\\d+)?") || lookupVariable(value, scopes) != null) {
                    scopes.peek().put(identifier, value);
                    valueTable.put(identifier, value);  // Update valueTable
                    System.out.println("Declaration: " + identifier + " = " + value);
                } else {
                    System.out.println("DECLARATION ERROR: " + value + " has not yet been declared.");
                }
            } else {
                System.out.println("DECLARATION ERROR: " + identifier + " has already been declared in the current scope.");
            }
        }
    }

    private static void assignment(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        String identifier = null;
        String value = null;
    
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    if (identifier == null) {
                        identifier = getLeafValue(child);
                    } else {
                        value = getLeafValue(child);
                    }
                    break;
                case "comet_literal":
                    value = getLeafValue(child);
                    break;
            }
        }
    
        if (identifier != null && value != null) {
            // Check if the identifier is declared in any scope
            if (lookupVariable(identifier, scopes) != null) {
                // Check if the value is a valid number or a declared variable
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    // Update the variable in the most specific scope
                    for (int i = scopes.size() - 1; i >= 0; i--) {
                        if (scopes.get(i).containsKey(identifier)) {
                            scopes.get(i).put(identifier, value);
                            valueTable.put(identifier, value);  // Update valueTable
                            System.out.println("Assignment: " + identifier + " = " + value);
                            break;
                        }
                    }
                } else if (lookupVariable(value, scopes) != null) {
                    String assignedValue = lookupVariable(value, scopes);
                    // Update the variable in the most specific scope
                    for (int i = scopes.size() - 1; i >= 0; i--) {
                        if (scopes.get(i).containsKey(identifier)) {
                            scopes.get(i).put(identifier, assignedValue);
                            valueTable.put(identifier, assignedValue);  // Update valueTable
                            System.out.println("Assignment: " + identifier + " = " + assignedValue);
                            break;
                        }
                    }
                } else {
                    System.out.println("ASSIGNMENT ERROR: " + value + " has not yet been declared.");
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
                    if (!valueTable.containsKey(identifier)) {
                        System.out.println("RECEPTION ERROR: " + identifier + " has not yet been declared.");
                    }
                    break;
                case "string":
                    statement = getLeafValue(child).replace("\"", "");
                    break;
            }
        }
    
        if (valueTable.containsKey(identifier)) {
            System.out.print(statement); // Print user prompt
            value = scanner.nextLine();  // Read user input using the class-level scanner
    
            if (value.matches("-?\\d+(\\.\\d+)?")) {
                valueTable.put(identifier, value);
                System.out.println("Reception: " + identifier + " = " + value);
            } else {
                System.out.println("RECEPTION ERROR: Reception input should be a Comet (integer).");
            }
        }
    }

    private static Boolean logical(ParseTreeNode node, HashMap<String, String> valueTable) {
        Boolean leftValue = null;
        Boolean rightValue = null;
        String operator = null;
        Boolean result = null;

        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "relationalExp":
                    if (leftValue == null) {
                        leftValue = relational(child, valueTable);
                    } else {
                        rightValue = relational(child, valueTable);
                    }
                    break;
                case "logicalOp":
                    operator = getLeafValue(child);

            }
        }

        if (operator.equals("logic_and")) {
            result = leftValue && rightValue;
        } else if (operator.equals("logic_or")) {
            result = leftValue || rightValue;
        }
        return result;
    }

    private static Boolean relational(ParseTreeNode node, HashMap<String, String> valueTable) {
        String leftValue = null;
        String rightValue = null;
        String operator = null;

        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    String identifier = getLeafValue(child);
                    if (leftValue == null) {
                        leftValue = valueTable.get(identifier);
                    } else {
                        rightValue = valueTable.get(identifier);

                    }
                    break;
                case "comet_literal":
                    String literal = getLeafValue(child);
                    if (leftValue == null) {
                        leftValue = literal;
                    } else {
                        rightValue = literal;
                    }
                    break;
                case "relationalOp":
                    operator = getLeafValue(child);
                    break;
            }
        }

        if (leftValue == null || rightValue == null || operator == null) {
            if (leftValue == null) {
                System.err.println("Left Value is Null");
            } else if (rightValue == null) {
                System.out.println( "left value is " + leftValue);
                System.err.println("Right Value is Null");
            } else {
                System.err.println("Operator is Null");
            }
            throw new IllegalStateException("Invalid relational expression");
        }

        double left = Double.parseDouble(leftValue);
        double right = Double.parseDouble(rightValue);

        switch (operator) {
            case "comp_not":
                System.out.println( "REL: " + left + "!=" + right);
                return left != right;
            case "comp_less":
                System.out.println( "REL: " + left + "<" + right);
                return left < right;
            case "comp_less_eq":
                System.out.println( "REL: " + left + "<=" + right);
                return left <= right;
            case "comp_great":
                System.out.println( "REL: " + left + ">" + right);
                return left > right;
            case "comp_great_eq":
                System.out.println( "REL: " + left + ">=" + right);
                return left >= right;
            case "comp_eq":
                System.out.println( "REL: " + left + "==" + right);
                return left == right;
            default:
                throw new IllegalStateException("Unknown relational operator: " + operator);
        }
    }

    private static void statementProcessor(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        for (ParseTreeNode child : node.getChildren()) { // Iterate over children of stmt
            switch (child.getSymbol()) {
                case "stmt":
                    for (ParseTreeNode stmtChild : child.getChildren()) { // Iterate over children of expr
                        switch (stmtChild.getSymbol()) {
                            case "expr":
                                for (ParseTreeNode exprChild : stmtChild.getChildren()) { // Iterate over children of
                                                                                          // expr
                                    switch (exprChild.getSymbol()) {
                                        case "decStmt":
                                            declaration(exprChild, valueTable, scopes);
                                            break;
                                        case "assignStmt":
                                            assignment(exprChild, valueTable, scopes);
                                            break;
                                        case "transmissionStmt":
                                            transmission(exprChild, valueTable);
                                            break;
                                        case "receptionStmt":
                                            reception(exprChild, valueTable);
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    }

    private static boolean conditionProcessor(Boolean condition, ParseTreeNode node,
            HashMap<String, String> valueTable) {
        for (ParseTreeNode condChild : node.getChildren()) { // Iterate over children of conditionalExp
            switch (condChild.getSymbol()) {
                case "logicalExp":
                    condition = logical(condChild, valueTable);
                    if (!condition) {
                        // If condition becomes false, no need to check further
                        break;
                    }
                    break;
                case "relationalExp":
                    condition = relational(condChild, valueTable);
                    if (!condition) {
                        // If condition becomes false, no need to check further
                        break;
                    }
                    break;
            }
        }
        return condition;
    }

    private static void orbit(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        boolean condition = true;

        // Check condition first
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "conditionalExp":
                    condition = conditionProcessor(condition, child, valueTable);
                    break; // You should break out of the outer loop after processing the conditionalExp
            }
        }

        // If condition is true, execute the statements
        if (condition) {
            enterScope(scopes);
            statementProcessor(node, valueTable, scopes);
            exitScope(scopes);
        }
    }

    private static void orbitPropel(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        boolean condition = true;

        // Check condition first
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "conditionalExp":
                    condition = conditionProcessor(condition, child, valueTable);
                    break; // You should break out of the outer loop after processing the conditionalExp
            }
        }

        // If condition is true, execute the statements
        if (condition) {
            enterScope(scopes);
            statementProcessor(node, valueTable, scopes);
            exitScope(scopes);
        } else {
            enterScope(scopes);
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "propelStmt":
                        statementProcessor(child, valueTable, scopes);
                        break;
                }
            }
            exitScope(scopes);
        }
    }

    private static void orbitNavigatePropel(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        Boolean condition = null;
        Boolean condition2 = null;

        // Check condition first
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "conditionalExp":
                    condition = conditionProcessor(condition, child, valueTable);
                    break; // You should break out of the outer loop after processing the conditionalExp
                case "navigateStmt":
                    for (ParseTreeNode navChild : child.getChildren()) { // Iterate over children of conditionalExp
                        switch (navChild.getSymbol()) {
                            case "conditionalExp":
                                condition2 = conditionProcessor(condition, navChild, valueTable);
                        }
                    }
                    break; // You should break out of the outer loop after processing the conditionalExp
            }
        }

        System.out.println("CONDITION1: " + condition);
        System.out.println("CONDITION2: " + condition2);

        // If condition is true, execute the statements
        if (condition) {
            enterScope(scopes);
            statementProcessor(node, valueTable, scopes);
            exitScope(scopes);
        } else if (condition2) {
            enterScope(scopes);
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "navigateStmt":
                        statementProcessor(child, valueTable, scopes);
                        break;
                }
            }
            exitScope(scopes);
        } else {
            enterScope(scopes);
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "propelStmt":
                        statementProcessor(child, valueTable, scopes);
                        break;
                }
            }
            exitScope(scopes);
        }
    }

    private static void whirl(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        boolean condition = true;
        // Check condition first
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "conditionalExp":
                    condition = conditionProcessor(condition, child, valueTable);
                    break; // You should break out of the outer loop after processing the conditionalExp
            }
        }

        // If condition is true, execute the statements
        while (condition) {
            enterScope(scopes);
            statementProcessor(node, valueTable, scopes);

            // Re-evaluate the condition
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "conditionalExp":
                        condition = conditionProcessor(condition, child, valueTable);
                        break;
                }
            }
            exitScope(scopes);
        }
    }

    private static void launchWhirl(ParseTreeNode node, HashMap<String, String> valueTable, Stack<HashMap<String, String>> scopes) {
        boolean condition = true;
        do {
            // Execute the statements
            enterScope(scopes);
            statementProcessor(node, valueTable, scopes);

            // Evaluate the condition
            condition = false; // Reset condition
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "conditionalExp":
                        condition = conditionProcessor(condition, child, valueTable);
                        break;
                }
            }
            exitScope(scopes);
        } while (condition);
    }
}