import java.io.*;
import java.util.*;

public class Executor {
  Node rootNode;
  Deque<Map<String, Integer>> stackOfMaps;

  public Executor(Node rootNode) {

    this.rootNode = rootNode;
    this.stackOfMaps = new ArrayDeque<>();  
    }

  public void execute() {
    executeProcedure(rootNode);
  }
  
  private void executeProcedure(Node rootNode) {

    stackOfMaps.push(new HashMap<>());

      for(Node currentChild : rootNode.children) {

        if (currentChild.name == "StmtSeq") {
          executeStmtSeq(currentChild);
        } 
        
        else if (currentChild.name == "DeclSeq") {
          executeDeclSeq(currentChild);
        }
      }
    stackOfMaps.pop();
  }

    private void executeDeclSeq(Node rootNode) {
      for (Node currentChild : rootNode.children) {
        executeDecl(currentChild);
      }
    }
    private void executeStmtSeq(Node rootNode) {
      for (Node currentChild : rootNode.children) {
        executeStmt(currentChild);
      }
    }

    private void executeDecl(Node rootNode) {
      if(stackOfMaps.peek().containsKey(rootNode.children.get(0).name)){
        System.out.println("Declaration exists already in current scope");
        System.exit(0);
      }
      stackOfMaps.peek().put(rootNode.children.get(0).name,0);
    }

    private void executeStmt(Node rootNode) {
      Node currentStmtNode = rootNode.children.get(0);
      switch (currentStmtNode.name) {
        case "Assign":
          executeAssign(currentStmtNode);
          break;

        case "If":
          executeIf(currentStmtNode);
          break;

        case "Loop":
          executeLoop(currentStmtNode);
          break;

        case "Out":
          executeOut(currentStmtNode);
          break;

        case "Decl":
          executeDecl(currentStmtNode);
          break;

        default:
          System.out.println("Unknown Statment Type");
          System.exit(0);
      }
    }

    private void executeAssign(Node rootNode) {

      if (!rootNode.name.equals("Assign")) {
        System.out.println("Wrong node sent here");
        System.exit(0);
      }

      Node idNode = rootNode.children.get(0);
      String id = idNode.value;

      Node secondNode = rootNode.children.get(1);

      if (secondNode.name.equals("Index")) {

        int index = executeExpr(secondNode.children.get(0));

        Node exprNode = rootNode.children.get(2);

        int value = executeExpr(exprNode);

        Map<String, Integer> scope = stackOfMaps.peek();
        scope.put(id + "[" + index + "]", value);
      } else {
        int value = executeExpr(secondNode);

        Map<String, Integer> scope = stackOfMaps.peek();
        scope.put(id,value);
      }    
    } 

    private void executeIf(Node rootNode) {

      if (!rootNode.name.equals("If")) {
        System.out.println("Wrong node in If statement block");
        System.exit(0);
      }

      Node condNode = rootNode.children.get(0);

      boolean cond = executeCond(condNode);

      Node thenStmtSeqNode = rootNode.children.get(1);

      if (cond) {

        executeStmtSeq(thenStmtSeqNode);
        
      } else if (rootNode.children.size() > 2) {

        Node elseStmntSeqNode = rootNode.children.get(2);
        executeStmtSeq(elseStmntSeqNode);
      }
    }

    private void executeLoop(Node rootNode) {

      if (!rootNode.name.equals("Loop")) {
        System.out.println("Wrong Node in Loop block");
        System.exit(0);
      }

      Node condNode = rootNode.children.get(0);

      boolean cond = executeCond(condNode);

      Node stmtSeqNode = rootNode.children.get(1);

      while(cond) {

        executeStmtSeq(stmtSeqNode);
        cond = executeCond(condNode);
      }
    }

    private void executeOut(Node rootNode) {

      if(!rootNode.name.equals("Out")) {
        System.out.println("Wrong node in out block");
        System.exit(0);
      }

      Node exprNode = rootNode.children.get(0);

      int value = executeExpr(exprNode);

      System.out.println(value);
    }

