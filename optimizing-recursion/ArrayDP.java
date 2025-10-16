public class ArrayDP implements FibonacciSequence<Long> {
    @Override
    public Long fib(int n) {
        if (n <= 0) {
            return 0L;
        }
        long[] fib = new long[n + 1];
        fib[0] = 0;
        fib[1] = 1;
        for (int i = 2; i <= n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib[n];
    }
}
