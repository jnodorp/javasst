package javasst;

/**
 * Enumeration of possible types.
 */
public enum JavaSstType {

    /**
     * Common types.
     */
    CLASS, VOID,

    /**
     * Token types.
     */
    CURLY_BRACE_OPEN, CURLY_BRACE_CLOSE, FINAL, EQUALS, SEMICOLON, PUBLIC, PARENTHESIS_OPEN,
    PARENTHESIS_CLOSE, COMMA, INT, IF, ELSE, WHILE, RETURN, EQUALS_EQUALS, LESS_THAN, LESS_THAN_EQUALS,
    GREATER_THAN, GREATER_THAN_EQUALS, PLUS, MINUS, TIMES, SLASH, COMMENT_START, COMMENT_STOP, IDENT, NUMBER, EOF,

    /**
     * Parser object and node classes.
     */
    ASSIGNMENT, CALL, CONSTANT, FUNCTION, IF_ELSE, PARAMETER, VARIABLE,

    /**
     * Parser object types.
     */
    INTEGER
}