import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Strategy interface for exact addition
interface AdditionStrategy<T extends Number> {
    T add(T a, T b) throws ArithmeticException;
}

// Strategy for Integer
class IntegerAdditionStrategy implements AdditionStrategy<Integer> {
    @Override
    public Integer add(Integer a, Integer b) throws ArithmeticException {
        return Math.addExact(a, b);
    }
}

// Strategy for Long
class LongAdditionStrategy implements AdditionStrategy<Long> {
    @Override
    public Long add(Long a, Long b) throws ArithmeticException {
        return Math.addExact(a, b);
    }
}


// Strategy for Double
// Note: Unlike int/long, Java does not provide Math.addExact for double/float.
// This is because floating-point arithmetic (IEEE 754) does not throw overflow
// exceptions: instead, results silently become Infinity, -Infinity, or NaN.
// If strict overflow detection is required, it must be implemented manually
// (e.g., by checking Double.isInfinite / Double.isNaN), or by using BigDecimal
// for arbitrary precision arithmetic.

class FloatAdditionStrategy implements AdditionStrategy<Float> {
    @Override
    public Float add(Float a, Float b) throws ArithmeticException {
        float result = a + b;

        // Check for positive or negative infinity (overflow)
        if (Float.isInfinite(result)) {
            throw new ArithmeticException("Float overflow: " + a + " + " + b);
        }

        // Check for Not-a-Number result
        if (Float.isNaN(result)) {
            throw new ArithmeticException("Result is NaN: " + a + " + " + b);
        }

        return result;
    }
}

class DoubleAdditionStrategy implements AdditionStrategy<Double> {
    @Override
    public Double add(Double a, Double b) throws ArithmeticException {
        double result = a + b;
        if (Double.isInfinite(result)) {
            throw new ArithmeticException("Double overflow: " + a + " + " + b);
        }
        if (Double.isNaN(result)) {
            throw new ArithmeticException("Result is NaN: " + a + " + " + b);
        }
        return result;
    }
}

class FibonacciResult<T extends Number> {
    private final int index;
    private final T value;

    private FibonacciResult(int index, T value) {
        this.index = index;
        this.value = value;
    }


    public static <T extends Number> FibonacciResult<T> of(int index, T value) {
        return new FibonacciResult<>(index, value);
    }

    public Class<T> getType() {
        return (Class<T>) value.getClass();
    }

    public int getIndex() {
        return index;
    }

    public T getValue() {
        return value;
    }

    public String toString() {
        return switch (value) {
            case Integer i -> Integer.toString(i);
            case Long l -> Long.toString(l);
            case Float f -> BigDecimal.valueOf(f).toPlainString();
            case Double d -> BigDecimal.valueOf(d).toPlainString();

            default -> value.toString();
        };
    }
}

@SuppressWarnings("all")
public class LargestExactFibonacci {
    public static void main(String[] args) {
        Map<Class<? extends Number>, AdditionStrategy<?>> strategies = new HashMap<>();
        strategies.put(Integer.class, new IntegerAdditionStrategy());
        strategies.put(Long.class, new LongAdditionStrategy());
        strategies.put(Float.class, new FloatAdditionStrategy());
        strategies.put(Double.class, new DoubleAdditionStrategy());


        FibonacciResult<Integer> resultInt = findLargestExactFibonacci(0, 1,
                (AdditionStrategy<Integer>) strategies.get(Integer.class));
        FibonacciResult<Long> resultLong = findLargestExactFibonacci(0L, 1L,
                (AdditionStrategy<Long>) strategies.get(Long.class));
        FibonacciResult<Float> resultFloat = findLargestExactFibonacci(0.0F, 1.0F,
                (AdditionStrategy<Float>) strategies.get(Float.class));
        FibonacciResult<Double> resultDouble = findLargestExactFibonacci(0.0D, 1.0D,
                (AdditionStrategy<Double>) strategies.get(Double.class));

        for (FibonacciResult<?> entry : List.of(resultInt, resultLong, resultFloat, resultDouble)) {
            System.out.printf("%s Fibonacci (%s): F(%s) = %s%n",
                    entry.getType().getSimpleName(), entry.getIndex(), entry.getIndex(), entry);
        }
    }
    private static <T extends Number> FibonacciResult<T> findLargestExactFibonacci(
            T prev, T current, AdditionStrategy<T> strategy) {
        int index = 1;

        while (true) {
            try {
                T next = strategy.add(prev, current);
                prev = current;
                current = next;
                index++;
            } catch (ArithmeticException e) {
                return FibonacciResult.of(index, current);
            }
        }
    }
}