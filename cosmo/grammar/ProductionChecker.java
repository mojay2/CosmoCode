package cosmo.grammar;

import cosmo.ParseTreeNode;
import java.util.*;

public class ProductionChecker {

    public static void checkProductions(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        checkStringProduction(stk, dataTable, root);
        checkIdentifierProduction(stk, dataTable, root);
        checkCometLiteralProduction(stk, dataTable, root);
        checkDeclarationProduction(stk, dataTable, root);
        checkAssignmentProduction(stk, dataTable, root);
        checkIoStatementProduction(stk, dataTable, root);
        checkTransmissionProduction(stk, dataTable, root);
        checkReceptionProduction(stk, dataTable, root);
        checkRelationalOperatorProduction(stk, dataTable, root);
        checkLogicalOperatorProduction(stk, dataTable, root);
        checkLogicalExpressionProduction(stk, dataTable, root);
        checkRelationalExpressionProduction(stk, dataTable, root);
        checkConditionalExpressionProduction(stk, dataTable, root);
        checkExpressionProduction(stk, dataTable, root);
        checkStatementProduction(stk, dataTable, root);
        checkTermProduction(stk, dataTable);
        checkTermPrimeProduction(stk, dataTable);
        checkTerm2Production(stk, dataTable);
        checkTermPrime2Production(stk, dataTable);
        // checkFactorProduction(stk, dataTable);
        checkTermPrime3Production(stk, dataTable);
        checkTerm3Production(stk, dataTable);
        checkArithmeticExpPrimeProduction(stk, dataTable);
        checkArithmeticExpressionProduction(stk, dataTable);
        checkNavigateProduction(stk, dataTable, root);
        checkPropelProduction(stk, dataTable, root);
        checkOrbitProduction(stk, dataTable, root);
        checkOrbit2Production(stk, dataTable, root);
        checkOrbit3Production(stk, dataTable, root);
        checkWhirlLoopProduction(stk, dataTable, root);
        checkLaunchWhirlLoopProduction(stk, dataTable, root);
    }

