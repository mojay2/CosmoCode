package cosmo;

import cosmo.interpreter.VariableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {
    private static Scanner scanner = new Scanner(System.in);

    // Method to enter a new scope
    private static void enterScope(Stack<HashMap<String, String>> scopes) {
        // Create a new scope (HashMap) and push it onto the stack
        HashMap<String, String> newScope = new HashMap<>();
        scopes.push(newScope);
    }

    // Method to exit the current scope
    public static void exitScope(Stack<HashMap<String, String>> scopes) {
        // Check if there is at least one scope to exit
        if (!scopes.isEmpty()) {
            // Pop the current scope from the stack
            HashMap<String, String> currentScope = scopes.pop();
            // Add the popped scope to the global scopedVariablesList
            Parser.scopedVariablesList.add(new HashMap<>(currentScope));
        }
    }

    // Method to assign a variable to the current scope
    public static void assignVariable(String key, String value, Stack<HashMap<String, String>> scopes) {
        // Check if there is at least one scope in the stack
        if (!scopes.isEmpty()) {
            // Assign the variable to the current scope (top of the stack)
            scopes.peek().put(key, value);
        }
    }

    // Method to look up a variable by its identifier in the scopes
    private static String lookupVariable(String identifier, Stack<HashMap<String, String>> scopes) {
        // Iterate through the scopes starting from the top of the stack
        for (int i = scopes.size() - 1; i >= 0; i--) {
            // Check if the variable exists in the current scope
            if (scopes.get(i).containsKey(identifier)) {
                // Return the value of the variable
                return scopes.get(i).get(identifier);
            }
        }
        // Return null if the variable is not found in any scope
        return null;
    }

    public static void interpret(ParseTreeNode root, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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
                transmission(root, valueTable, scopes);
                break;
            case "receptionStmt":
                reception(root, valueTable, scopes);
                break;
            case "whirlLoop":
                whirl(root, valueTable, scopes);
                break;
            case "launchWhirlLoop":
                launchWhirl(root, valueTable, scopes);
                break;
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

    private static List<String> getLeaves(ParseTreeNode node) {
        List<String> leaves = new ArrayList<>();
        if (node.isLeaf()) {
            leaves.add(node.getSymbol());
        } else {
            for (ParseTreeNode child : node.getChildren()) {
                leaves.addAll(getLeaves(child));
            }
        }
        return leaves;
    }

    private static void declaration(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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
                case "arithExp":
                    value = arithmetic(child, valueTable, scopes);
                    break;
            }
        }

        if (identifier != null && value != null) {
            // Check if the identifier is already declared in the current scope
            if (!scopes.peek().containsKey(identifier)) {
                // Create a VariableEntry and set its value
                VariableEntry entry = new VariableEntry(identifier, value, scopes);
                // Add the VariableEntry to the valueTable
                valueTable.put(identifier, entry);
                // Also add the identifier and value to the current scope
                scopes.peek().put(identifier, value);
            } else {
                throw new IllegalStateException(
                        "DECLARATION ERROR: " + identifier + " has already been declared in the current scope.");
            }
        }
    }

    private static String arithmetic(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
        List<String> leaves = getLeaves(node);
        int result = 0;
        String description = "";

        boolean didOperation = true;

        while (didOperation) {
            didOperation = false;

            // Mult & Div
            for (int i = 1; i < leaves.size(); i += 2) {
                String operator = leaves.get(i);
                if (operator.equals("arith_mult") || operator.equals("arith_div")) {
                    int firstOperand;
                    String firstLeaf = leaves.get(i - 1);
                    if (firstLeaf.startsWith("id_")) {
                        String firstOp = lookupVariable(firstLeaf.substring(3), scopes);
                        if (firstOp == null || firstOp == "") {
                            throw new IllegalStateException(
                                    "ARITHMETIC ERROR: " + firstLeaf.substring(3) + " is not defined.");
                        }
                        firstOperand = Integer.parseInt(firstOp);
                    } else if (firstLeaf.startsWith("cmt_")) {
                        String cmt = firstLeaf.substring(4);
                        firstOperand = Integer.parseInt(cmt);
                    } else {
                        firstOperand = Integer.parseInt(firstLeaf);
                    }

                    String nextLeaf = leaves.get(i + 1);
                    int nextOperand;
                    if (nextLeaf.startsWith("id_")) {
                        String nextOp = lookupVariable(nextLeaf.substring(3), scopes);
                        if (nextOp == null || nextOp == "") {
                            throw new IllegalStateException(
                                    "ARITHMETIC ERROR: " + nextLeaf.substring(3) + " is not defined.");
                        }
                        nextOperand = Integer.parseInt(nextOp);
                    } else if (nextLeaf.startsWith("cmt_")) {
                        String cmt = nextLeaf.substring(4);
                        nextOperand = Integer.parseInt(cmt);
                    } else {
                        nextOperand = Integer.parseInt(nextLeaf);
                    }

                    switch (operator) {
                        case "arith_mult":
                            result = firstOperand * nextOperand;
                            break;
                        case "arith_div":
                            if (nextOperand == 0) {
                                throw new IllegalStateException(
                                        "ARITHMETIC ERROR: Division by zero.");
                            }
                            result = firstOperand / nextOperand;
                            break;
                    }

                    description = "Arithmetic: " + firstOperand + " " + operator + " " + nextOperand + " = " + result;

                    // remove processed operands and operator
                    leaves.remove(i - 1);
                    leaves.remove(i - 1);
                    leaves.set(i - 1, String.valueOf(result));
                    i = 0;
                    didOperation = true;
                    break;
                }
            }
        }

        // Addition & Subtraction
        String firstLeaf = leaves.get(0);
        if (firstLeaf.startsWith("id_")) {
            String resultOp = lookupVariable(firstLeaf.substring(3), scopes);
            if (resultOp == null || resultOp == "") {
                throw new IllegalStateException(
                        "ARITHMETIC ERROR: " + firstLeaf.substring(3) + " is not defined.");
            }
            result = Integer.parseInt(resultOp);
        } else if (firstLeaf.startsWith("id_")) {
            String cmt = firstLeaf.substring(4);
            result = Integer.parseInt(cmt);
        } else {
            result = Integer.parseInt(firstLeaf);
        }
        for (int i = 1; i < leaves.size(); i += 2) {
            String firstOp = Integer.toString(result);
            String operator = leaves.get(i);
            String nextLeaf = leaves.get(i + 1);
            int nextOperand;
            if (nextLeaf.startsWith("id_")) {
                String nextOp = lookupVariable(nextLeaf.substring(3), scopes);
                if (nextOp == null || nextOp == "") {
                    throw new IllegalStateException(
                            "ARITHMETIC ERROR: " + nextLeaf.substring(3) + " is not defined.");
                }
                nextOperand = Integer.parseInt(nextOp);
            } else if (nextLeaf.startsWith("cmt_")) {
                String cmt = nextLeaf.substring(4);
                nextOperand = Integer.parseInt(cmt);
            } else {
                nextOperand = Integer.parseInt(nextLeaf);
            }
            switch (operator) {
                case "arith_plus":
                    result += nextOperand;
                    break;
                case "arith_minus":
                    result -= nextOperand;
                    break;
                default:
                    System.out.println("Invalid Operator: " + operator + "\n");
            }
            description = "Arithmetic: " + firstOp + " " + operator + " " + nextOperand + " = " + result;
        }

        return Integer.toString(result);
    }

    private static void assignment(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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
                case "arithExp":
                    value = arithmetic(child, valueTable, scopes);
                    break;
            }
        }

        if (identifier != null && value != null) {
            boolean foundInCurrentScope = scopes.peek().containsKey(identifier);

            if (foundInCurrentScope) {
                // Update the variable in the current scope
                scopes.peek().put(identifier, value);
                // Update the valueTable accordingly
                VariableEntry entry = new VariableEntry(identifier, value, scopes);
                valueTable.put(identifier, entry);
            } else {
                // Check if the identifier exists in outer scopes
                boolean foundInOuterScope = false;
                for (int i = scopes.size() - 2; i >= 0; i--) { // Start from the outermost scope
                    if (scopes.get(i).containsKey(identifier)) {
                        // Update the variable value in the outer scope
                        scopes.get(i).put(identifier, value);
                        // Update the valueTable accordingly
                        VariableEntry entry = new VariableEntry(identifier, value, scopes);
                        valueTable.put(identifier, entry);
                        foundInOuterScope = true;
                        break;
                    }
                }

                if (!foundInOuterScope) {
                    throw new IllegalStateException("ASSIGNMENT ERROR: " + identifier + " has not been declared.");
                }
            }
        }
    }

    private static void transmission(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
        String identifier = null;
        String string = null;

        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    identifier = getLeafValue(child);
                    break;
                case "string":
                    string = getLeafValue(child).replace("\"", "");
                    break;
            }
        }

        if (identifier != null) {
            String assignedValue = lookupVariable(identifier, scopes);
            if (assignedValue != null) {
                System.out.println(assignedValue);
            } else {
                throw new IllegalStateException(
                        "TRANSMISSION ERROR: " + identifier + " has not yet been declared or is out of scope.");
            }
        }

        if (string != null) {
            System.out.println(string);
        }
    }

    private static void reception(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
        String identifier = null;
        String statement = null;
        String value = null;

        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    identifier = getLeafValue(child);
                    if (lookupVariable(identifier, scopes) == null) {
                        throw new IllegalStateException(
                                "RECEPTION ERROR: " + identifier + " has not yet been declared.");
                    }
                    break;
                case "string":
                    statement = getLeafValue(child).replace("\"", "");
                    break;
            }
        }

        if (identifier != null && statement != null) {
            if (lookupVariable(identifier, scopes) != null) {
                System.out.print(statement); // Print user prompt
                value = scanner.nextLine(); // Read user input using the class-level scanner

                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    boolean foundInCurrentScope = scopes.peek().containsKey(identifier);

                    if (foundInCurrentScope) {
                        // Update the variable in the current scope
                        scopes.peek().put(identifier, value);
                        // Update the valueTable accordingly
                        VariableEntry entry = new VariableEntry(identifier, value, scopes);
                        valueTable.put(identifier, entry);
                    } else {
                        // Check if the identifier exists in outer scopes
                        boolean foundInOuterScope = false;
                        for (int i = scopes.size() - 2; i >= 0; i--) { // Start from the outermost scope
                            if (scopes.get(i).containsKey(identifier)) {
                                // Update the variable value in the outer scope
                                scopes.get(i).put(identifier, value);
                                // Update the valueTable accordingly
                                VariableEntry entry = new VariableEntry(identifier, value, scopes);
                                valueTable.put(identifier, entry);
                                foundInOuterScope = true;
                                break;
                            }
                        }

                        if (!foundInOuterScope) {
                            throw new IllegalStateException(
                                    "RECEPTION ERROR: " + identifier + " has not been declared.");
                        }
                    }
                } else {
                    throw new IllegalStateException(
                            "RECEPTION ERROR: Reception input should be a Comet (integer).");
                }
            }
        }
    }

    private static Boolean logical(ParseTreeNode node, HashMap<String, VariableEntry> valueTable) {
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

    private static Boolean relational(ParseTreeNode node, HashMap<String, VariableEntry> valueTable) {
        String leftValue = null;
        String rightValue = null;
        String operator = null;

        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "identifier":
                    String identifier = getLeafValue(child);
                    VariableEntry entry = valueTable.get(identifier);
                    if (entry != null) {
                        if (leftValue == null) {
                            leftValue = entry.getValue();
                        } else {
                            rightValue = entry.getValue();
                        }
                    } else {
                        throw new IllegalStateException(
                                "Identifier '" + identifier + "' not found in the value table.");
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

        if (leftValue == null) {
            throw new IllegalStateException("RELATIONAL ERROR: Left value is null.");
        } else if (rightValue == null) {
            throw new IllegalStateException("RELATIONAL ERROR: Right value is null.");
        } else if (operator == null) {
            throw new IllegalStateException("RELATIONAL ERROR: Operator is null.");
        }

        double left = Double.parseDouble(leftValue);
        double right = Double.parseDouble(rightValue);

        switch (operator) {
            case "comp_not":
                return left != right;
            case "comp_less":
                return left < right;
            case "comp_less_eq":
                return left <= right;
            case "comp_great":
                return left > right;
            case "comp_great_eq":
                return left >= right;
            case "comp_eq":
                return left == right;
            default:
                throw new IllegalStateException("RELATIONAL ERROR: Unknown relational operator: " + operator + ".");
        }
    }

    private static void statementProcessor(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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
                                            transmission(exprChild, valueTable, scopes);
                                            break;
                                        case "receptionStmt":
                                            reception(exprChild, valueTable, scopes);
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
            HashMap<String, VariableEntry> valueTable) {
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

    private static void orbit(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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

    private static void orbitPropel(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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

    private static void orbitNavigatePropel(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
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

    private static final int MAX_ITERATIONS = 200;

    private static void whirl(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
        boolean condition = true;
        int loopCount = 0;

        // Check condition first
        for (ParseTreeNode child : node.getChildren()) {
            switch (child.getSymbol()) {
                case "conditionalExp":
                    condition = conditionProcessor(condition, child, valueTable);
                    break; // You should break out of the outer loop after processing the conditionalExp
            }
        }

        enterScope(scopes);

        // If condition is true, execute the statements
        while (condition) {
            if (++loopCount > MAX_ITERATIONS) {
                throw new IllegalStateException("Maximum loop iterations exceeded");
            }

            statementProcessor(node, valueTable, scopes);

            // Re-evaluate the condition
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "conditionalExp":
                        condition = conditionProcessor(condition, child, valueTable);
                        break;
                }
            }
        }

        exitScope(scopes);
    }

    private static void launchWhirl(ParseTreeNode node, HashMap<String, VariableEntry> valueTable,
            Stack<HashMap<String, String>> scopes) {
        boolean condition = true;
        int loopCount = 0;

        do {
            if (++loopCount > MAX_ITERATIONS) {
                throw new IllegalStateException("Maximum loop iterations exceeded");
            }

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