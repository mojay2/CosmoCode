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

    // Regular expressions and sets for the various token types
    static String whiteSpace = "(([\\s] *) | ((?s). *[\\n\\r].) *)";
    static Set<String> separators = new HashSet<>(Arrays.asList("(", ")", "{", "}", ",", ";"));
    static Set<String> comparisonOperators = new HashSet<>(Arrays.asList("==", ">=", "<=", "!=", ">", "<"));
    static Set<String> logicalOperators = new HashSet<>(Arrays.asList("&&", "||"));
    static Set<String> arithmeticOperators = new HashSet<>(Arrays.asList("+", "-", "*", "/", "++", "--"));
    static Set<String> commentIndicators = new HashSet<>(Arrays.asList("/*", "*/"));
    static String[] arrayInput;
    static ArrayList<String> symbolTable = new ArrayList<>();

    /**
     * Main method to process input and output for multiple files.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // change value of i for different input/output files
        for (int i = 1; i <= 3; i++) {
            processInput("CosmoCode/input/input" + i + ".txt", "CosmoCode/output/output" + i + ".txt");
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
        for (int i = 0; i < input.length; i++) {
            if (input[i].equals("Comet")) { // Comet Keyword
                tokenList.add("comet_token");
            } else if(input[i].matches("-?\\d+")) {  // Comet
                tokenList.add("comet_" + input[i]);
            } else if(separators.contains(input[i])) { //Separator
                tokenList.add("separator");
            } else if(comparisonOperators.contains(input[i])) {  //Comparison Operator
                tokenList.add("comp_oper");
            } else if(logicalOperators.contains(input[i])) { //Logical Operator
                tokenList.add("logic_oper");
            } else if(arithmeticOperators.contains(input[i])) { // Arithmetic Operator
                tokenList.add("arithmetic_oper");
            } else if(input[i].equals("=")) { //Assignment Operator
                tokenList.add("assgnmt_oper");
            } else if(input[i].equals("Voyage")) { //Voyage literal
                tokenList.add("voyage_token");
            } else if(input[i].equals("Reception")) { //Reception Literal
                tokenList.add("reception_token");
            } else if(input[i].equals("Transmission")) { //Transmission Literal
                tokenList.add("transmission_token");
            } else if(input[i].equals("Whirl")) { // Whirl
                tokenList.add("whirl_lit");
            } else if(input[i].equals("LaunchWhirl")) { //LaunchWhirl
                tokenList.add("launchwhirl_token");
            } else if(input[i].equals("Orbit")) {  //Orbit
                tokenList.add("orbit_token");
            } else if(input[i].equals("Navigate")) { //Navigate
                tokenList.add("navigate_token");
            } else if(input[i].equals("Propel")) { //Propel
                tokenList.add("propel_token");
            } else if (input[i].matches("[a-zA-Z0-9_]+")) { //Indentifier / ?Variable?
                if (input[i].length() == 1){
                    tokenList.add("id_" + input[i]);
                    if(symbolTable.contains(input[i]))
                        symbolTable.add("id_" + input[i]);
                } else {
                    tokenList.add("id_" + input[i]);
                    symbolTable.add("id_" + input[i]);
                }
            } else if(input[i].matches(whiteSpace)){ //White Space
            } else if(input[i].startsWith("\"")){ //String
                for(; !input[i].endsWith("\"") ;i++);
                tokenList.add("string");
            } else if(input[i].startsWith("/*")){  //Comment
                for(; !input[i].endsWith("*/") ;i++);
                tokenList.add("comment");
            }else { // Add more cases here
                tokenList.add("invalid_token");
            }
        }
        return tokenList.toArray(new String[0]);
    }
}