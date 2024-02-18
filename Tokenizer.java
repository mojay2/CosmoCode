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
    static HashMap<String, String> reservedWordsMap = createReservedWordsMap();
    static HashMap<String, String> separatorMap = createSeparatorMap();
    static HashMap<String, String> comparisonMap = createComparisonMap();
    static HashMap<String, String> logicalOperatorMap = createLogicalMap();
    static HashMap<String, String> arithmeticOperatorMap = createArithmeticMap();
    static HashMap<String, String> symbolTableMap = createSymbolTableMap();

    static HashSet<String> commentIndicators = new HashSet<>(Arrays.asList("/*", "*/"));
    static String[] arrayInput;
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
        System.out.println(formatMap(symbolTableMap));
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

        for (int i = 0; i < input.length; i++) {
            if (reservedWordsMap.containsKey(input[i])) { // Keyword 
                tokenList.add(reservedWordsMap.get(input[i]));
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
            } else if (input[i].matches("[a-zA-Z0-9_]+")) { // Indentifier / ?Variable?
                if(isReservedWord(input[i])){
                    counter++;
                    tokenList.add("invalid_token_" + counter);
                    totalErrors++;
                }
                tokenList.add("id_" + input[i]);       
                if (!symbolTableMap.containsKey(input[i])){
                    symbolTableMap.put(input[i], "id_"+input[i]);
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

    private static HashMap<String, String> createSeparatorMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("(", "op_par");
        map.put(")", "cl_par");
        map.put("{", "op_brac");
        map.put("}", "cl_brac");
        map.put(",", "comma");
        map.put(";", "semicolon");
        return map;
    }

    private static HashMap<String, String> createLogicalMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("&&", "and");
        map.put("||", "or");
        return map;
    }

    private static HashMap<String, String> createArithmeticMap() {
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

    private static HashMap<String, String> createComparisonMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("==", "eq");
        map.put(">=", "great_eq");
        map.put("<=", "less_eq");
        map.put("!=", "not");
        map.put(">", "great");
        map.put("<", "less");
        return map;
    }

    private static HashMap<String, String> createReservedWordsMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Comet", "comet_token");
        map.put("Voyage", "voyage_token");
        map.put("reception", "reception_token");
        map.put("transmission", "transmission_token");
        map.put("Whirl", "whirl_token");
        map.put("Launch", "launchwhirl_token");
        map.put("Orbit", "orbit_token");
        map.put("Navigate", "navigate_token");
        map.put("Propel", "propel_token");
        return map;
    }

    private static HashMap<String, String> createSymbolTableMap(){
        HashMap<String, String> map = new HashMap<>();
        map.putAll(separatorMap);
        map.putAll(logicalOperatorMap);
        map.putAll(arithmeticOperatorMap);
        map.putAll(comparisonMap);
        map.putAll(reservedWordsMap);
        return map;
    }

    
    /**
     * Formats a map into a string with key-value pairs.
     * @param map The input map to format.
     * @return Formatted string with key-value pairs.
     */
    private static String formatMap(Map<String, String> map) {
        StringBuilder formattedOutput = new StringBuilder();
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formattedOutput.append(entry.getKey())
                            .append(" : ")
                            .append(entry.getValue())
                            .append(" , ");
        }

        // Remove the trailing comma and space
        if (formattedOutput.length() > 2) {
            formattedOutput.setLength(formattedOutput.length() - 2);
        }

        return formattedOutput.toString();
    }

    /**
     * Checks if a given string is a reserved word in the language.
     * @param string The input string to check.
     * @return true if the string is a reserved word, false otherwise.
     */
    private static Boolean isReservedWord(String string){
        List<String> keysList = getKeysList(reservedWordsMap);
        for (int i = 0; i < keysList.size(); i++) {
            keysList.set(i, keysList.get(i).toLowerCase());
        }
        return keysList.contains(string);
    }

    /**
     * Returns a list of keys from a given map.
     * @param map The map to get the keys from.
     * @return List of Keys.
     */
    private static List<String> getKeysList(Map<String, String> map) {
        List<String> keysList = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            keysList.add(entry.getKey());
        }

        return keysList;
    }
}

