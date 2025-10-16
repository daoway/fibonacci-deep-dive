public class IterativeFibonacci implements FibonacciSequence<Long> {
    @Override
    public Long fib(int n) {
        if (n <= 0) {
            return 0L;
        }
        if (n == 1) {
            return 1L;
        }
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }
}
