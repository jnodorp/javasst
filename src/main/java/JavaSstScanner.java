import scanner.Input;
import scanner.TokenImpl;
import scanner.TokenType;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class processes input provided by an {@link Input} instance.
 */
public class JavaSstScanner implements Iterator<TokenImpl> {

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
     * The read characters since the last token.
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
    public TokenImpl next() {
        stack = "";
        TokenType symbol = null;

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
                symbol = TokenType.CURLY_BRACE_OPEN;
                break;
            case '}':
                symbol = TokenType.CURLY_BRACE_CLOSE;
                break;
            case ';':
                symbol = TokenType.SEMICOLON;
                break;
            case '(':
                symbol = TokenType.PARENTHESIS_OPEN;
                break;
            case ')':
                symbol = TokenType.PARENTHESIS_CLOSE;
                break;
            case ',':
                symbol = TokenType.COMMA;
                break;
            case '+':
                symbol = TokenType.PLUS;
                break;
            case '-':
                symbol = TokenType.MINUS;
                break;
            case '*':
                symbol = lookahead("*/", TokenType.COMMENT_STOP, TokenType.TIMES);
                break;
            case '/':
                symbol = lookahead("/*", TokenType.COMMENT_START, TokenType.SLASH);
                break;
            case '<':
                symbol = lookahead("<=", TokenType.LESS_THAN_EQUALS, TokenType.LESS_THAN);
                break;
            case '>':
                symbol = lookahead(">=", TokenType.GREATER_THAN_EQUALS, TokenType.GREATER_THAN);
                break;
            case '=':
                symbol = lookahead("==", TokenType.EQUALS_EQUALS, TokenType.EQUALS);
                break;
            case 'c':
                symbol = lookahead("class", TokenType.CLASS, null);
                break;
            case 'e':
                symbol = lookahead("else", TokenType.ELSE, null);
                break;
            case 'f':
                symbol = lookahead("final", TokenType.FINAL, null);
                break;
            case 'i':
                if (lookahead("if", TokenType.IF, null) != null) {
                    symbol = TokenType.IF;
                } else if (lookahead("int", TokenType.INT, null) != null) {
                    symbol = TokenType.INT;
                } else {
                    symbol = null;
                }
                break;
            case 'p':
                symbol = lookahead("public", TokenType.PUBLIC, null);
                break;
            case 'r':
                symbol = lookahead("return", TokenType.RETURN, null);
                break;
            case 'v':
                symbol = lookahead("void", TokenType.VOID, null);
                break;
            case 'w':
                symbol = lookahead("while", TokenType.WHILE, null);
                break;
            case (char) -1:
                symbol = TokenType.EOF;
                break;
            default:
                if (DIGIT.matcher("" + current).matches()) {
                    while (DIGIT.matcher("" + current).matches()) {
                        current = input.next();
                        stack += current;
                        symbol = TokenType.NUMBER;
                    }
                }
        }

        if (symbol == null && LETTER.matcher("" + current).matches()) {
            while (LETTER.matcher("" + current).matches() || DIGIT.matcher("" + current).matches()) {
                current = input.next();
                stack += current;
                symbol = TokenType.IDENT;
            }
        }

        int positionBias = 0;
        if (symbol == TokenType.IDENT || symbol == TokenType.NUMBER) {
            stack = stack.substring(0, stack.length() - 1);
            positionBias++;
        } else {
            current = input.next();
        }

        if (stack.length() == 1) {
            positionBias++;
        }

        if (symbol == TokenType.COMMENT_START) {
            comment = true;
            return next();
        }

        if (symbol == TokenType.COMMENT_STOP) {
            comment = false;
            return next();
        }

        if (comment) {
            return next();
        }

        if (symbol == null) {
            throw new InputMismatchException("Invalid input '" + stack + "' at " +
                    "[" + input.getLine() + ":" + input.getColumn() + "]@" + input.getFile());
        }

        LOGGER.log(Level.INFO, "Found token '" + symbol + "' with stack '" + stack + "'.");

        return new TokenImpl(stack, symbol, input.getLine(), input.getColumn() - (stack.length() - positionBias),
                input.getFile());
    }

    /**
     * Perform a lookahead.
     *
     * @param match   The characters to look at.
     * @param success TokenImpl type to return on match.
     * @param failure TokenImpl type to return on mismatch.
     * @return One of the specified symbols.
     */
    private TokenType lookahead(final String match, final TokenType success, final TokenType failure) {
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
