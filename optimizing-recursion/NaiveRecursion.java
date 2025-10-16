public class NaiveRecursion implements FibonacciNumber<Long> {
    public Long fib(int n) {
        if (n == 0) {
            return 0L;
        }
        if (n == 1) {
            return 1L;
        }
        return fib(n - 1) + fib(n - 2);
    }
}
