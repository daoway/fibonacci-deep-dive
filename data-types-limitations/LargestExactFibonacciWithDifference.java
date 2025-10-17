import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

// Assume these are defined in another class/package
// import your.package.AdditionStrategy;
// import your.package.FibonacciResult;

// Unrestricted strategies for primitives (allow wrap-around or approximation)
class IntegerUnrestrictedStrategy implements AdditionStrategy<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b; // Allows signed overflow wrap-around
    }
}

class LongUnrestrictedStrategy implements AdditionStrategy<Long> {
    @Override
    public Long add(Long a, Long b) {
        return a + b; // Allows signed overflow wrap-around
    }
}

class FloatUnrestrictedStrategy implements AdditionStrategy<Float> {
    @Override
    public Float add(Float a, Float b) {
        return a + b; // Allows Infinity or NaN
    }
}

class DoubleUnrestrictedStrategy implements AdditionStrategy<Double> {
    @Override
    public Double add(Double a, Double b) {
        return a + b; // Allows Infinity or NaN
    }
}

// Strategy for BigInteger
class BigIntegerAdditionStrategy implements AdditionStrategy<BigInteger> {
    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }
}

@SuppressWarnings("all")
public class LargestExactFibonacciWithDifference {
    public static void main(String[] args) {
        Map<Class<? extends Number>, AdditionStrategy<?>> strategies = new HashMap<>();
        strategies.put(Integer.class, new IntegerUnrestrictedStrategy());
        strategies.put(Long.class, new LongUnrestrictedStrategy());
        strategies.put(Float.class, new FloatUnrestrictedStrategy());
        strategies.put(Double.class, new DoubleUnrestrictedStrategy());
        strategies.put(BigInteger.class, new BigIntegerAdditionStrategy());

        // Define indices to compute differences
        int[] indices = {1, 10, 20, 30, 46, 50, 92, 100, 186};

        // Define overflow/precision loss thresholds
        int intMaxIndex = 46; // F(47) overflows int
        int longMaxIndex = 92; // F(93) overflows long
        int floatMaxIndex = 186; // float reaches Infinity around here
        int doubleMaxIndex = 186; // double still valid at 186

        // Compute Fibonacci results and differences for each type and index
        System.out.println("Index, int_diff, long_diff, float_diff, double_diff");
        for (int n : indices) {
            String intDiffStr = n > intMaxIndex ? "Overflow" : computeFibonacciDifference(
                    computeFibonacciAtIndex(n, 0, 1, (AdditionStrategy<Integer>) strategies.get(Integer.class)),
                    (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class)).toString();
            String longDiffStr = n > longMaxIndex ? "Overflow" : computeFibonacciDifference(
                    computeFibonacciAtIndex(n, 0L, 1L, (AdditionStrategy<Long>) strategies.get(Long.class)),
                    (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class)).toString();
            String floatDiffStr = n > floatMaxIndex ? "Infinity" : formatDifference(
                    computeFibonacciDifference(
                            computeFibonacciAtIndex(n, 0.0F, 1.0F, (AdditionStrategy<Float>) strategies.get(Float.class)),
                            (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class)), n);
            String doubleDiffStr = n > doubleMaxIndex ? "Precision Loss" : formatDifference(
                    computeFibonacciDifference(
                            computeFibonacciAtIndex(n, 0.0D, 1.0D, (AdditionStrategy<Double>) strategies.get(Double.class)),
                            (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class)), n);

            System.out.printf("%d, %s, %s, %s, %s%n",
                    n, intDiffStr, longDiffStr, floatDiffStr, doubleDiffStr);
        }
    }

    // Format difference in scientific notation for n >= 100
    private static String formatDifference(BigInteger value, int index) {
        if (value.equals(BigInteger.ZERO)) return "0";
        if (index >= 100) {
            BigDecimal decimal = new BigDecimal(value);
            return String.format("%.4E", decimal); // Scientific notation with 4 decimal places
        }
        return value.toString();
    }

    // Compute Fibonacci number at a specific index
    private static <T extends Number> FibonacciResult<T> computeFibonacciAtIndex(
            int targetIndex, T prev, T current, AdditionStrategy<T> strategy) {
        int index = 1;
        T result = current;

        while (index < targetIndex) {
            T next = strategy.add(prev, current);
            prev = current;
            current = next;
            index++;
            result = current;
        }

        return FibonacciResult.of(index, result);
    }

    // Compute the difference between a primitive Fibonacci result and BigInteger result
    private static <T extends Number> BigInteger computeFibonacciDifference(
            FibonacciResult<T> primitiveResult, AdditionStrategy<BigInteger> bigIntStrategy) {
        int index = primitiveResult.getIndex();
        T primitiveValue = primitiveResult.getValue();

        // Compute exact Fibonacci number using BigInteger
        BigInteger prev = BigInteger.ZERO;
        BigInteger current = BigInteger.ONE;
        for (int i = 2; i <= index; i++) {
            BigInteger next = bigIntStrategy.add(prev, current);
            prev = current;
            current = next;
        }

        // Convert primitive value to BigInteger for comparison
        BigInteger primitiveAsBigInt;
        switch (primitiveValue) {
            case Integer i:
                primitiveAsBigInt = BigInteger.valueOf(i);
                break;
            case Long l:
                primitiveAsBigInt = BigInteger.valueOf(l);
                break;
            case Float f:
                if (Float.isInfinite(f) || Float.isNaN(f)) {
                    return current; // Difference is the full BigInteger value
                }
                primitiveAsBigInt = BigDecimal.valueOf(f).toBigInteger();
                break;
            case Double d:
                if (Double.isInfinite(d) || Double.isNaN(d)) {
                    return current; // Difference is the full BigInteger value
                }
                primitiveAsBigInt = BigDecimal.valueOf(d).toBigInteger();
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + primitiveValue.getClass());
        }

        // Return absolute difference
        return current.subtract(primitiveAsBigInt).abs();
    }
}