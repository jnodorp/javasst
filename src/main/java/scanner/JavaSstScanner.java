package scanner;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class processes input provided by an {@link Input} instance.
 */
public class JavaSstScanner implements Iterator<Symbol> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JavaSstScanner.class.getName());

    /**
     * A pattern matching digits.
     */
    private static final Pattern DIGIT = Pattern.compile("[0-9]");

    /**
     * A pattern matching letters.
     */
    private static final Pattern LETTER = Pattern.compile("[a-zA-Z]");

    /**
     * The {@link Input} stream.
     */
    private final Input input;

    /**
     * The last read character.
     */
    private char current;

    /**
     * If the currently read characters are in a comment.
     */
    private boolean comment = false;

    /**
     * The read characters since the last symbol.
     */
    private String stack;

    /**
     * Create a new {@link JavaSstScanner} which processes the input provided by an {@link Input} instance.
     *
     * @param input The {@link Input} instance.
     */
    public JavaSstScanner(final Input input) {
        this.input = input;
    }

    @Override
    public boolean hasNext() {
        return input.hasNext();
    }

    @Override
    public Symbol next() {
        stack = "";
        SymbolType symbol = null;

        // Skip whitespaces.
        while (current <= ' ') {
            String whitespace = (current + "").replaceAll("\n", "\\\\n");
            whitespace = whitespace.replaceAll("\r", "\\\\r");
            LOGGER.log(Level.INFO, "Skipping whitespace '" + whitespace + "'.");
            current = input.next();
        }
        stack += current;

        LOGGER.log(Level.INFO, "Matching character '" + current + "'.");
        switch (current) {
            case '{':
                symbol = SymbolType.CURLY_BRACE_OPEN;
                break;
            case '}':
                symbol = SymbolType.CURLY_BRACE_CLOSE;
                break;
            case ';':
                symbol = SymbolType.SEMICOLON;
                break;
            case '(':
                symbol = SymbolType.PARENTHESES_OPEN;
                break;
            case ')':
                symbol = SymbolType.PARENTHESIS_CLOSE;
                break;
            case ',':
                symbol = SymbolType.COMMA;
                break;
            case '+':
                symbol = SymbolType.PLUS;
                break;
            case '-':
                symbol = SymbolType.MINUS;
                break;
            case '*':
                symbol = lookahead("*/", SymbolType.COMMENT_STOP, SymbolType.TIMES);
                break;
            case '/':
                symbol = lookahead("/*", SymbolType.COMMENT_START, SymbolType.SLASH);
                break;
            case '<':
                symbol = lookahead("<=", SymbolType.LESS_THAN_EQUALS, SymbolType.LESS_THAN);
                break;
            case '>':
                symbol = lookahead(">=", SymbolType.GREATER_THAN_EQUALS, SymbolType.GREATER_THAN);
                break;
            case '=':
                symbol = lookahead("==", SymbolType.EQUALS_EQUALS, SymbolType.EQUALS);
                break;
            case 'c':
                symbol = lookahead("class", SymbolType.CLASS, null);
                break;
            case 'e':
                symbol = lookahead("else", SymbolType.ELSE, null);
                break;
            case 'f':
                symbol = lookahead("final", SymbolType.FINAL, null);
                break;
            case 'i':
                if (lookahead("if", SymbolType.IF, null) != null) {
                    symbol = SymbolType.IF;
                } else if (lookahead("int", SymbolType.INT, null) != null) {
                    symbol = SymbolType.INT;
                } else {
                    symbol = null;
                }
                break;
            case 'p':
                symbol = lookahead("public", SymbolType.PUBLIC, null);
                break;
            case 'r':
                symbol = lookahead("return", SymbolType.RETURN, null);
                break;
            case 'v':
                symbol = lookahead("void", SymbolType.VOID, null);
                break;
            case 'w':
                symbol = lookahead("while", SymbolType.WHILE, null);
                break;
            case (char) -1:
                symbol = SymbolType.EOF;
                break;
            default:
                if (DIGIT.matcher("" + current).matches()) {
                    while (DIGIT.matcher("" + current).matches()) {
                        current = input.next();
                        stack += current;
                        symbol = SymbolType.NUMBER;
                    }
                }
        }

        if (symbol == null && LETTER.matcher("" + current).matches()) {
            while (LETTER.matcher("" + current).matches() || DIGIT.matcher("" + current).matches()) {
                current = input.next();
                stack += current;
                symbol = SymbolType.IDENT;
            }
        }

        int positionBias = 0;
        if (symbol == SymbolType.IDENT || symbol == SymbolType.NUMBER) {
            stack = stack.substring(0, stack.length() - 1);
            positionBias++;
        } else {
            current = input.next();
        }

        if (stack.length() == 1) {
            positionBias++;
        }

        if (symbol == SymbolType.COMMENT_START) {
            comment = true;
            return next();
        }

        if (symbol == SymbolType.COMMENT_STOP) {
            comment = false;
            return next();
        }

        if (comment) {
            return next();
        }

        if (symbol == null) {
            throw new InputMismatchException("Invalid input '" + stack + "' at " + input.getPosition());
        }

        LOGGER.log(Level.INFO, "Found symbol '" + symbol + "' with stack '" + stack + "'.");

        final Position position = new Position(input.getPosition().getFile(), input.getPosition().getLine(), input
                .getPosition().getColumn() - (stack.length() - positionBias));
        return new Symbol(stack, symbol, position);
    }

    /**
     * Perform a lookahead.
     *
     * @param match   The characters to look at.
     * @param success Symbol type to return on match.
     * @param failure Symbol type to return on mismatch.
     * @return One of the specified symbols.
     */
    private SymbolType lookahead(final String match, final SymbolType success, final SymbolType failure) {
        String newMatch = match.substring(1);

        final StringBuilder lookahead = new StringBuilder();
        Arrays.stream(input.lookahead(newMatch.getBytes().length)).forEach(lookahead::append);

        if (newMatch.equals(lookahead.toString())) {
            stack = match;
            for (int i = 0; i < newMatch.getBytes().length; i++) {
                input.next();
            }
            return success;
        } else {
            return failure;
        }
    }
}
