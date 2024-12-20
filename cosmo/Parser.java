package cosmo;

import cosmo.grammar.ProductionChecker;
import cosmo.interpreter.VariableEntry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;

public class Parser {
    private String[] tokens;
    private HashMap<String, VariableEntry> valueTable = new HashMap<>();
    private static Stack<HashMap<String, String>> scopes = new Stack<>();
    public static List<Map<String, String>> scopedVariablesList = new ArrayList<>();

    public Parser(String[] tokens) {
        this.tokens = tokens;
        this.valueTable = new HashMap<>(); // Initialize valueTable
        scopes.push(new HashMap<>());
    }

    int tokenLength = 0;

    void check(String[] stk, List<String[]> dataTable, ParseTreeNode root, HashMap<String, VariableEntry> valueTable2) {
        ProductionChecker.checkProductions(stk, dataTable, root);
    }

    public void parse(int fileNumber) {
        StringBuilder tokenString = new StringBuilder();
        for (String token : tokens) {
            tokenString.append(token).append(" ");
        }
        // Remove trailing space
        String tokensAsString = tokenString.toString().trim();
        tokenLength = tokens.length;

        // Define the file path
        String parserFilePath = "./output/parser/output" + fileNumber + ".csv";
        String parseTreeFilePath = "./output/parse_tree/output" + fileNumber + ".txt";
        String valueTableFilePath = "./output/value_table/output" + fileNumber + ".txt";

        // Define a String array to hold the stk and remainingInput
        String[] stk = new String[tokenLength];
        Arrays.fill(stk, ""); // Initialize stk with empty strings

        ParseTreeNode root = new ParseTreeNode("Program");
        // Define an ArrayList to hold data
        List<String[]> dataTable = new ArrayList<>();

        // Add data dynamically
        dataTable.add(new String[] { "Input String", tokensAsString, "" });
        dataTable.add(new String[] { "", "", "" });
        dataTable.add(new String[] { "Action", "Stack", "Remaining Input" });

        // Iterate over the input tokens
        String action;
        for (int j = 0; j < tokenLength - 1; j++) {
            // Set the action to "SHIFT" initially
            action = "SHIFT";

            // Print action
            dataTable.add(new String[] { action, joinWithoutNull(stk),
                    joinWithoutNull(Arrays.copyOfRange(tokens, j, tokenLength)) });

            // Add token to the stack at the next available position
            root.addChild(new ParseTreeNode(tokens[j]));
            stk[j] = tokens[j];

            // Check stack for production rules
            check(stk, dataTable, root, valueTable);
        }

        // Process the last token separately
        action = "SHIFT";
        dataTable.add(new String[] { action, joinWithoutNull(stk), "" }); // Last token doesn't have remaining input

        // Add the last token to the stack
        stk[tokenLength - 1] = tokens[tokenLength - 1];
        root.addChild(new ParseTreeNode(tokens[tokenLength - 1]));

        // Check stack for production rules after adding the last token
        check(stk, dataTable, root, valueTable);

        // Check stack for production rules after adding the last token for arithmetic
        check(stk, dataTable, root, valueTable);

        // Check if the stack contains only valid tokens
        boolean isValidInput = true;
        String[] validTokens = { "decStmt", "expr", "orbitStmt1", "orbitStmt2", "orbitStmt3", "whirlLoop",
                "launchWhirlLoop" };

        // Filter out empty strings from the stack
        List<String> filteredStack = Arrays.stream(stk)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Convert the filtered stack back to an array
        String[] filteredStk = filteredStack.toArray(new String[0]);

        // Check if the stack contains only valid tokens
        for (String token : filteredStk) {
            boolean tokenFound = false;
            for (String validToken : validTokens) {
                if (token.equals(validToken)) {
                    tokenFound = true;
                    break;
                }
            }
            if (!tokenFound) {
                isValidInput = false;
                break;
            }
        }

        if (!isValidInput) {
            ArrayList<String> list = new ArrayList<>();
            for (int n = 0; n < stk.length; n++) {
                if (!stk[n].equals(""))
                    list.add(stk[n]);
            }
            list = checkProds(list);
            list = retrieveValidTokens(list);
            // System.out.println(list);
            if (list.get(0).equals("arithExp") && list.size() == 1) {
                isValidInput = true;
            }
        }

        String treeString = root.printTree();
        try (PrintWriter out = new PrintWriter(parseTreeFilePath)) {
            // Print output based on input validity
            if (isValidInput) {
                System.out.println("Accept");
                System.out.println("\n-------------------------------------------------\r\n");
                System.out.println("Output:");
                dataTable.add(new String[] { "ACCEPT", "", "" });
                out.println(treeString);
                Interpreter.interpret(root, valueTable, scopes);
            } else {
                System.out.println("Reject\n");
                System.out.println("\n-------------------------------------------------\r\n" + //
                        "\n");
                dataTable.add(new String[] { "REJECT", "", "" });
                out.println("Parse tree cannot be generated for the given input");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Write data to CSV file
        writeOutputToFile(parserFilePath, dataTable);
        // Write final value table to txt file
        writeValueTableToFile(valueTableFilePath);
    }

    public static String joinWithoutNull(String[] arr) {
        StringBuilder result = new StringBuilder();
        for (String str : arr) {
            if (str != null && !str.isEmpty()) {
                result.append(str + " ");
            }
        }
        return result.toString().trim(); // Trim to remove trailing space
    }

    public void writeOutputToFile(String filePath, List<String[]> dataTable) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String[] rowData : dataTable) {
                for (int i = 0; i < rowData.length; i++) {
                    writer.append(rowData[i]);
                    if (i < rowData.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
            // System.out.println("CSV file has been created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeValueTableToFile(String fileName) {
        // Try-with-resources to ensure BufferedWriter is closed after usage
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            int scopeNumber = 1; // Initialize the scope number
            for (Map<String, String> scopedVariables : scopedVariablesList) {
                // Write the scope header
                writer.write("Scope " + scopeNumber + ":");
                writer.newLine();
                // Write each key-value pair in the current scope
                for (Map.Entry<String, String> entry : scopedVariables.entrySet()) {
                    writer.write(entry.getKey() + " : " + entry.getValue());
                    writer.newLine();
                }
                writer.newLine(); // Add a blank line after each scope to separate them
                scopeNumber++; // Increment the scope number
            }
        } catch (IOException e) {
            // Print an error message if an IOException occurs
            System.err.println("Error writing value table to file: " + e.getMessage());
        }

        // Clear all the tables after writing the output file
        scopedVariablesList.clear();
    }

    static ArrayList<String> checkProds(ArrayList<String> tokenList) {
        String action = "REDUCE TO -> ";
        tokenList.add(0, "");
        tokenList.add(0, "");
        tokenList.add(0, "");
        tokenList.add(0, "$");
        tokenList.add(tokenList.size(), "%");
        for (int i = tokenList.size() - 1; i >= 0; i--) {
            if (tokenList.get(i).equals("$"))
                break;
            if (tokenList.get(i).equals(""))
                continue;
            String token = tokenList.get(i);

            // <termPrime> -> arith_mult <factor> <termPrime> | arith_div <factor>
            // <termPrime>
            if (token.equals("termPrime") &&
                    tokenList.get(i - 1).equals("factor") &&
                    (tokenList.get(i - 2).equals("arith_mult") || tokenList.get(i - 2).equals("arith_div"))) {
                tokenList.set(i - 2, "termPrime");
                tokenList.set(i, "");
                tokenList.set(i - 1, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // <arithExpPrime> -> arith_plus <term> <arithExpPrime> | arith_minus <term>
            // <arithExpPrime>
            else if (token.equals("arithExpPrime") &&
                    tokenList.get(i - 1).equals("term") &&
                    (tokenList.get(i - 2).equals("arith_plus") || tokenList.get(i - 2).equals("arith_minus"))) {
                tokenList.set(i - 2, "arithExpPrime");
                tokenList.set(i, "");
                tokenList.set(i - 1, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // <term> -> <factor> <termPrime>
            else if (token.equals("termPrime") &&
                    tokenList.get(i - 1).equals("factor") &&
                    (!tokenList.get(i - 2).equals("arith_mult") && !tokenList.get(i - 2).equals("arith_div"))) {
                tokenList.set(i - 1, "term");
                tokenList.set(i, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // when term is rightmost token, add <arithExpPrime> -> e
            else if (token.equals("term") &&
                    !tokenList.get(i - 1).equals("factor") &&
                    tokenList.get(i + 1).equals("")) {
                tokenList.set(i + 1, "arithExpPrime");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // <arithExp> -> <term><arithExpPrime>
            else if (token.equals("arithExpPrime") &&
                    tokenList.get(i - 1).equals("term") &&
                    (!tokenList.get(i - 2).equals("arith_plus") || !tokenList.get(i - 2).equals("arith_minus"))) {
                tokenList.set(i - 1, "arithExp");
                tokenList.set(i, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // when factor is rightmost token, derive termPrime -> e
            else if (token.equals("factor") &&
                    tokenList.get(i + 1).equals("%")) {
                tokenList.add(i + 1, "termPrime");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            } else if (token.equals("factor") &&
                    tokenList.get(i + 1).equals("%")) {
                tokenList.add(i + 1, "termPrime");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
        }

        return tokenList;
    }

    /**
     * Iterates through the given list of tokens to remove dummy symbols
     * and prints the valid tokens
     * 
     * @param list list of tokens
     */
    static void printValidTokens(ArrayList<String> list) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            if (!element.isEmpty() && !element.equals("$") && !element.equals("%")) {
                System.out.print(element + " ");
            }
        }
        System.out.println();
    }

    /**
     * Iterates through the given list of tokens to remove dummy symbols
     * and returns the updated list of valid tokens
     * 
     * @param list list of tokens
     * @return list of valid tokens
     */
    static ArrayList<String> retrieveValidTokens(ArrayList<String> list) {
        ArrayList<String> result = new ArrayList<>();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            if (!element.isEmpty() && !element.equals("$") && !element.equals("%")) {
                result.add(element);
            }
        }
        return result;
    }
}
