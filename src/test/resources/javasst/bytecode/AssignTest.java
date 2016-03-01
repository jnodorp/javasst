class AssignTest {
    final int const1 = 0;

    int field1;
    int field2;

    public int assignCallToField() {
        field1 = call();
        return field1;
    }

    public int assignConstToField() {
        field1 = const1;
        return field1;
    }

    public int assignFieldToField() {
        field1 = field2;
        return field2;
    }

    public int assignVarToField() {
        int var;
        var = 8;

        field1 = var;
        return field1;
    }

    public int assignParamToField(int param) {
        field1 = param;
        return field1;
    }

    public int assignExpressionToField() {
        /** field1 = 16 == 23; */
        return field1;
    }

    public int assignNumberToField() {
        field1 = 64;
        return field1;
    }

    public int call() {
        return 4;
    }
}