package cosmo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private String[] tokens;

    public Parser(String[] tokens) {
        this.tokens = tokens;
    }

    int tokenLength = 0;

    void check(String[] stk, List<String[]> dataTable) {
        // Check stringStmt production
        for (int z = 0; z < stk.length; z++) {
            // Reset original for each iteration
            StringBuilder originalBuilder = new StringBuilder();
    
            // Construct original string representation of the stack
            for (int i = z; i < stk.length; i++) {
                originalBuilder.append(stk[i]);
                if (i < stk.length - 1) {
                    originalBuilder.append(" ");
                }
            }
            String original = originalBuilder.toString();
    
            // Check for producing rule stringStmt -> string or stringStmt -> id
            if (stk[z].startsWith("\"") && stk[z].endsWith("\"")) {
                // Perform reduction for stringStmt
                stk[z] = "stringStmt";
                stk[z + 1] = "";
    
                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }

        // Check for idStmt production
        for (int z = 0; z < stk.length; z++) {
            // Reset original for each iteration
            StringBuilder originalBuilder = new StringBuilder();
                
            // Construct original string representation of the stack
            for (int i = z; i < stk.length; i++) {
                originalBuilder.append(stk[i]);
                if (i < stk.length - 1) {
                    originalBuilder.append(" ");
                }
            }
            String original = originalBuilder.toString();

            // Check for producing rule identifier -> id
            if (stk[z].startsWith("id_")) {
                // Perform reduction for stringStmt
                stk[z] = "idStmt";
                stk[z + 1] = "";
    
                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
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
    
        // Check for remaining productions
        check(stk, dataTable);
    
        // Write data to CSV file
        writeOutputToFile(filePath, dataTable);
    }
    
    static String joinWithoutNull(String[] arr) {
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
}
