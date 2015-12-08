package parser;

/**
 * A constant.
 */
public class ParserObjectVariable extends ParserObject {

    @Override
    public ObjectClass getParserObjectClass() {
        return ObjectClass.VARIABLE;
    }
}
