package cosmo;

import cosmo.grammar.ProductionChecker;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class Parser {
    private String[] tokens;

    public Parser(String[] tokens) {
        this.tokens = tokens;
    }

    int tokenLength = 0;

    void check(String[] stk, List<String[]> dataTable) {
        ProductionChecker.checkProductions(stk, dataTable);
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
        String filePath = "./output/parser/output" + fileNumber + ".csv";
    
        // Define a String array to hold the stk and remainingInput
        String[] stk = new String[tokenLength];
        Arrays.fill(stk, ""); // Initialize stk with empty strings
    
        // Define an ArrayList to hold data
        List<String[]> dataTable = new ArrayList<>();
    
        // Add data dynamically
        dataTable.add(new String[]{"Input String", tokensAsString, ""});
        dataTable.add(new String[]{"", "", ""});
        dataTable.add(new String[]{"Action", "Stack", "Remaining Input"});
    
        // Iterate over the input tokens
        int i = 0;
        String action;
        for (int j = 0; j <= tokenLength; j++) {
            // Set the action to "SHIFT" initially
            action = "SHIFT";
    
            // Print action              
            dataTable.add(new String[]{action, joinWithoutNull(stk), joinWithoutNull(Arrays.copyOfRange(tokens, j, tokenLength))});
    
            if (j < tokenLength) {
                stk[i] = tokens[j];
            }
    
            // Increment stack index
            i++;
    
            // Check stack for production rules
            check(stk, dataTable);
        }
    
        // Perform one last check for remaining productions
        check(stk, dataTable);

        // Check if the stack contains only valid tokens
        boolean isValidInput = true;
        for (int k = 0; k < tokenLength; k++) {
            if (!stk[k].equals("expr")) { // TO BE EDITED
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
            System.out.println(list);
            if (list.get(0).equals("arithExp") && list.size() == 1) {
                isValidInput = true;
            }
        }
        
        // Print output based on input validity
        if (isValidInput) {
            System.out.println("Accept");
            dataTable.add(new String[]{"ACCEPT", "", ""});
        } else {
            System.out.println("Reject");
            dataTable.add(new String[]{"REJECT", "", ""});
        }
        
         // Write data to CSV file
         writeOutputToFile(filePath, dataTable);
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
            System.out.println("CSV file has been created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<String> checkProds(ArrayList<String> tokenList){
        String action = "REDUCE TO -> ";
        tokenList.add(0, "");
        tokenList.add(0, "");
        tokenList.add(0, "");
        tokenList.add(0, "$");
        tokenList.add(tokenList.size(), "%");
        for (int i = tokenList.size() - 1; i >= 0; i--) {
            if(tokenList.get(i).equals("$"))
                break;
            if(tokenList.get(i).equals(""))
                continue;
            String token = tokenList.get(i);
            
            //<termPrime> -> arith_mult <factor> <termPrime> | arith_div <factor> <termPrime>
            if(token.equals("termPrime") && 
            tokenList.get(i-1).equals("factor") && 
            (tokenList.get(i-2).equals("arith_mult") || tokenList.get(i-2).equals("arith_div"))){
                tokenList.set(i-2, "termPrime");
                tokenList.set(i, "");
                tokenList.set(i-1, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // <arithExpPrime> -> arith_plus <term> <arithExpPrime> | arith_minus <term> <arithExpPrime>
            else if(token.equals("arithExpPrime") && 
            tokenList.get(i-1).equals("term") && 
            (tokenList.get(i-2).equals("arith_plus") || tokenList.get(i-2).equals("arith_minus"))){
                tokenList.set(i-2, "arithExpPrime");
                tokenList.set(i, "");
                tokenList.set(i-1, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            } 
            // <term> -> <factor> <termPrime> 
            else if(token.equals("termPrime") && 
            tokenList.get(i-1).equals("factor") && 
            (!tokenList.get(i-2).equals("arith_mult") && !tokenList.get(i-2).equals("arith_div"))){
                tokenList.set(i-1, "term");
                tokenList.set(i, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // when term is rightmost token, add <arithExpPrime> -> e
            else if(token.equals("term") && 
            !tokenList.get(i-1).equals("factor") && 
            tokenList.get(i+1).equals("")){
                tokenList.set(i+1, "arithExpPrime");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            }
            // <arithExp> -> <term><arithExpPrime>
            else if(token.equals("arithExpPrime") && 
            tokenList.get(i-1).equals("term") && 
            (!tokenList.get(i-2).equals("arith_plus") || !tokenList.get(i-2).equals("arith_minus"))){
                tokenList.set(i-1, "arithExp");
                tokenList.set(i, "");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            } 
            // when factor is rightmost token, derive termPrime -> e
            else if(token.equals("factor") && 
            tokenList.get(i+1).equals("%")){
                tokenList.add(i+1, "termPrime");
                System.out.print(action);
                printValidTokens(tokenList);
                i = tokenList.size() - 1;
            } else if(token.equals("factor") && 
            tokenList.get(i+1).equals("%")){
                tokenList.add(i+1, "termPrime");
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
     * @param list list of tokens
     * @return list of valid tokens
     */
    static ArrayList<String> retrieveValidTokens(ArrayList<String> list) {
        ArrayList<String> result = new ArrayList<>();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String element = iterator.next();
            if (!element.isEmpty() && !element.equals("$") && !element.equals("%")) {
                result.add(element);            }
        }
        return result;
    }
}
