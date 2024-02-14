package CosmoCode;
import java.util.*;

public class Tokenizer{

    static String whiteSpace = "(([\\s] *) | ((?s). *[\\n\\r].) *)";
    static Set<String> separators = new HashSet<>(Arrays.asList("(", ")", "{", "}", ",", ";"));
    static Set<String> comparisonOperators = new HashSet<>(Arrays.asList("==", ">=", "<=", "!=", ">", "<"));
    static Set<String> logicalOperators = new HashSet<>(Arrays.asList("&&", "||"));
    static Set<String> arithmeticOperators = new HashSet<>(Arrays.asList("+", "-", "*", "/", "++", "--"));
    static Set<String> commentIndicators = new HashSet<>(Arrays.asList("/*", "*/"));

    static String[] arrayInput;

    public static void main(String [] args){        
        String input = " Comet var1 = reception ( \"Enter A Number\" ) ;" +
        " Comet var2 = 10 " +
        " Whirl ( var2 <= var1 ) { " +
        "    var2 -- ; " +
        " } ; " +
        " Launch { " +
        "    var2 -- ; " +
        " } whirl ( var2 != 0 || var2 != 5 ) ; " +
        " Orbit ( var1 > var2 ) { " +
        "    transmission ( \"Greater!\" ) ; " +
        " } Navigate ( var1 < var2 ) { " +
        "    transmission ( \"Lesser!\" ) ; " +
        " } Propel { " +
        "    transmission ( \"Equal!\" ) ; " +
        " } " +
        " ; " +
        " /* This is a comment */ " +
        " $_ThisShouldBeInvalid " //Invalid token for testing
        ;

        System.out.println("=================================================");
        System.out.println("Input:\n" + input);

        // split into array
        arrayInput = input.trim().split("\\s+");
        System.out.println("\nInput Coverted to String Array:\n" + Arrays.toString(arrayInput));

        // pass to tokenizer
        String[] tokenized = tokenize(arrayInput);
        System.out.println("\nTokenized Input:\n" + Arrays.toString(tokenized));
        System.out.println("=================================================");
    }

    public static String[] tokenize(String[] input) {
        ArrayList<String> tokenList = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i].equals("Comet")) { // Comet Keyword
                tokenList.add("comet_lit");
            } else if(input[i].matches("-?\\d+")) {  // Comet
                tokenList.add("comet");
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
                tokenList.add("voyage_lit");
            } else if(input[i].equals("Reception")) { //Reception Literal
                tokenList.add("reception_lit");
            } else if(input[i].equals("Transmission")) { //Transmission Literal
                tokenList.add("transmission_lit");
            } else if(input[i].equals("Whirl")) { // Whirl
                tokenList.add("whirl_lit");
            } else if(input[i].equals("LaunchWhirl")) { //LaunchWhirl
                tokenList.add("launchwhirl_lit");
            } else if(input[i].equals("Orbit")) {  //Orbit
                tokenList.add("orbit_lit");
            } else if(input[i].equals("Navigate")) { //Navigate
                tokenList.add("navigate_lit");
            } else if(input[i].equals("Propel")) { //Propel
                tokenList.add("propel_lit");
            } else if (input[i].matches("[a-zA-Z0-9_]+")) { //Indentifier / ?Variable?
                if (input[i].length() == 1)
                    tokenList.add("identifier");
                else 
                    tokenList.add("identifier");
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