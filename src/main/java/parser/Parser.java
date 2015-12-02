package parser;

/**
 * A parser converts a scanner to a tree structure.
 */
public interface Parser {

    /**
     * Start the parsing process (by calling the start node method).
     */
    void parse();
}
