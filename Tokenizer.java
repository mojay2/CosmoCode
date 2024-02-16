package CosmoCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * A simple tokenizer program that reads input from files, tokenizes the input,
 * and writes the tokenized output to other files.
 */
public class Tokenizer {

    // Regular expressions, Maps and sets for the various token types
    static String whiteSpace = "(([\\s] *) | ((?s). *[\\n\\r].) *)";
    static Map<String, String> symbolTableMap = createSymbolTableMap();
    static Map<String, String> separatorMap = createSeparatorMap();
    static Map<String, String> comparisonMap = createComparisonMap();
    static Map<String, String> logicalOperatorMap = createLogicalMap();
    static Map<String, String> arithmeticOperatorMap = createArithmeticMap();
    static Set<String> commentIndicators = new HashSet<>(Arrays.asList("/*", "*/"));
    static String[] arrayInput;
    static ArrayList<String> symbolTable = new ArrayList<>();
    static int counter = 0;
    static int totalErrors = 0;

    /**
     * Main method to process input and output for multiple files.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // change value of i for different input/output files
        for (int i = 1; i <= 3; i++) {
            processInput("./input/input" + i + ".txt", "./output/output" + i + ".txt");
        }
    }

    /**
     * Processes the input file, tokenizes the input, and writes the tokenized
     * output to the specified file.
     * 
     * @param inputPath  the path to the input file
     * @param outputPath the path to the output file
     */
    public static void processInput(String inputPath, String outputPath) {
        String input = readInput(inputPath);

        System.out.println("=================================================");
        System.out.println("Input from " + inputPath + ":\n" + input);

        // split into array
        arrayInput = input.trim().split("\\s+");
        System.out.println("\nInput Coverted to String Array:\n" + Arrays.toString(arrayInput));

        // pass to tokenizer
        String[] tokenized = tokenize(arrayInput);
        System.out.println("\nTokenized Input:\n" + Arrays.toString(tokenized));
        System.out.println("\nSymbol Table:\n");
        System.out.println(symbolTable);
        System.out.println("\nTotal number of errors:\n" + totalErrors);
        System.out.println("=================================================");
        writeOutputToFile(outputPath, tokenized);

    }

    /**
     * Reads input from a file and returns it as a string.
     * 
     * @param inputPath the path to the input file
     * @return the input read from the file
     */
    public static String readInput(String inputPath) {
        StringBuilder inputBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                inputBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputBuilder.toString();
    }

    /**
     * Writes the tokenized input to a file.
     * 
     * @param outputPath     the path to the output file
     * @param tokenizedInput the tokenized input to write to the file
     */
    public static void writeOutputToFile(String outputPath, String[] tokenizedInput) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            for (String token : tokenizedInput) {
                bw.write(token);
                bw.newLine();
            }
            System.out.println("Tokenized input written to " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tokenizes the input array and returns the tokenized output.
     * 
     * @param input the array of input tokens
     * @return the tokenized output
     */
    public static String[] tokenize(String[] input) {
        ArrayList<String> tokenList = new ArrayList<>();
        counter = 0;
        totalErrors = 0;
        symbolTable = new ArrayList();
        populateSymbols();


        for (int i = 0; i < input.length; i++) {
            if (symbolTableMap.containsKey(input[i])) { // Keyword 
                tokenList.add(symbolTableMap.get(input[i]));
            } else if (input[i].matches("-?\\d+")) { // Comet
                tokenList.add("comet_" + input[i]);
            } else if (separatorMap.containsKey(input[i])) { // Separator
                tokenList.add("sep_" + separatorMap.get(input[i]));
            } else if (comparisonMap.containsKey(input[i])) { // Comparison Operator
                tokenList.add("comp_" + comparisonMap.get(input[i]));
            } else if (logicalOperatorMap.containsKey(input[i])) { // Logical Operator
                tokenList.add("logic_" + logicalOperatorMap.get(input[i]));
            } else if (arithmeticOperatorMap.containsKey(input[i])) { // Arithmetic Operator
                tokenList.add("arith_" + arithmeticOperatorMap.get(input[i]));
            } else if (input[i].equals("=")) { // Assignment Operator
                tokenList.add("assgnmt_oper");
            } else if (input[i].matches("[a-zA-Z0-9_]+")) { // Indentifier / ?Variable?
                tokenList.add("id_" + input[i]);
                if (!symbolTable.contains(input[i])){
                    symbolTable.add(input[i]);
                }
            } else if (input[i].matches(whiteSpace)) { // White Space
            } else if (input[i].startsWith("\"")) { // String
                String stringConst = "";
                for (; !input[i].endsWith("\""); i++){
                    stringConst = stringConst + input[i] + " ";
                }
                tokenList.add(stringConst + input[i]);
            } else if (input[i].startsWith("/*")) { // Comment
                for (; !input[i].endsWith("*/"); i++)
                    ;
                // tokenList.add("comment");
            } else { // Add more cases here
                counter++;
                tokenList.add("invalid_token_" + counter);
                totalErrors++;
            }
        }
        return tokenList.toArray(new String[0]);
    }

    private static Map<String, String> createSeparatorMap() {
        return Map.ofEntries(
                Map.entry("(", "op_par"),
                Map.entry(")", "cl_par"),
                Map.entry("{", "op_brac"),
                Map.entry("}", "cl_brac"),
                Map.entry(",", "comma"),
                Map.entry(";", "semicolon")
        );
    }

    private static Map<String, String> createLogicalMap() {
        return Map.ofEntries(
                Map.entry("&&", "and"),
                Map.entry("||", "or")
        );
    }

    private static Map<String, String> createArithmeticMap() {
        return Map.ofEntries(
            Map.entry("+", "plus"),
            Map.entry("-", "minus"),
            Map.entry("*", "mult"),
            Map.entry("/", "div"),
            Map.entry("++", "incr"),
            Map.entry("--", "decr")
        );
    }

    private static Map<String, String> createComparisonMap() {
        return Map.ofEntries(
            Map.entry("==", "eq"),
            Map.entry(">=", "great_eq"),
            Map.entry("<=", "less_eq"),
            Map.entry("!=", "not"),
            Map.entry(">", "great"),
            Map.entry("<", "less")
        );
    }

    private static Map<String, String> createSymbolTableMap() {
        return Map.ofEntries(
            Map.entry("Comet", "comet_token"),
            Map.entry("Voyage", "voyage_token"),
            Map.entry("Reception", "reception_token"),
            Map.entry("Transmission", "transmission_token"),
            Map.entry("Whirl", "whirl_token"),
            Map.entry("LaunchWhirl", "launchwhirl_token"),
            Map.entry("Orbit", "orbit_token"),
            Map.entry("Navigate", "navigate_token"),
            Map.entry("Propel", "propel_token")
        );
    }

    private static void populateSymbols() { 
        for (String value : separatorMap.values()) {
            symbolTable.add(value);
        }
        for (String value : logicalOperatorMap.values()) {
            symbolTable.add(value);
        }
        for (String value : arithmeticOperatorMap.values()) {
            symbolTable.add(value);
        }
        for (String value : comparisonMap.values()) {
            symbolTable.add(value);
        }
        for (String value : symbolTableMap.values()) {
            symbolTable.add(value);
        }
    }
}

