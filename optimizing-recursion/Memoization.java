import java.util.HashMap;
import java.util.Map;

public class Memoization implements FibonacciSequence<Long> {
    private final Map<Integer, Long> memo = new HashMap<>();

    @Override
    public Long fib(int n) {
        if (n <= 1) {
            return (long) n;
        }
        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        long result = fib(n - 1) + fib(n - 2);
        memo.put(n, result);
        return result;
    }

}
