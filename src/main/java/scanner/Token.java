package scanner;

import java.io.File;

/**
 * A token is an identifier used within a programming language. E.g. int, class, etc..
 *
 * @param <E> The tokens type enumeration.
 */
public interface Token<E extends Enum> {

    /**
     * Get the tokens type for comparison. This should be a unique number per type (ideally the ordnal of an
     * enumeration).
     *
     * @return The tokens type.
     */
    E getType();

    /**
     * Get the tokens identifier. This is the string which 'created' the token.
     *
     * @return The tokens identifier.
     */
    String getIdentifier();

    /**
     * Get the line in the parsed file.
     *
     * @return The line in the parsed file.
     */
    int getLine();

    /**
     * Get the column in the parsed file.
     *
     * @return The column in the parsed file.
     */
    int getColumn();

    /**
     * Get the parsed file.
     *
     * @return The parsed file.
     */
    File getFile();
}
