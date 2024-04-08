import java.io.*;
import java.nio.file.*;

// instantiates scanner class
public class Scanner {
    private Core currentToken;
    private String currentId;
    private int currentConst;
    private String fileContent;
    private int index = 0;
// makes public method and makes the scanner I decided to read the entire file into memory because one of the constraints of the project is that it takes then frees the memory needed, this is the best way I thought to do it
    public Scanner(String filePath) {
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            moveToNextToken();
        } catch (IOException e) {
            System.out.println("Error reading the input file");
        }
    }
    // returns current token
    public Core currentToken() {
        return currentToken;
    }
    // calls move to next token method in order to peek/get the next token
    public void nextToken() {
        moveToNextToken();
    }
    // gets the current ID
    public String getId() {
        return currentId;
    }
    // gets the current const
    public int getConst() {
        return currentConst;
    }
    //moves along file, gets next token, ends if it reaches the end of file
    private void moveToNextToken() {
        while (index < fileContent.length() && Character.isWhitespace(fileContent.charAt(index))) {
            index++;
        }
        if (index >= fileContent.length()) {
            currentToken = Core.EOS;
            return;
        }
     // implements a new string builder //   
    StringBuilder sb = new StringBuilder();

    //entire file is read in, while loop scans until whitespace or until a symbol is found. //
    while (index < fileContent.length()) {
        char c = fileContent.charAt(index);
        if (Character.isWhitespace(c) || isSymbol(c)) {
          // if string is longer than one character, string is passed into processtoken, for string to be processed and tokenized //  
          if (sb.length() > 0) {
                processToken(sb.toString());
                return;
            }
          
            if (isSymbol(c)) {
                processSymbol(c);
                index++;
                return;
            }
          // character is appended if it doesnt match our other cases //
        } else {
            sb.append(c);
        }
        index++;
    }
    if (sb.length() > 0) {
        processToken(sb.toString());
    } else {
        currentToken = Core.EOS;
    }
}
// checks if the current symbol is one of the symbols
private boolean isSymbol(char c) {
    return "+-*/:=<:;.,()[]{}".indexOf(c) >= 0;
}
// processes a token, checks if we have an integer within our bounds, if its a keyword, or valid id using our other methods 
private void processToken(String token) {
    try {
        currentConst = Integer.parseInt(token);
        if (currentConst >= 0 && currentConst <= 10007) {
            currentToken = Core.CONST;
        } else {
            System.out.print("ERROR - Invalid Const --> " + token + " <--");
            System.exit(1);
        }
    } catch (NumberFormatException e) {
        if (isKeyword(token.toLowerCase()) && token.equals(token.toLowerCase())) {
            currentToken = Core.valueOf(token.toUpperCase());
        } else if (isValidIdentifier(token)) {
            currentToken = Core.ID;
            currentId = token;
        } else {
            System.out.print("ERROR - Invalid ID --> " + token + " <--");
            System.exit(1);
        }
    }
}
// processes a symbol (passes char in to process) changes the current token //
private void processSymbol(char c) {
    switch (c) {
        case '+':
            currentToken = Core.ADD;
            break;
        case '-':
            currentToken = Core.SUBTRACT;
            break;
        case '*':
            currentToken = Core.MULTIPLY;
            break;
        case '/':
            currentToken = Core.DIVIDE;
            break;
        case ':':
            if (index + 1 < fileContent.length() && fileContent.charAt(index + 1) == '=') {
                currentToken = Core.ASSIGN;
                index++;
            } else {
                currentToken = Core.COLON;
            }
            break;
        case '=':
            currentToken = Core.EQUAL;
            break;
        case '<':
            currentToken = Core.LESS;
            break;
        case ';':
            currentToken = Core.SEMICOLON;
            break;
        case '.':
            currentToken = Core.PERIOD;
            break;
        case ',':
            currentToken = Core.COMMA;
            break;
        case '(':
            currentToken = Core.LPAREN;
            break;
        case ')':
            currentToken = Core.RPAREN;
            break;
        case '[':
            currentToken = Core.LBRACE;
            break;
        case ']':
            currentToken = Core.RBRACE;
            break;
        // default case handles error, this is basically the catch //
        default:
            System.out.print("ERROR - Invalid symbol --> " + c + " <--");
            System.exit(1);
    }
} 
        
    // checks if we have a keyword 
    private boolean isKeyword(String token) {
        for (Core c : Core.values()) {
            if (c.name().equalsIgnoreCase(token) && !c.equals(Core.ID) && !c.equals(Core.CONST) && !c.equals(Core.EOS) && !c.equals(Core.ERROR)) {
                return true;
            }
        }
        return false;
    }
    //Takes token , then .matches checkes that it matches the regular expression provided =] //
    private boolean isValidIdentifier(String token) {
        return token.matches("[a-zA-Z][a-zA-Z0-9]*");
    }
}