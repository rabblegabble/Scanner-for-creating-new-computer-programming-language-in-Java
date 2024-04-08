public class Parser {

  
    private Scanner S;

    public Parser(Scanner S) {
        this.S = S;
    }
    // New Procedure Node, makes sure program is valid by checking procedure token continues to parse down program using recursive descent until program is parsed. //
  
    public Node parseProcedure() throws ParseException {
        Node proc = new Node("Procedure");
        match(Core.PROCEDURE);
        proc.addChild(new Node("ID", S.getId()));
        match(Core.ID);
        match(Core.IS);
        
        if (S.currentToken() == Core.BEGIN) {
            proc.addChild(parseStmtSeq());
        } else {
            proc.addChild(parseDeclSeq());
            match(Core.BEGIN);
            proc.addChild(parseStmtSeq());
        }

        match(Core.END);
        return proc;
    }
  
    // parses declSeq  
    public Node parseDeclSeq() throws ParseException {
        Node declSeq = new Node("DeclSeq");

        declSeq.addChild(parseDecl());
        while (S.currentToken() == Core.INTEGER || S.currentToken() == Core.RECORD) {
            declSeq.addChild(parseDecl());
        }

        return declSeq;
    }

    // parses StmtSeq
    public Node parseStmtSeq() throws ParseException {
        Node stmtSeq = new Node("StmtSeq");

        stmtSeq.addChild(parseStmt());
        while (validStmtStartToken(S.currentToken())) {
            stmtSeq.addChild(parseStmt());
        }

        return stmtSeq;
    }

    //parses declaration
    public Node parseDecl() throws ParseException {
        Node decl = new Node("Decl");

        if (S.currentToken() == Core.INTEGER) {
            decl.addChild(parseDeclInteger());
        } else if (S.currentToken() == Core.RECORD) {
            decl.addChild(parseDeclRecord());
        } else {
            throw new ParseException("Expected INTEGER OR RECORD but found " + S.currentToken());
        }

        return decl;
    }

    // parse declINT
    public Node parseDeclInteger() throws ParseException {
        match(Core.INTEGER);
        Node id = new Node("ID", S.getId());
        match(Core.ID);
        match(Core.SEMICOLON);

        return new Node("DeclInteger", id.value);  
    }

    // parse DeclRECORD
    public Node parseDeclRecord() throws ParseException {
        match(Core.RECORD);
        Node id = new Node("ID", S.getId());
        match(Core.ID);
        match(Core.SEMICOLON);

        return new Node("DeclRecord", id.value);  
    }

    
    // parses STMT
    public Node parseStmt() throws ParseException {
      Node stmt = new Node("Stmt");

      if(S.currentToken() == Core.ID) {
        stmt.addChild(parseAssign());
        
      } else if (S.currentToken() == Core.IF) {
        stmt.addChild(parseIf());
        
      } else if (S.currentToken() == Core.WHILE) {
        stmt.addChild(parseLoop());
        
      } else if (S.currentToken() == Core.OUT) {
        stmt.addChild(parseOut());
        
      } else if (S.currentToken() == Core.INTEGER || S.currentToken() == Core.RECORD) {
        stmt.addChild(parseDecl());
        
      } else {
             throw new ParseException("Expected ... but found " + S.currentToken());
      }

      return stmt;
    } 

    // parses assigning statement
    public Node parseAssign() throws ParseException {
      Node assign = new Node("Assign");

      assign.addChild(new Node("ID",S.getId()));
      match(Core.ID);

      if (S.currentToken() == Core.LBRACE) {
        assign.addChild(parseIndex());
        match(Core.ASSIGN);
        assign.addChild(parseExpr());
        match(Core.SEMICOLON);
      } else if (S.currentToken() == Core.ASSIGN) {
        match(Core.ASSIGN);

        if (S.currentToken() == Core.NEW) {
          match(Core.NEW);
          match(Core.RECORD);
          match(Core.LBRACE);
          assign.addChild(parseExpr());
          match(Core.RBRACE);
          match(Core.SEMICOLON);
          
        } else if (S.currentToken() == Core.RECORD) {
          match(Core.RECORD);
          assign.addChild(new Node("ID", S.getId()));
          match(Core.ID);
          match(Core.SEMICOLON);
          
        } else {
          assign.addChild(parseExpr());
          match(Core.SEMICOLON);
          
        }
      } else {
        throw new ParseException("Expected ... but found " + S.currentToken());
      }

      return assign;
    }

    // parses Index
    public Node parseIndex() throws ParseException {
      Node index = new Node("Index");

      if (S.currentToken() == Core.LBRACE) {
        match(Core.LBRACE);
        index.addChild(parseExpr());
        match(Core.RBRACE);
      }

      return index;
    }

    // parse Out
    public Node parseOut() throws ParseException {
      Node out = new Node("Out");

      match(Core.OUT);
      match(Core.LPAREN);
      out.addChild(parseExpr());
      match(Core.RPAREN);
      match(Core.SEMICOLON);

      return out;
    }

    // parseOUT
    public Node parseIf() throws ParseException {
      Node ifStmt = new Node("If");

      match(Core.IF);
      ifStmt.addChild(parseCond());
      match(Core.THEN);
      ifStmt.addChild(parseStmtSeq());

      if (S.currentToken() == Core.ELSE) {
        match(Core.ELSE);
        ifStmt.addChild(parseStmtSeq());
      }

      match(Core.END);

      return ifStmt;
    }

    //parseLOOP
    public Node parseLoop() throws ParseException {
      Node loop = new Node("Loop");

      match(Core.WHILE);
      loop.addChild(parseCond());
      match(Core.DO);
      loop.addChild(parseStmtSeq());
      match(Core.END);

      return loop;
    }

    //parses conditional statment
    public Node parseCond() throws ParseException {
      Node cond = new Node("Cond");

      if (S.currentToken() == Core.NOT) {
        match(Core.NOT);
        cond.addChild(parseCond());
      } else if (S.currentToken() == Core.ID || S.currentToken() == Core.CONST || S.currentToken() == Core.LPAREN) {
        cond.addChild(parseCmpr());

        if (S.currentToken() == Core.AND) {
          match(Core.AND);
          cond.addChild(parseCond());
        } else if (S.currentToken() == Core.OR) {
            match(Core.OR);
            cond.addChild(parseCond());
        }
      } else {
        throw new ParseException("Expected ... but found " + S.currentToken());
      }
      return cond;
    }

    // parses comparitive statement
    public Node parseCmpr() throws ParseException {
      Node cmpr = new Node("Cmpr");

      cmpr.addChild(parseExpr());

      if (S.currentToken() == Core.EQUAL) {
        match(Core.EQUAL);
      } else if (S.currentToken() == Core.LESS) { 
        match(Core.LESS);
      } else {
        throw new ParseException("Expected EQUAL or LESS but found " + S.currentToken());
      }

      cmpr.addChild(parseExpr());
      return cmpr;
    }

    //parse expression
    public Node parseExpr() throws ParseException {
      Node expr = new Node("Expr");

      expr.addChild(parseTerm());

      while (S.currentToken() == Core.ADD || S.currentToken() == Core.SUBTRACT) 
      {
       if (S.currentToken() == Core.ADD) {
         match(Core.ADD);
       } else {
         match(Core.SUBTRACT);
       }
        expr.addChild(parseTerm());
      }
      return expr;
    }

    // parses mult or divide ***Term 
    public Node parseTerm() throws ParseException {
      Node term = new Node("Term");

      term.addChild(parseFactor());

      while (S.currentToken() == Core.MULTIPLY || S.currentToken() == Core.DIVIDE) {
        if (S.currentToken() == Core.MULTIPLY) {
          match(Core.MULTIPLY);
        } else {
          match(Core.DIVIDE);
        }

          term.addChild(parseFactor());
      }
      return term;
    }

    //parses FACTOR
    public Node parseFactor() throws ParseException {
      Node factor = new Node("Factor");

      if (S.currentToken() == Core.ID) {
        factor.addChild(new Node("ID", S.getId()));
        match(Core.ID);

        if (S.currentToken() == Core.LBRACE) {
          factor.addChild(parseIndex());
        }
      } else if (S.currentToken() == Core.CONST) {
        factor.addChild(new Node("CONST", String.valueOf(S.getConst())));
        match(Core.CONST);
      } else if (S.currentToken() == Core.LPAREN) {
        match(Core.LPAREN);
        factor.addChild(parseExpr());
        match(Core.RPAREN);
      } else if (S.currentToken() == Core.IN) {
        match(Core.IN);
        match(Core.LPAREN);
        match(Core.RPAREN);
      } else {
        throw new ParseException("Expected ... but found " + S.currentToken());
      }
      
      return factor;
    }


    // match checks current token, and moves to next token //
  
    public void match(Core expectedToken) throws ParseException {
        if (S.currentToken() == expectedToken) {
            S.nextToken();
        } else {
            throw new ParseException("Expected token " + expectedToken + ", but found " + S.currentToken());
        }
    }

    // checks whether statment token is valid starting token //

    public boolean validStmtStartToken(Core token) {
        return token == Core.ID || token == Core.IF || token == Core.WHILE || token == Core.OUT;
    }
}