   private boolean executeCond(Node rootNode) {
    if (!rootNode.name.equals("Cond")) {
        System.out.println("Wrong node in Cond block");
        System.exit(0);
    }

    Node firstChild = rootNode.children.get(0);

    if (firstChild.name.equals("Cmpr")) {
        return executeCmpr(firstChild);
    } else if (firstChild.name.equals("Not")) {
        return !executeCond(firstChild.children.get(0));
    } else if (firstChild.name.equals("And")) {
        boolean cond1 = executeCond(firstChild.children.get(0));
        boolean cond2 = executeCond(firstChild.children.get(1));
        return cond1 && cond2;
    } else if (firstChild.name.equals("Or")) {
        boolean cond1 = executeCond(firstChild.children.get(0));
        boolean cond2 = executeCond(firstChild.children.get(1));
        return cond1 || cond2;
    } else {
        System.out.println("Unexpected error in cond not and or block");
        System.exit(0);
    }

    // For compilation reasons
    return false;
}

   private boolean executeCmpr(Node rootNode) {
    int value1 = executeExpr(rootNode.children.get(0));
    String operator = rootNode.children.get(0).children.get(0).name;
    int value2 = executeExpr(rootNode.children.get(1));

    System.out.println(operator);
    System.out.println(value1);
    System.out.println(value2);

    if (operator.equals("=")) {
        return value1 == value2;
    } else if (operator.equals("<")) {
        return value1 < value2;
    } else {
        System.out.println("Not expected to be here in Cmpr block");
        System.exit(0);
    }

    // for compilation reasons
    return false;
}

    private int executeExpr(Node rootNode) {

      if (!rootNode.name.equals("Expr")) {
        System.out.println("Wrong node sent to Expr block");
        System.exit(0);
    }

    Node termNode = rootNode.children.get(0);
    int value = executeTerm(termNode);

    // If there are more children, they should be in pairs: an operator and a term
    for (int i = 1; i < rootNode.children.size(); i += 2) {
        String operator = rootNode.children.get(i).name;
        Node nextTermNode = rootNode.children.get(i+1);
        int nextValue = executeTerm(nextTermNode);

        if (operator.equals("+")) {
            value += nextValue;
        } else if (operator.equals("-")) {
            value -= nextValue;
        } else {
            System.out.println("Unexpected error in Expr block");
            System.exit(0);
        }
    }

    return value;
    }

    public Integer executeTerm(Node rootNode) {

       // The term node should have at least one child
    assert rootNode.children.size() > 0;

    Node factorNode = rootNode.children.get(0);
    int value = executeFactor(factorNode);

    // If there are more children, they should be in pairs: an operator and a term
    for (int i = 1; i < rootNode.children.size(); i += 2) {
        String operator = rootNode.children.get(i).name;
        Node nextFactorNode = rootNode.children.get(i+1);
        int nextValue = executeFactor(nextFactorNode);

        if (operator.equals("*")) {
            value *= nextValue;
        } else if (operator.equals("/")) {
            if (nextValue == 0) throw new RuntimeException("Division by zero error");
            value /= nextValue;
        } else {
            System.out.println("Error in dividing factor block");
            System.exit(0);
        }
    }

    return value;
    }

    public Integer executeFactor(Node factorNode) {

      // The factor node should have only one child
    assert factorNode.children.size() == 1;

    Node child = factorNode.children.get(0);
    String data = child.name;

    if (data.equals("CONST")) {
        return Integer.parseInt(child.value);
    } else if (data.equals("ID")) {
        String id = child.value;
        return lookupVariable(id);
    } else if (data.equals("Expr")) {
        return executeExpr(child);
    } else {
        System.out.println("Unexpected Error in factorNode");
        System.exit(0);
    }

    // for compilation reasons
    return 0;
    }  

    private int lookupVariable(String id) {
    for (Map<String, Integer> map : stackOfMaps) {
        if (map.containsKey(id)) {
            return map.get(id);
        }
    }
    System.out.println("Variable " + id + " not declared.");
    System.exit(0);
    
    // for compilation reasons
    return 0;
  }
}