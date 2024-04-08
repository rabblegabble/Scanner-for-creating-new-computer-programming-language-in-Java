
// Lets me throw Exceptions during parse errors, takes in custom message //
public class ParseException extends Exception {
    public ParseException(String message) {
        super(message);
    }
}