    private static void checkStringProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule string -> string or string -> id
            if (stk[z].startsWith("\"") && stk[z].endsWith("\"")) {
                stk[z] = "string";
                stk[z + 1] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("string");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);
            }
        }
    }

    private static void checkIdentifierProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].startsWith("id_")) {
                // Perform reduction for identifier
                stk[z] = "identifier";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("identifier");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);
                return;
            } 
        }
    }

    private static void checkCometLiteralProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].startsWith("comet_") && !stk[z].startsWith("comet_token")) {
                // Perform reduction for identifier
                stk[z] = "comet_literal";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                System.out.println("AHHH: ");
                root.logTree();
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("comet_literal");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);
            } 
        }
    }

    private static void checkDeclarationProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule decStmt -> comet_token id arith_assign
            if (stk[z].equals("comet_token") &&
                    stk[z + 1].equals("assignStmt")) {
                // Perform reduction for identifier
                stk[z] = "decStmt";
                stk[z + 1] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("decStmt");
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkAssignmentProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("assign_variable") &&
                    stk[z + 1].equals("arith_assign") &&
                    (stk[z + 2].equals("identifier") || stk[z + 2].equals("comet_literal")
                            || stk[z + 2].equals("arithmeticExp"))
                    &&
                    stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "assignStmt";
                stk[z+1] = "";
                stk[z+2] = "";
                stk[z+3] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("assignStmt");
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkIoStatementProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("sep_op_par") && (stk[z + 1].equals("string") || stk[z + 1].equals("identifier")) &&
            stk[z + 2].equals("sep_cl_par") && stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "ioStmt";
                stk[z+1] = "";
                stk[z+2] = "";
                stk[z+3] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("ioStmt");
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkTransmissionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("transmission_token") && stk[z + 1].equals("ioStmt")) {
                // Perform reduction for identifier
                stk[z] = "transmissionStmt";
                stk[z+1] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("transmissionStmt");
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkReceptionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("identifier") && stk[z + 1].equals("arith_assign") && stk[z + 2].equals("reception_token") && stk[z + 3].equals("ioStmt")) {
                // Perform reduction for identifier
                stk[z] = "receptionStmt";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode previousNode5 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("receptionStmt");
                reducedNode.addChild(previousNode5);
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                root.addChild(reducedNode);
                root.addChild(previousNode);

                return;
            }
        }
    }

    private static void checkRelationalOperatorProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("comp_eq") || 
            stk[z].equals("comp_not") || 
            stk[z].equals("comp_great_eq") || 
            stk[z].equals("comp_less_eq") || 
            stk[z].equals("comp_great") || 
            stk[z].equals("comp_less")) {
                // Perform reduction for identifier
                stk[z] = "relationalOp";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("relationalOp");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkLogicalOperatorProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("logic_and") || 
            stk[z].equals("logic_or")) {
                // Perform reduction for identifier
                stk[z] = "logicalOp";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("logicalOp");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);
                return;
            }
        }
    }

    private static void checkLogicalExpressionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("relationalExp") && stk[z + 1].equals("logicalOp") && stk[z + 2].equals("relationalExp")) {
                // Perform reduction for identifier
                stk[z] = "logicalExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("logicalExp");
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkRelationalExpressionProduction(String[] stk, List<String[]> dataTable,
            ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if ((stk[z].equals("identifier") || stk[z].equals("comet_literal")) && stk[z + 1].equals("relationalOp") && (stk[z+2].equals("identifier") || stk[z+2].equals("comet_literal"))) {
                // Perform reduction for identifier
                stk[z] = "relationalExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("relationalExp");
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                root.addChild(reducedNode);
                root.addChild(previousNode);

                return;
            }
        }
    }

    private static void checkConditionalExpressionProduction(String[] stk, List<String[]> dataTable,
            ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if ((stk[z].equals("sep_op_par") && stk[z + 1].equals("relationalExp") && stk[z+2].equals("sep_cl_par")) || (stk[z].equals("sep_op_par") && stk[z + 1].equals("logicalExp") && stk[z+2].equals("sep_cl_par"))) {
                // Perform reduction for identifier
                stk[z] = "conditionalExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("conditionalExp");
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                root.addChild(reducedNode);
                root.addChild(previousNode);

                return;
            }
        }
    }

    private static void checkExpressionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("receptionStmt") || 
            stk[z].equals("transmissionStmt") || 
            stk[z].equals("arithmeticExp")) {
                // Perform reduction for identifier
                stk[z] = "expr";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("expr");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkStatementProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        // Iterate through the stack elements
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check if the current sequence matches the production rule
            if (stk[z].equals("sep_op_brac") && stk[z + 1].equals("expr") && stk[z + 2].equals("sep_cl_brac")) {
                // Perform reduction for the production rule
                stk[z] = "stmt";
                stk[z + 1] = "";
                stk[z + 2] = "";
    
                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("stmt");
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }   

    private static void checkTermProduction(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (z == 0 && (stk[z].equals("identifier") || stk[z].equals("comet_literal")) && (stk[z+1].equals("arith_plus") || stk[z+1].equals("arith_minus"))) {
                // Perform reduction for identifier
                stk[z] = "term";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkTermPrimeProduction(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (z > 1 && (stk[z].equals("identifier") || stk[z].equals("comet_literal")) && 
            (stk[z-1].equals("arith_mult") || stk[z-1].equals("arith_div")) && 
            (stk[z+1].equals("arith_plus") || stk[z+1].equals("arith_minus"))) {
                // Perform reduction for identifier
                stk[z-1] = "termPrime";
                stk[z-0] = "";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkTerm2Production(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if ((stk[z].equals("identifier") || stk[z].equals("comet_literal")) && (stk[z+1].equals("arith_plus")||stk[z+1].equals("arith_minus"))) {
                // Perform reduction for identifier
                stk[z] = "term";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkTermPrime2Production(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (z > 0 && (stk[z].equals("identifier") || stk[z].equals("comet_literal")) && stk[z - 1].equals("arith_mult") && z == stk.length - 1) {
                // Perform reduction for identifier
                stk[z + 1] = "termPrime";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    /* 
    private static void checkFactorProduction(String[] stk, List<String[]> dataTable) {
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("identifier") || stk[z].equals("comet_literal")) {
                // Perform reduction for identifier
                stk[z] = "factor";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }*/

    private static void checkTermPrime3Production(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("arith_mult") && (stk[z+1].equals("identifier") || stk[z+1].equals("comet_literal")) && stk[z + 2].equals("termPrime")) {
                // Perform reduction for identifier
                stk[z] = "termPrime";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("arith_div") && (stk[z+1].equals("identifier") || stk[z+1].equals("comet_literal")) && stk[z + 2].equals("termPrime")) {
                // Perform reduction for identifier
                stk[z] = "termPrime";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkTerm3Production(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if ((stk[z].equals("identifier") || stk[z].equals("comet_literal")) && (stk[z + 1].equals("termPrime"))) {
                // Perform reduction for identifier
                stk[z] = "term";
                stk[z + 1] = "";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkArithmeticExpPrimeProduction(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("arith_plus") && (stk[z + 1].equals("term") && stk[z + 2].equals("arithExpPrime")) ||
            stk[z].equals("arith_minus") && (stk[z + 1].equals("term") && stk[z + 2].equals("arithExpPrime"))) {
                // Perform reduction for identifier
                stk[z] = "arithExpPrime";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkArithmeticExpressionProduction(String[] stk, List<String[]> dataTable) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("term") && (stk[z + 1].equals("arithExpPrime"))) {
                // Perform reduction for identifier
                stk[z] = "arithExp";
                stk[z + 1] = "";
                
                // Add reduction to dataTable
                dataTable.add(new String[]{"REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", ""});
                return;
            }
        }
    }

    private static void checkNavigateProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("navigate_token") && 
            stk[z + 1].equals("conditionalExp") && 
            stk[z + 2].equals("stmt")) {
                // Perform reduction for identifier
                stk[z] = "navigateStmt";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("navigateStmt");
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkPropelProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("propel_token") && 
            stk[z + 1].equals("stmt")) {
                // Perform reduction for identifier
                stk[z] = "propelStmt";
                stk[z + 1] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("propelStmt");
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                root.addChild(reducedNode);
                root.addChild(previousNode);

                return;
            }
        }
    }

    private static void checkOrbitProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("orbit_token") && 
            stk[z + 1].equals("conditionalExp") && 
            stk[z + 2].equals("stmt") && 
            stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "orbitStmt";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("orbitStmt");
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkOrbit2Production(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 4; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("orbit_token") && 
            stk[z + 1].equals("conditionalExp") && 
            stk[z + 2].equals("stmt") && 
            stk[z + 3].equals("propelStmt") && 
            stk[z + 4].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "orbitStmt";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";
                stk[z + 4] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode previousNode5 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("orbitStmt");
                reducedNode.addChild(previousNode5);
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkOrbit3Production(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 5; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("orbit_token") && 
            stk[z + 1].equals("conditionalExp") && 
            stk[z + 2].equals("stmt") && 
            stk[z + 3].equals("navigateStmt") && 
            stk[z + 4].equals("propelStmt") && 
            stk[z + 5].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "orbitStmt";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";
                stk[z + 4] = "";
                stk[z + 5] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode previousNode5 = root.popChild();
                ParseTreeNode previousNode6 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("orbitStmt");
                reducedNode.addChild(previousNode6);
                reducedNode.addChild(previousNode5);
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkWhirlLoopProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("whirl_token") && 
            stk[z + 1].equals("conditionalExp") && 
            stk[z + 2].equals("stmt") && 
            stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "whirlLoop";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("whirlLoop");
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);
                return;
            }
        }
    }

    private static void checkLaunchWhirlLoopProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 4; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("launchwhirl_token") && 
            stk[z + 1].equals("stmt") && 
            stk[z + 2].equals("whirl_token") && 
            stk[z + 3].equals("conditionalExp") &&
            stk[z + 4].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "launchWhirlLoop";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";
                stk[z + 4] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode previousNode5 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("launchWhirlLoop");
                reducedNode.addChild(previousNode5);
                reducedNode.addChild(previousNode4);
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
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

    private static String constructOriginalString(String[] stk, int startIndex) {
        StringBuilder originalBuilder = new StringBuilder();

        // Construct original string representation of the stack
        for (int i = startIndex; i < stk.length; i++) {
            originalBuilder.append(stk[i]);
            if (i < stk.length - 1) {
                originalBuilder.append(" ");
            }
        }
        return originalBuilder.toString();
    }

    static void removeEmptyValuesInBetween(String[] array) {
        boolean foundNonEmpty = false;
        int writeIndex = 0;

        for (int readIndex = 0; readIndex < array.length; readIndex++) {
            if (!array[readIndex].isEmpty()) {
                if (foundNonEmpty && writeIndex != readIndex) {
                    array[writeIndex++] = array[readIndex];
                    array[readIndex] = "";
                } else {
                    writeIndex++;
                }
                foundNonEmpty = true;
            }
        }
    }
}