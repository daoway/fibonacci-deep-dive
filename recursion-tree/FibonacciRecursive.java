import java.util.UUID;

public class FibonacciRecursive {
    private final RecursionListener listener;

    public FibonacciRecursive(RecursionListener listener) {
        this.listener = listener;
    }

    public int fib(int n, Integer parentId) {
        int callId = UUID.randomUUID().hashCode();
        listener.onCall(n, parentId, callId);

        return switch (n) {
            case 0 -> 0;
            case 1 -> 1;
            default -> fib(n - 1, callId) + fib(n - 2, callId);
        };
    }
}
