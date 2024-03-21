import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class ShiftReduceParser {
    // Global Variables
    static int z = 0, i = 0, j = 0, c = 0;

    // Arrays to hold input tokens, stack elements, and an action buffer
    static String[] a = new String[16];  // Input tokens
    static String[] ac = new String[20]; // Action buffer
    static String[] stk = new String[15]; // Stack elements

    // Initialize stk array elements to empty string
    static {
        Arrays.fill(stk, "");
    }

    /**
     * Checks if the stack contains any production rules to be reduced.
     * @param out PrintStream object to print output
     */
    static void check(PrintStream out) {
        // Flag to track if any reduction has occurred
        boolean reductionOccurred = false;
    
        // Copying string to be printed as action
        String action = "REDUCE TO ";
        String original = "";
    
        // Iterate over the input tokens
        for (z = 0; z < c; z++) {
            // checking for producing rule stringStmt->string or stringStmt->id
            if (stk[z].startsWith("\"") && stk[z].endsWith("\"")) {
                out.print(action);
                StringBuilder originalBuilder = new StringBuilder();
                for (int i = z; i < c; i++) {
                    originalBuilder.append(stk[i]);
                    if (i < c - 1) {
                        originalBuilder.append(" ");
                    }
                }
                original = originalBuilder.toString();
                stk[z] = "stringStmt";
                stk[z + 1] = "";
                // Set the flag to true to indicate a reduction occurred
                reductionOccurred = true;
                // printing action
                out.printf("%s -> %s\n", joinWithoutNull(stk), original);
                break; // Stop iterating as reduction is found
            }
        }
    
        // Check for other production rules if no reduction occurred yet
        if (!reductionOccurred) {
            // Check for idStmt production
            for (z = 0; z < c; z++) {
                // checking for producing rule stringStmt->string or stringStmt->id
                if (stk[z].startsWith("id_")) {
                    out.print(action);
                    StringBuilder originalBuilder = new StringBuilder();
                    for (int i = z; i < c; i++) {
                        originalBuilder.append(stk[i]);
                        if (i < c - 1) {
                            originalBuilder.append(" ");
                        }
                    }
                    original = originalBuilder.toString();
                    stk[z] = "idStmt";
                    stk[z + 1] = "";
                    // Set the flag to true to indicate a reduction occurred
                    reductionOccurred = true;
                    // printing action
                    out.printf("%s -> %s\n", joinWithoutNull(stk), original);
                    break; // Stop iterating as reduction is found
                }
            }
        }
    
        // Check for ioStmt production
        if (!reductionOccurred) {
            for (z = 0; z < c - 3; z++) {
                // checking for producing rule sep_op_par stringStmt sep_cl_par sep_semicolon
                if (stk[z].equals("sep_op_par") && (stk[z + 1].equals("stringStmt") || stk[z + 1].equals("idStmt")) &&
                    stk[z + 2].equals("sep_cl_par") && stk[z + 3].equals("sep_semicolon")) {
                    out.print(action);
                    StringBuilder originalBuilder = new StringBuilder();
                    for (int i = z; i < c; i++) {
                        originalBuilder.append(stk[i]);
                        if (i < c - 1) {
                            originalBuilder.append(" ");
                        }
                    }
                    original = originalBuilder.toString();
                    stk[z] = "ioStmt";
                    stk[z + 1] = "";
                    stk[z + 2] = "";
                    stk[z + 3] = "";
                    // Set the flag to true to indicate a reduction occurred
                    reductionOccurred = true;
                    // printing action
                    out.printf("%s -> %s\n", joinWithoutNull(stk), original);
                    break; // Stop iterating as reduction is found
                }
            }
        }
    
        // Check for transmissionStmt production if no reduction occurred yet
        if (!reductionOccurred) {
            for (z = 0; z < c - 1; z++) {
                // checking for producing rule transmission_token ioStmt
                if (stk[z].equals("transmission_token") && stk[z + 1].equals("ioStmt")) {
                    out.print(action);
                    StringBuilder originalBuilder = new StringBuilder();
                    for (int i = z; i < c; i++) {
                        originalBuilder.append(stk[i]);
                        if (i < c - 1) {
                            originalBuilder.append(" ");
                        }
                    }
                    original = originalBuilder.toString();
                    stk[z] = "transmissionStmt";
                    stk[z + 1] = "";
                    // Set the flag to true to indicate a reduction occurred
                    reductionOccurred = true;
                    // printing action
                    out.printf("%s -> %s\n", joinWithoutNull(stk), original);
                    break; // Stop iterating as reduction is found
                }
            }
        }
    
        // Check for receptionStmt production if no reduction occurred yet
        if (!reductionOccurred) {
            for (z = 0; z < c - 3; z++) {
                // checking for producing rule idStmt arith_assign reception_token ioStmt
                if (stk[z].equals("idStmt") && stk[z + 1].equals("arith_assign") && stk[z + 2].equals("reception_token") && stk[z + 3].equals("ioStmt")) {
                    out.print(action);
                    StringBuilder originalBuilder = new StringBuilder();
                    for (int i = z; i < c; i++) {
                        originalBuilder.append(stk[i]);
                        if (i < c - 1) {
                            originalBuilder.append(" ");
                        }
                    }
                    original = originalBuilder.toString();
                    stk[z] = "receptionStmt";
                    stk[z + 1] = "";
                    stk[z + 2] = "";
                    stk[z + 3] = "";
                    // Set the flag to true to indicate a reduction occurred
                    reductionOccurred = true;
                    // printing action
                    out.printf("%s -> %s\n", joinWithoutNull(stk), original);
                    break; // Stop iterating as reduction is found
                }
            }
        }
    
        // If no reduction occurred, skip printing the "REDUCE TO E ->" message
        if (!reductionOccurred) {
            return;
        }
    }
    
    /**
     * Joins array elements without null values.
     * @param arr The input array
     * @return The joined string
     */
    static String joinWithoutNull(String[] arr) {
        StringBuilder result = new StringBuilder();
        for (String str : arr) {
            if (str != null && !str.isEmpty()) {
                result.append(str + " ");
            }
        }
        return result.toString().trim(); // Trim to remove trailing space
    }

    /**
     * Main method to parse input string using shift-reduce parsing technique.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Input string
        String input = "id_x arith_assign reception_token sep_op_par \"hello world\" sep_cl_par sep_semicolon transmission_token sep_op_par \"this is another string\" sep_cl_par sep_semicolon";
        
        // Split input string into tokens
        String[] words = input.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        c = words.length;  // Set the length of the input array

        // Define the output file path
        String outputFile = "ParsedOutput.txt";

        try {
            // Create a new PrintStream to write to the output file
            PrintStream outputStream = new PrintStream(new FileOutputStream(outputFile));

            // Redirect System.out to the output file
            System.setOut(outputStream);

            // "SHIFT" is copied to act to be printed
            String action = "SHIFT";

            // Print the initial values of stack and input
            System.out.printf("Input String: %s\n\n", input);
            System.out.printf("%-130s %-100s %-70s\n", "Action", "Stack", "Remaining Input");
            System.out.println("-".repeat(270));

            // Iterate over the input tokens
            for (i = 0; j < c; i++, j++) {
                // Print action
                System.out.printf("%-130s %-100s %-70s\n", action, joinWithoutNull(stk), joinWithoutNull(Arrays.copyOfRange(words, j, c)));

                // Push into stack
                stk[i] = words[j];
                stk[i + 1] = "";

                // Check stack for production rules
                check(outputStream);
            }

            // Check for any remaining valid production rules
            check(outputStream);

            // Check if the stack contains only valid tokens
            boolean isValidInput = true;
            for (int k = 0; k < c; k++) {
                if (!stk[k].equals("transmissionStmt") && !stk[k].equals("receptionStmt") && !stk[k].isEmpty()) {
                    isValidInput = false;
                    break;
                }
            }

            // Print output based on input validity
            if (isValidInput) {
                System.out.println("Accept");
            } else {
                System.out.println("Reject");
            }

            // Close the output stream
            outputStream.close();

            // Print message indicating that the output has been written to the file
            System.out.printf("Output has been written to %s\n", outputFile);
        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
        }
    }
}