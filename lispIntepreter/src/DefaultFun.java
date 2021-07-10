import java.util.List;
import java.util.Optional;

public class DefaultFun {


    public static final Operation ECHO = new Operation() {
        @Override
        public Object action(List<Object> params) throws ArithmeticException, InterpreteError, Exception {
            if (!params.isEmpty()) {
                return params.get(params.size() - 1);
            }
            return this;
        }
    };
    public static final Operation PLUS = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            switch (judgeType(params)) {
                case LONG:
                    return params.stream().reduce(0L, (a, b) -> TransformUtils.toLong(a) + TransformUtils.toLong(b));
                case DOUBLE:
                    return params.stream().reduce(0.0, (a, b) -> TransformUtils.toDouble(a) + TransformUtils.toDouble(b));
                case STRING:
                    StringBuilder sb = new StringBuilder();
                    for (Object param : params) {
                        sb.append(param);
                    }
                    return sb.toString();
                default:
                    throw new InterpreteError();
            }
        }
    };
    public static final Operation SUBTRACTION = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            boolean isFloat = false;
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Double) {
                    isFloat = true;
                    break;
                }
            }
            try {
                if (isFloat) {
                    if (params.size() == 1) {
                        return -TransformUtils.toDouble(params.get(0));
                    } else {
                        double val = TransformUtils.toDouble(params.get(0));
                        for (int i = 1; i < params.size(); i++) {
                            val -= TransformUtils.toDouble(params.get(i));
                        }
                        return val;
                    }

                } else {
                    if (params.size() == 1) {
                        return -TransformUtils.toLong(params.get(0));
                    } else {
                        long val = TransformUtils.toLong(params.get(0));
                        for (int i = 1; i < params.size(); i++) {
                            val -= TransformUtils.toLong(params.get(i));
                        }
                        return val;
                    }
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation Multiple = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            switch (judgeType(params)) {
                case LONG:
                    return params.stream().reduce(1L, (a, b) -> TransformUtils.toLong(a) * TransformUtils.toLong(b));
                case DOUBLE:
                    return params.stream().reduce(1.0, (a, b) -> TransformUtils.toDouble(a) * TransformUtils.toDouble(b));
                case STRING:
                    if (params.size() != 2) {
                        throw new InterpreteError();
                    }

                    String str = null;
                    int multi = 0;
                    for (Object param : params) {
                        if (param instanceof String) {
                            str = (String) param;
                        } else {
                            multi = (int) TransformUtils.toLong(param);
                        }
                    }
                    if (multi == 0 || null == str) {
                        throw new InterpreteError();
                    }
                    return str.repeat(multi);
                default:
                    throw new InterpreteError();
            }
        }
    };
    public static final Operation DIVIDE = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            boolean isFloat = false;
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Double) {
                    isFloat = true;
                    break;
                }
            }
            try {
                if (isFloat) {
                    if (params.size() == 1) {
                        return 1 / TransformUtils.toDouble(params.get(0));
                    } else {
                        double val = TransformUtils.toDouble(params.get(0));
                        for (int i = 1; i < params.size(); i++) {
                            val /= TransformUtils.toDouble(params.get(i));
                        }
                        return val;
                    }

                } else {
                    if (params.size() == 1) {
                        return 1 / TransformUtils.toLong(params.get(0));
                    } else {
                        long val = TransformUtils.toLong(params.get(0));
                        for (int i = 1; i < params.size(); i++) {
                            val /= TransformUtils.toLong(params.get(i));
                        }
                        return val;
                    }
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation POW = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() != 2) {
                    throw new InterpreteError();
                }
                return pow(params.get(0), params.get(1));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation OR = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.isEmpty()) {
                    throw new InterpreteError();
                }
                return params.stream().reduce(0L, (a, b) -> TransformUtils.toLong(a) | TransformUtils.toLong(b));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation AND = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.isEmpty()) {
                    throw new InterpreteError();
                }
                return params.stream().reduce(-1L, (a, b) -> TransformUtils.toLong(a) & TransformUtils.toLong(b));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation XOR = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.isEmpty()) {
                    throw new InterpreteError();
                }
                return params.stream().reduce(0L, (a, b) -> TransformUtils.toLong(a) ^ TransformUtils.toLong(b));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation LSHIFT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() != 2) {
                    throw new InterpreteError();
                }
                return TransformUtils.toLong(params.get(0)) << TransformUtils.toLong(params.get(1));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation RSHIFT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() != 2) {
                    throw new InterpreteError();
                }
                return TransformUtils.toLong(params.get(0)) >> TransformUtils.toLong(params.get(1));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation ABS_RSHIFT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() != 2) {
                    throw new InterpreteError();
                }
                return TransformUtils.toLong(params.get(0)) >>> TransformUtils.toLong(params.get(1));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };

    public static final Operation NOT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() != 1) {
                    throw new InterpreteError();
                }
                return ~TransformUtils.toLong(params.get(0));
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };


    public static final Operation EQUAL = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    Object tmp = params.get(0);
                    for (int i = 1; i < params.size(); i++) {
                        if (!tmp.equals(params.get(i))) {
                            return 0;
                        }
                        tmp = params.get(i);
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation N_EQUAL = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    Object tmp = params.get(0);
                    for (int i = 1; i < params.size(); i++) {
                        if (tmp.equals(params.get(i))) {
                            return 0;
                        }
                        tmp = params.get(i);
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    // greater than
    public static final Operation GT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    double tmp = ((Number) params.get(0)).doubleValue();
                    for (int i = 1; i < params.size(); i++) {
                        if (tmp <= ((Number) params.get(i)).doubleValue()) {
                            return 0;
                        }
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };

    //greater or equal than
    public static final Operation GET = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    double tmp = ((Number) params.get(0)).doubleValue();
                    for (int i = 1; i < params.size(); i++) {
                        if (tmp < ((Number) params.get(i)).doubleValue()) {
                            return 0;
                        }
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };

    //lesser than
    public static final Operation LT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    double tmp = ((Number) params.get(0)).doubleValue();
                    for (int i = 1; i < params.size(); i++) {
                        if (tmp >= ((Number) params.get(i)).doubleValue()) {
                            return 0;
                        }
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    //lesser or equal than
    public static final Operation LET = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    double tmp = ((Number) params.get(0)).doubleValue();
                    for (int i = 1; i < params.size(); i++) {
                        if (tmp > ((Number) params.get(i)).doubleValue()) {
                            return 0;
                        }
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation LOGIC_OR = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    for (int i = 0; i < params.size(); i++) {
                        if (TransformUtils.toDouble(params.get(i)) > 0.0) {
                            return 1;
                        }
                    }
                    return 0;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation LOGIC_AND = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() == 1) {
                    throw new InterpreteError();
                } else {
                    for (int i = 0; i < params.size(); i++) {
                        if (TransformUtils.toDouble(params.get(i)) == 0.0) {
                            return 0;
                        }
                    }
                    return 1;
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };
    public static final Operation LOGIC_NOT = new Operation() {
        @Override
        public Object action(List<Object> params) throws InterpreteError {
            if (params.isEmpty()) {
                return this;
            }
            try {
                if (params.size() != 1) {
                    throw new InterpreteError();
                } else {
                    if (TransformUtils.toDouble(params.get(0)) == 0.0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } catch (Exception e) {
                throw new InterpreteError();
            }
        }
    };

    public static final String EXIT = "exit";


    private static Object pow(Object base, Object exponent) {
        if (base instanceof Double || exponent instanceof Double) {
            return Math.pow(TransformUtils.toDouble(base), TransformUtils.toDouble(exponent));
        } else {
            return quickPow(TransformUtils.toLong(base), TransformUtils.toLong(exponent));
        }
    }

    private static long quickPow(long base, long exp) {
        long res = 1;
        while (exp != 0) {
            if (exp % 2 == 1) {
                res *= base;
            }
            base *= base;
            exp >>= 1;
        }
        return res;
    }

    private static Type judgeType(List<Object> params) {
        boolean isDouble = false;
        for (Object param : params) {
            if (param instanceof String) {
                return Type.STRING;
            } else if (param instanceof Double) {
                isDouble = true;
            }
        }
        return isDouble ? Type.DOUBLE : Type.LONG;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(quickPow(2, 7));
        double num = 1.0;
        num++;
        System.out.println(num);
    }
}

enum Type {
    STRING, LONG, DOUBLE
}