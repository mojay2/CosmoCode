package cosmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Interpreter {
    private static Scanner scanner = new Scanner(System.in);

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
            case "orbitStmt1":
                orbit(root, valueTable);
                break;
            case "orbitStmt2":
                orbitPropel(root, valueTable);
                break;
            case "orbitStmt3":
                orbitNavigatePropel(root, valueTable);
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
            case "whirlLoop":
                whirl(root, valueTable);
                break;
            case "launchWhirlLoop":
                launchWhirl(root, valueTable);
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
                case "arithExp":
                    value = Integer.toString(arithmetic(child, valueTable));
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

    private static int arithmetic(ParseTreeNode node, HashMap<String, String> valueTable) {
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
                        String id = firstLeaf.substring(3);
                        firstOperand = Integer.parseInt(valueTable.get(id));
                    } else if (firstLeaf.startsWith("cmt_")) {
                        String cmt = firstLeaf.substring(4);
                        firstOperand = Integer.parseInt(cmt);
                    } else {
                        firstOperand = Integer.parseInt(firstLeaf);
                    }

                    String nextLeaf = leaves.get(i + 1);
                    int nextOperand;
                    if (nextLeaf.startsWith("id_")) {
                        String id = nextLeaf.substring(3);
                        nextOperand = Integer.parseInt(valueTable.get(id));
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
                            result = firstOperand / nextOperand; // !! add logic here for division by zero
                            break;
                    }

                    description = "Arithmetic: " + firstOperand + " " + operator + " " + nextOperand + " = " + result;
                    System.out.println(description);

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
            String id = firstLeaf.substring(3); // Remove "id_" prefix
            result = Integer.parseInt(valueTable.get(id));
        } else if (firstLeaf.startsWith("id_")) {
            String cmt = firstLeaf.substring(4); // Remove "id_" prefix
            result = Integer.parseInt(cmt);
        } else {
            result = Integer.parseInt(firstLeaf);
        }
        description = "Arithmetic: " + result + " ";
        for (int i = 1; i < leaves.size(); i += 2) {
            String operator = leaves.get(i);
            String nextLeaf = leaves.get(i + 1);
            int nextOperand;
            if (nextLeaf.startsWith("id_")) {
                String id = nextLeaf.substring(3); // Remove "id_" prefix
                nextOperand = Integer.parseInt(valueTable.get(id));
            } else if (nextLeaf.startsWith("cmt_")) {
                String cmt = nextLeaf.substring(4); // Remove "id_" prefix
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
            description += operator + " " + nextOperand + " = " + result;
            System.out.println(description);
        }

        return result;
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
                case "arithExp":
                    value = Integer.toString(arithmetic(child, valueTable));
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
            value = scanner.nextLine(); // Read user input using the class-level scanner

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
            throw new IllegalStateException("Invalid relational expression");
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
                throw new IllegalStateException("Unknown relational operator: " + operator);
        }
    }

    private static void statementProcessor(ParseTreeNode node, HashMap<String, String> valueTable) {
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
                                            declaration(exprChild, valueTable);
                                            break;
                                        case "assignStmt":
                                            assignment(exprChild, valueTable);
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

    private static void orbit(ParseTreeNode node, HashMap<String, String> valueTable) {
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
            statementProcessor(node, valueTable);
        }
    }

    private static void orbitPropel(ParseTreeNode node, HashMap<String, String> valueTable) {
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
            statementProcessor(node, valueTable);
        } else {
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "propelStmt":
                        statementProcessor(child, valueTable);
                        break;
                }
            }
        }
    }

    private static void orbitNavigatePropel(ParseTreeNode node, HashMap<String, String> valueTable) {
        boolean condition = true;
        boolean condition2 = true;

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
                                condition = conditionProcessor(condition, child, valueTable);
                        }
                    }
                    break; // You should break out of the outer loop after processing the conditionalExp
            }
        }

        // If condition is true, execute the statements
        if (condition) {
            statementProcessor(node, valueTable);
        } else if (condition2) {
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "navigateStmt":
                        statementProcessor(child, valueTable);
                        break;
                }
            }
        } else {
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "propelStmt":
                        statementProcessor(child, valueTable);
                        break;
                }
            }
        }
    }

    private static void whirl(ParseTreeNode node, HashMap<String, String> valueTable) {
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
            statementProcessor(node, valueTable);

            // Re-evaluate the condition
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "conditionalExp":
                        condition = conditionProcessor(condition, child, valueTable);
                        break;
                }
            }
        }
    }

    private static void launchWhirl(ParseTreeNode node, HashMap<String, String> valueTable) {
        boolean condition = true;
        do {
            // Execute the statements
            statementProcessor(node, valueTable);

            // Evaluate the condition
            condition = false; // Reset condition
            for (ParseTreeNode child : node.getChildren()) {
                switch (child.getSymbol()) {
                    case "conditionalExp":
                        condition = conditionProcessor(condition, child, valueTable);
                        break;
                }
            }
        } while (condition);
    }
}