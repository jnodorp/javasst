package parser;

/**
 * A constant.
 */
public class ParserObjectConstant extends ParserObject {

    /**
     * The integer value.
     */
    private final long intValue;

    /**
     * The double value.
     */
    private final double doubleValue;

    /**
     * Create a new integer constant.
     *
     * @param intValue The integer value.
     */
    public ParserObjectConstant(long intValue) {
        this.intValue = intValue;
        this.doubleValue = 0;
    }

    /**
     * Create a new double constant.
     *
     * @param doubleValue The double value.
     */
    public ParserObjectConstant(double doubleValue) {
        this.doubleValue = doubleValue;
        this.intValue = 0;
    }

    @Override
    public ObjectClass getParserObjectClass() {
        return ObjectClass.CONSTANT;
    }

    /**
     * Get the integer value.
     *
     * @return The integer value.
     */
    public long getIntValue() {
        return intValue;
    }

    /**
     * Get the double value.
     *
     * @return The double value.
     */
    public double getDoubleValue() {
        return doubleValue;
    }
}
