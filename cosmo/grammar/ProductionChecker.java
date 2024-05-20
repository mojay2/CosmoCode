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
        checkTransmissionProduction(stk, dataTable, root);
        checkReceptionProduction(stk, dataTable, root);
        checkRelationalOperatorProduction(stk, dataTable, root);
        checkLogicalOperatorProduction(stk, dataTable, root);
        checkLogicalExpressionProduction(stk, dataTable, root);
        checkRelationalExpressionProduction(stk, dataTable, root);
        checkConditionalExpressionProduction(stk, dataTable, root);
        checkExpressionProduction(stk, dataTable, root);
        checkStatementProduction(stk, dataTable, root);
        checkFactorProduction(stk, dataTable, root);
        checkNavigateProduction(stk, dataTable, root);
        checkPropelProduction(stk, dataTable, root);
        checkOrbitProduction(stk, dataTable, root);
        checkOrbit2Production(stk, dataTable, root);
        checkOrbit3Production(stk, dataTable, root);
        checkWhirlLoopProduction(stk, dataTable, root);
        checkLaunchWhirlLoopProduction(stk, dataTable, root);
        checkArithmeticExpressionProduction(stk, dataTable, root);
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

            // Check for producing rule comet -> number
            if (stk[z].startsWith("cmt_")) {
                // Perform reduction for comet
                stk[z] = "comet_literal";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
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
        for (int z = 0; z < stk.length - 4; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule decStmt -> comet_token id arith_assign
            if (stk[z].equals("comet_token") &&
                    stk[z + 1].equals("identifier") &&
                    stk[z + 2].equals("arith_assign") &&
                    (stk[z + 3].equals("identifier") || stk[z + 3].equals("comet_literal")
                            || stk[z + 3].equals("arithExp"))
                    &&
                    stk[z + 4].equals("sep_semicolon")) {

                // Perform reduction for decStmt
                stk[z] = "decStmt";
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
                ParseTreeNode reducedNode = new ParseTreeNode("decStmt");
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

    private static void checkAssignmentProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule assignment -> id | comet_literal | arihtmetic
            if (stk[z].equals("identifier") &&
                    stk[z + 1].equals("arith_assign") &&
                    (stk[z + 2].equals("identifier") || stk[z + 2].equals("comet_literal")
                            || stk[z + 2].equals("arithExp"))
                    &&
                    stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for assignment
                stk[z] = "assignStmt";
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

    private static void checkTransmissionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 1; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule transmission -> transmission ( string | id )
            if (stk[z].equals("transmission_token") && stk[z + 1].equals("sep_op_par")
                    && (stk[z + 2].equals("string") || stk[z + 2].equals("identifier")) &&
                    stk[z + 3].equals("sep_cl_par") && stk[z + 4].equals("sep_semicolon")) {
                // Perform reduction for transmission
                stk[z] = "transmissionStmt";
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
                ParseTreeNode reducedNode = new ParseTreeNode("transmissionStmt");
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

    private static void checkReceptionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if (stk[z].equals("identifier") && stk[z + 1].equals("arith_assign") && stk[z + 2].equals("reception_token")
                    && stk[z + 3].equals("sep_op_par") && stk[z + 4].equals("string") &&
                    stk[z + 5].equals("sep_cl_par") && stk[z + 6].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "receptionStmt";
                stk[z + 1] = "";
                stk[z + 2] = "";
                stk[z + 3] = "";
                stk[z + 4] = "";
                stk[z + 5] = "";
                stk[z + 6] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode previousNode4 = root.popChild();
                ParseTreeNode previousNode5 = root.popChild();
                ParseTreeNode previousNode6 = root.popChild();
                ParseTreeNode previousNode7 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("receptionStmt");
                reducedNode.addChild(previousNode7);
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

        int z = 0;
        while (z < stk.length - 1) {
            String original = constructOriginalString(stk, z);

            // Check if the current sequence matches the production rule
            // <logicalExp> ->
            // <relationalExp> <logicalOp> <relationalExp> ... <logicalOp> <relationalExp>
            if (stk[z].equals("relationalExp")) {
                int tempZ = z + 1;
                while (tempZ < stk.length && (stk[tempZ].equals("logicalOp") || stk[tempZ].equals("relationalExp"))) {
                    tempZ++;
                }

                if (tempZ - z > 2 && (tempZ - z) % 2 == 1) {
                    // Perform reduction for identifier
                    for (int i = z; i < tempZ; i++) {
                        stk[i] = i == z ? "logicalExp" : "";
                    }

                    // Add reduction to dataTable
                    dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " +
                            original, "", "" });

                    // Update Parse Tree
                    ParseTreeNode closePar = root.popChild();
                    ParseTreeNode reducedNode = new ParseTreeNode("logicalExp");
                    for (int i = tempZ - 1; i >= z; i--) {
                        reducedNode.addChild(root.popChild());
                    }
                    root.addChild(reducedNode);
                    root.addChild(closePar);
                    z = tempZ - 1;
                }
            }
            z++;
        }
    }

    private static void checkRelationalExpressionProduction(String[] stk, List<String[]> dataTable,
            ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule identifier -> id | comet_literal
            if ((stk[z].equals("identifier") || stk[z].equals("comet_literal")) && stk[z + 1].equals("relationalOp")
                    && (stk[z + 2].equals("identifier") || stk[z + 2].equals("comet_literal"))) {
                // Perform reduction for identifier
                stk[z] = "relationalExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode idOrComet = root.popChild();
                ParseTreeNode relationalOp = root.popChild();
                ParseTreeNode idOrComet2 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("relationalExp");
                reducedNode.addChild(idOrComet2);
                reducedNode.addChild(relationalOp);
                reducedNode.addChild(idOrComet);
                root.addChild(reducedNode);
                return;
            }
        }
    }

    private static void checkConditionalExpressionProduction(String[] stk, List<String[]> dataTable,
            ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule conditional -> ( relationalExp ) | ( logicalExp )
            if ((stk[z].equals("sep_op_par") && stk[z + 1].equals("relationalExp") && stk[z + 2].equals("sep_cl_par"))
                    || (stk[z].equals("sep_op_par") && stk[z + 1].equals("logicalExp")
                            && stk[z + 2].equals("sep_cl_par"))) {
                // Perform reduction for identifier
                stk[z] = "conditionalExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode closePar = root.popChild();
                ParseTreeNode relationalOrLogical = root.popChild();
                ParseTreeNode openPar = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("conditionalExp");
                reducedNode.addChild(openPar);
                reducedNode.addChild(relationalOrLogical);
                reducedNode.addChild(closePar);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkExpressionProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule expression -> transmission | reception | arithmetic
            // | assignment | declaration
            if (stk[z].equals("receptionStmt") ||
                    stk[z].equals("transmissionStmt") ||
                    (stk[z].equals("arithExp") && !stk[z + 1].equals("arith_plus") && !stk[z + 1].equals("arith_minus")
                            && !stk[z + 1].equals("arith_div") && !stk[z + 1].equals("arith_mult"))
                    ||
                    stk[z].equals("assignStmt") || stk[z].equals("decStmt") || stk[z].equals("orbitStmt1")
                    || stk[z].equals("orbitStmt2") || stk[z].equals("orbitStmt3") || stk[z].equals("whirlLoop")
                    || stk[z].equals("launchWhirlLoop")) {
                // Perform reduction for expression
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
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check if the current sequence matches the production rule <stmt> ->
            // sep_op_brac (<expr>)* sep_cl_brac
            if (stk[z].equals("sep_op_brac")) {
                int exprCount = 0;
                int i = z + 1;

                // Count the number of <expr> tokens
                while (i < stk.length && stk[i].equals("expr")) {
                    exprCount++;
                    i++;
                }

                if (exprCount > 0 && i < stk.length && stk[i].equals("sep_cl_brac")) {
                    // Perform reduction for the production rule
                    stk[z] = "stmt";
                    for (int j = z + 1; j <= i; j++) {
                        stk[j] = "";
                    }

                    // Add reduction to dataTable
                    dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                    // Updating Parse Tree
                    ParseTreeNode reducedNode = new ParseTreeNode("stmt");
                    ParseTreeNode closingBracketNode = root.popChild(); // sep_cl_brac

                    // Adding expr nodes
                    List<ParseTreeNode> exprNodes = new ArrayList<>();
                    for (int k = 0; k < exprCount; k++) {
                        exprNodes.add(0, root.popChild()); // expr
                    }

                    ParseTreeNode openingBracketNode = root.popChild(); // sep_op_brac
                    reducedNode.addChild(openingBracketNode);
                    for (ParseTreeNode exprNode : exprNodes) {
                        reducedNode.addChild(exprNode);
                    }
                    reducedNode.addChild(closingBracketNode);

                    root.addChild(reducedNode);

                    return;
                }
            }
        }
    }

    // START OF ARITHMETIC
    private static void checkArithmeticExpressionProduction(String[] stk, List<String[]> dataTable,
            ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // arithExp -> arithExp + Term | arithExp - Term
            if (stk[z].equals("factor") &&
                    isArithOperator(stk[z + 1])
                    &&
                    (stk[z + 2].equals("identifier") || stk[z + 2].equals("comet_literal")
                            || stk[z + 2].equals("factor"))) {
                stk[z] = "arithExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("arithExp");
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }

            if (stk[z].equals("arithExp") &&
                    isArithOperator(stk[z + 1])
                    &&
                    (stk[z + 2].equals("identifier") || stk[z + 2].equals("comet_literal")
                            || stk[z + 2].equals("factor"))) {
                stk[z] = "arithExp";
                stk[z + 1] = "";
                stk[z + 2] = "";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode previousNode2 = root.popChild();
                ParseTreeNode previousNode3 = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("arithExp");
                reducedNode.addChild(previousNode3);
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static boolean isArithOperator(String str) {
        return (str.equals("arith_plus")) || (str.equals("arith_minus"))
                || (str.equals("arith_mult")) || (str.equals("arith_div"));
    }

    private static void checkFactorProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        for (int z = 0; z < stk.length; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule factor -> id | comet_literal
            if ((stk[z].equals("identifier") || stk[z].equals("comet_literal")) &&
                    (stk[z + 1].equals("arith_plus") || stk[z + 1].equals("arith_minus")
                            || stk[z + 1].equals("arith_mult") || stk[z + 1].equals("arith_div"))) {
                // Perform reduction for identifier
                stk[z] = "factor";

                // Add reduction to dataTable
                dataTable.add(new String[] { "REDUCE TO " + joinWithoutNull(stk) + " <- " + original, "", "" });

                // Update Parse Tree
                ParseTreeNode previousNode = root.popChild();
                ParseTreeNode reducedNode = new ParseTreeNode("operator");
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }
    // END OF ARITHMETIC

    private static void checkNavigateProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 2; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule navigate -> navigate ( condition ) { stmt }
            if (stk[z].equals("navigate_token") &&
                    stk[z + 1].equals("conditionalExp") &&
                    stk[z + 2].equals("stmt")) {
                // Perform reduction for navigate
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

            // Check for producing rule propel -> propel { stmt }
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
                ParseTreeNode reducedNode = new ParseTreeNode("propelStmt");
                reducedNode.addChild(previousNode2);
                reducedNode.addChild(previousNode);
                root.addChild(reducedNode);

                return;
            }
        }
    }

    private static void checkOrbitProduction(String[] stk, List<String[]> dataTable, ParseTreeNode root) {
        removeEmptyValuesInBetween(stk);
        for (int z = 0; z < stk.length - 3; z++) {
            String original = constructOriginalString(stk, z);

            // Check for producing rule orbit -> orbit ( condition ) { stmt } ;
            if (stk[z].equals("orbit_token") &&
                    stk[z + 1].equals("conditionalExp") &&
                    stk[z + 2].equals("stmt") &&
                    stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for orbit
                stk[z] = "orbitStmt1";
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
                ParseTreeNode reducedNode = new ParseTreeNode("orbitStmt1");
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

            // Check for producing rule orbit -> orbit ( condition ) { stmt } propel { stmt
            // } ;
            if (stk[z].equals("orbit_token") &&
                    stk[z + 1].equals("conditionalExp") &&
                    stk[z + 2].equals("stmt") &&
                    stk[z + 3].equals("propelStmt") &&
                    stk[z + 4].equals("sep_semicolon")) {
                // Perform reduction for orbit
                stk[z] = "orbitStmt2";
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
                ParseTreeNode reducedNode = new ParseTreeNode("orbitStmt2");
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

            // Check for producing rule orbit -> orbit ( condition ) { stmt } navigate (
            // condition ) { stmt } propel { stmt } ;
            if (stk[z].equals("orbit_token") &&
                    stk[z + 1].equals("conditionalExp") &&
                    stk[z + 2].equals("stmt") &&
                    stk[z + 3].equals("navigateStmt") &&
                    stk[z + 4].equals("propelStmt") &&
                    stk[z + 5].equals("sep_semicolon")) {
                // Perform reduction for identifier
                stk[z] = "orbitStmt3";
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
                ParseTreeNode reducedNode = new ParseTreeNode("orbitStmt3");
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

            // Check for producing rule whirl -> whirl ( condition ) { stmt } ;
            if (stk[z].equals("whirl_token") &&
                    stk[z + 1].equals("conditionalExp") &&
                    stk[z + 2].equals("stmt") &&
                    stk[z + 3].equals("sep_semicolon")) {
                // Perform reduction for whirl
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

            // Check for producing rule launchWhirl -> launchWhirl { stmt } whirl (
            // condition ) ;
            if (stk[z].equals("launchwhirl_token") &&
                    stk[z + 1].equals("stmt") &&
                    stk[z + 2].equals("whirl_token") &&
                    stk[z + 3].equals("conditionalExp") &&
                    stk[z + 4].equals("sep_semicolon")) {
                // Perform reduction for launchWhirl
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