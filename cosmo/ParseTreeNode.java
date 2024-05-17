package cosmo;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
  private String symbol;
  private List<ParseTreeNode> children;

  public ParseTreeNode(String symbol) {
    this.symbol = symbol;
    this.children = new ArrayList<>();
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public List<ParseTreeNode> getChildren() {
    return children;
  }

  public void addChild(ParseTreeNode child) {
    this.children.add(child);
  }

  public boolean isLeaf() {
    return children == null || children.isEmpty();
  }

  public ParseTreeNode popChild() {
    if (children.isEmpty()) {
      return null;
    }
    return children.remove(children.size() - 1);
  }

  public String printTree() {
    return printTreeHelper(this, 0);
  }

  public void logTree() {
    logTreeHelper(this, 0);
  }

  // Recursive helper method to print the parse tree
  private String printTreeHelper(ParseTreeNode node, int depth) {
    // Create the string for the current node
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append("|____");
    }
    sb.append(node.getSymbol()).append("\n");

    // Recursively create strings for children
    for (ParseTreeNode child : node.getChildren()) {
      sb.append(printTreeHelper(child, depth + 1));
    }

    return sb.toString();
  }

  private void logTreeHelper(ParseTreeNode node, int depth) {
    // Print the current node
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append("|____");
    }
    sb.append(node.getSymbol());
    // System.out.println(sb.toString());

    // Recursively print children
    for (ParseTreeNode child : node.getChildren()) {
      logTreeHelper(child, depth + 1);
    }
  }
}