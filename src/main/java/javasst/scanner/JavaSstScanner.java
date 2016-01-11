package javasst.scanner;

import scanner.Input;
import scanner.Scanner;

import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static javasst.scanner.JavaSstTokenType.*;

/**
 * This class processes input provided by an {@link Input} instance.
 */
public class JavaSstScanner extends Scanner<JavaSstToken, JavaSstTokenType> {

    /**
     * A pattern matching digits.
     */
    private static final Pattern DIGIT = Pattern.compile("[0-9]");

    /**
     * A pattern matching letters.
     */
    private static final Pattern LETTER = Pattern.compile("[a-zA-Z]");

    /**
     * A pattern matching everything but letters and digits.
     */
    private static final Pattern NOT_LETTERS_OR_DIGITS = Pattern.compile("[^0-9a-zA-Z]");

    /**
     * If the currently read characters are in a comment.
     */
    private boolean comment = false;

    /**
     * Create a new {@link JavaSstScanner} which processes the input provided by an {@link Input} instance.
     *
     * @param input The {@link Input} instance.
     */
    public JavaSstScanner(final Input input) {
        super(input);
    }

    @Override
    public JavaSstToken next() {
        stack = "";
        JavaSstTokenType symbol = null;

        // Skip whitespaces.
        while (current <= ' ') {
            String whitespace = (current + "").replaceAll("\n", "\\\\n");
            whitespace = whitespace.replaceAll("\r", "\\\\r");
            LOGGER.log(Level.FINEST, "Skipping whitespace '" + whitespace + "'.");
            current = input.next();
        }
        stack += current;

        LOGGER.log(Level.FINER, "Matching character '" + current + "'.");
        switch (current) {
            case '{':
                symbol = CURLY_BRACE_OPEN;
                break;
            case '}':
                symbol = CURLY_BRACE_CLOSE;
                break;
            case ';':
                symbol = SEMICOLON;
                break;
            case '(':
                symbol = PARENTHESIS_OPEN;
                break;
            case ')':
                symbol = PARENTHESIS_CLOSE;
                break;
            case ',':
                symbol = COMMA;
                break;
            case '+':
                symbol = PLUS;
                break;
            case '-':
                symbol = MINUS;
                break;
            case '*':
                symbol = lookahead("*/", COMMENT_STOP, TIMES);
                break;
            case '/':
                symbol = lookahead("/*", COMMENT_START, SLASH);
                break;
            case '<':
                symbol = lookahead("<=", LESS_THAN_EQUALS, LESS_THAN);
                break;
            case '>':
                symbol = lookahead(">=", GREATER_THAN_EQUALS, GREATER_THAN);
                break;
            case '=':
                symbol = lookahead("==", EQUALS_EQUALS, EQUALS);
                break;
            case 'c':
                symbol = lookahead("class", CLASS, null);
                break;
            case 'e':
                symbol = lookahead("else", ELSE, null);
                break;
            case 'f':
                symbol = lookahead("final", FINAL, null);
                break;
            case 'i':
                if (lookahead("if", IF, null) != null) {
                    symbol = IF;
                } else if (lookahead("int", INT, null, NOT_LETTERS_OR_DIGITS) != null) {
                    symbol = INT;
                } else {
                    symbol = null;
                }
                break;
            case 'p':
                symbol = lookahead("public", PUBLIC, null);
                break;
            case 'r':
                symbol = lookahead("return", RETURN, null);
                break;
            case 'v':
                symbol = lookahead("void", VOID, null);
                break;
            case 'w':
                symbol = lookahead("while", WHILE, null);
                break;
            case (char) -1:
                symbol = EOF;
                break;
            default:
                if (DIGIT.matcher("" + current).matches()) {
                    while (DIGIT.matcher("" + current).matches()) {
                        current = input.next();
                        stack += current;
                        symbol = NUMBER;
                    }
                }
        }

        if (symbol == null && LETTER.matcher("" + current).matches()) {
            while (LETTER.matcher("" + current).matches() || DIGIT.matcher("" + current).matches()) {
                current = input.next();
                stack += current;
                symbol = IDENT;
            }
        }

        if (symbol == IDENT || symbol == NUMBER) {
            stack = stack.substring(0, stack.length() - 1);
        } else {
            if (!input.hasNext()) {
                current = (char) -1;
            } else {
                current = input.next();
            }
        }

        if (symbol == COMMENT_START) {
            comment = true;
            return next();
        }

        if (symbol == COMMENT_STOP) {
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

        LOGGER.log(Level.FINE, "Found token '" + symbol + "' with stack '" + stack + "'.");

        return new JavaSstToken(stack, symbol, input.getLine(), input.getColumn() - stack.length(), input.getFile());
    }
}
