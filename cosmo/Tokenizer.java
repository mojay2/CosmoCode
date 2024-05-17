package cosmo;

import cosmo.lexeme.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Tokenizer {

    static HashSet<String> commentIndicators = new HashSet<>(Arrays.asList("/*", "*/"));
    static String[] arrayInput;
    static int counter = 0;
    static int totalErrors = 0;

    public void processInputs() {
        Scanner scanner = new Scanner(System.in);
        boolean continueProcessing = true;

        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("****************************************");
        System.out.println("*                                      *");
        System.out.println("*         Welcome to CosmoCode!        *");
        System.out.println("*                                      *");
        System.out.println("****************************************");
        System.out.println("Press any key to continue...");
        scanner.nextLine();

        while (continueProcessing) {
            System.out.print("\033[H\033[2J");
            System.out.flush();

            System.out.println("Please enter an input file to be read:");
            System.out.println("1. input1.txt");
            System.out.println("2. input2.txt");
            System.out.println("3. input3.txt");
            System.out.println("4. input4.txt");

            int choice = scanner.nextInt();

            String[] tokens = processInput("./input/input" + choice + ".txt",
                    "./output/tokenizer/output" + choice + ".txt",
                    "./output/symbol_table/output" + choice + ".txt");
            Parser parser = new Parser(tokens);
            parser.parse(choice); // Invoke the parsing process

            System.out.println("\n-------------------------------------------------\r\n");

            System.out.println("Would you like to continue? (yes/no)");
            String continueChoice = scanner.next();
            continueProcessing = continueChoice.equalsIgnoreCase("yes");
        }

        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("****************************************");
        System.out.println("*                                      *");
        System.out.println("*      Thanks for using CosmoCode!     *");
        System.out.println("*                                      *");
        System.out.println("****************************************");

        scanner.close();
    }

    public String[] processInput(String inputPath, String outputPath, String symbolTablePath) {
        String input = readInput(inputPath);
        System.out.println("\n=================================================\n");
        System.out.println("Input from " + inputPath);
        // System.out.println("Input from " + inputPath + ":\n" + input);

        // split into array
        arrayInput = input.trim().split("\\s+");
        // System.out.println("\nInput Converted to String Array:\n" +
        // Arrays.toString(arrayInput));

        // Get symbol table
        Map<String, String> symbolTable = SymbolTable.createSymbolTableMap();

        // pass to tokenizer
        String[] tokenized = tokenize(arrayInput, symbolTable);
        // System.out.println("\nTokenized Input:\n" + Arrays.toString(tokenized));

        // Print symbol table to console
        // System.out.println("\nSymbol Table:\n");
        // System.out.println(formatMap(symbolTable));

        // Write symbol table to file
        writeSymbolTableToFile(symbolTablePath, symbolTable);

        System.out.println("\nTotal number of tokenizing errors:\n" + totalErrors);
        System.out.println("\n-------------------------------------------------\r\n");
        writeOutputToFile(outputPath, tokenized);

        return tokenized;
    }

    public String readInput(String inputPath) {
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

    public void writeOutputToFile(String outputPath, String[] tokenizedInput) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            for (String token : tokenizedInput) {
                bw.write(token);
                bw.newLine();
            }
            // System.out.println("Tokenized input written to " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeSymbolTableToFile(String outputPath, Map<String, String> symbolTable) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("Symbol Table:\n");
            for (Map.Entry<String, String> entry : symbolTable.entrySet()) {
                writer.write(entry.getKey() + " : " + entry.getValue() + "\n");
            }
            // System.out.println("Symbol table written to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] tokenize(String[] input, Map<String, String> symbolTable) {
        ArrayList<String> tokenList = new ArrayList<>();
        counter = 0;
        totalErrors = 0;

        for (int i = 0; i < input.length; i++) {
            if (ReservedWords.createReservedWordsMap().containsKey(input[i])) { // Keyword
                tokenList.add(ReservedWords.createReservedWordsMap().get(input[i]));
            } else if (input[i].matches("-?\\d+")) { // Comet
                tokenList.add("cmt_" + input[i]);
            } else if (Separator.createSeparatorMap().containsKey(input[i])) { // Separator
                tokenList.add("sep_" + Separator.createSeparatorMap().get(input[i]));
            } else if (Comparison.createComparisonMap().containsKey(input[i])) { // Comparison Operator
                tokenList.add("comp_" + Comparison.createComparisonMap().get(input[i]));
            } else if (LogicalOperators.createLogicalMap().containsKey(input[i])) { // Logical Operator
                tokenList.add("logic_" + LogicalOperators.createLogicalMap().get(input[i]));
            } else if (ArithmeticOperators.createArithmeticMap().containsKey(input[i])) { // Arithmetic Operator
                tokenList.add("arith_" + ArithmeticOperators.createArithmeticMap().get(input[i]));
            } else if (input[i].matches("[a-zA-Z0-9_]+")) { // Identifier / ?Variable?
                if (isReservedWord(input[i])) {
                    counter++;
                    tokenList.add("invalid_token_" + counter);
                    totalErrors++;
                }
                tokenList.add("id_" + input[i]);
                if (!symbolTable.containsKey(input[i])) {
                    symbolTable.put(input[i], "id_" + input[i]);
                }
            } else if (input[i].matches("\\s+")) { // White Space
            } else if (input[i].startsWith("\"")) { // String
                String stringConst = "";
                for (; !input[i].endsWith("\""); i++) {
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

    private String formatMap(Map<String, String> map) {
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

    private Boolean isReservedWord(String string) {
        List<String> keysList = getKeysList(ReservedWords.createReservedWordsMap());
        for (int i = 0; i < keysList.size(); i++) {
            keysList.set(i, keysList.get(i).toLowerCase());
        }
        return keysList.contains(string);
    }

    private List<String> getKeysList(Map<String, String> map) {
        List<String> keysList = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            keysList.add(entry.getKey());
        }

        return keysList;
    }
}
