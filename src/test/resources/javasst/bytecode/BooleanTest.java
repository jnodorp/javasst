class BooleanTest {

    public int testLessThanEquals(int x, int y) {
        if(x <= y) {
            return 1;
        } else {
            return 0;
        }
    }

    public int testLessThan(int x, int y) {
        if(x < y) {
            return 1;
        } else {
            return 0;
        }
    }

    public int testEqualsEquals(int x, int y) {
        if(x == y) {
            return 1;
        } else {
            return 0;
        }
    }

    public int testGreaterThanEquals(int x, int y) {
        if(x >= y) {
            return 1;
        } else {
            return 0;
        }
    }

    public int testGreaterThan(int x, int y) {
        if(x > y) {
            return 1;
        } else {
            return 0;
        }
    }
}