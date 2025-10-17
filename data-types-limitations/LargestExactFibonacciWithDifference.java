import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


// Strategy for BigInteger
class BigIntegerAdditionStrategy implements AdditionStrategy<BigInteger> {
    @Override
    public BigInteger add(BigInteger a, BigInteger b) throws ArithmeticException {
        return a.add(b);
    }
}

@SuppressWarnings("all")
public class LargestExactFibonacciWithDifference {
    public static void main(String[] args) {
        Map<Class<? extends Number>, AdditionStrategy<?>> strategies = new HashMap<>();
        strategies.put(Integer.class, new IntegerAdditionStrategy());
        strategies.put(Long.class, new LongAdditionStrategy());
        strategies.put(Float.class, new FloatAdditionStrategy());
        strategies.put(Double.class, new DoubleAdditionStrategy());
        strategies.put(BigInteger.class, new BigIntegerAdditionStrategy());

        // Define indices to compute differences
        int[] indices = {1, 10, 20, 30, 46, 50, 92, 100, 186, 500, 1000, 1476};

        // Compute Fibonacci results and differences for each type and index
        System.out.println("Index, int_diff, long_diff, float_diff, double_diff");
        for (int n : indices) {
            FibonacciResult<Integer> intResult = computeFibonacciAtIndex(n, 0, 1,
                    (AdditionStrategy<Integer>) strategies.get(Integer.class));
            FibonacciResult<Long> longResult = computeFibonacciAtIndex(n, 0L, 1L,
                    (AdditionStrategy<Long>) strategies.get(Long.class));
            FibonacciResult<Float> floatResult = computeFibonacciAtIndex(n, 0.0F, 1.0F,
                    (AdditionStrategy<Float>) strategies.get(Float.class));
            FibonacciResult<Double> doubleResult = computeFibonacciAtIndex(n, 0.0D, 1.0D,
                    (AdditionStrategy<Double>) strategies.get(Double.class));

            BigInteger intDiff = computeFibonacciDifference(intResult,
                    (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class));
            BigInteger longDiff = computeFibonacciDifference(longResult,
                    (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class));
            BigInteger floatDiff = computeFibonacciDifference(floatResult,
                    (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class));
            BigInteger doubleDiff = computeFibonacciDifference(doubleResult,
                    (AdditionStrategy<BigInteger>) strategies.get(BigInteger.class));

            // Format float and double differences in scientific notation for n > 100
            String floatDiffStr = n > 100 ? formatScientific(floatDiff) : floatDiff.toString();
            String doubleDiffStr = n > 100 ? formatScientific(doubleDiff) : doubleDiff.toString();

            System.out.printf("%d, %s, %s, %s, %s%n",
                    n, intDiff, longDiff, floatDiffStr, doubleDiffStr);
        }
    }

    // Format BigInteger in scientific notation
    private static String formatScientific(BigInteger value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.toString().matches(".*E.*") ? decimal.toString() : decimal.toPlainString();
    }

    // Compute Fibonacci number at a specific index
    private static <T extends Number> FibonacciResult<T> computeFibonacciAtIndex(
            int targetIndex, T prev, T current, AdditionStrategy<T> strategy) {
        int index = 1;
        T result = current;

        try {
            while (index < targetIndex) {
                T next = strategy.add(prev, current);
                prev = current;
                current = next;
                index++;
                result = current;
            }
        } catch (ArithmeticException e) {
            // Return the last valid result if overflow occurs
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
                primitiveAsBigInt = BigDecimal.valueOf(f).toBigInteger();
                break;
            case Double d:
                primitiveAsBigInt = BigDecimal.valueOf(d).toBigInteger();
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + primitiveValue.getClass());
        }

        // Return absolute difference
        return current.subtract(primitiveAsBigInt).abs();
    }
}