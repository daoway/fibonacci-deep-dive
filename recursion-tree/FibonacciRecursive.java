import java.util.UUID;

public class FibonacciRecursive {
    private final RecursionListener listener;

    public FibonacciRecursive(RecursionListener listener) {
        this.listener = listener;
    }

    public int fib(int n, String parentId) {
        String callId = UUID.randomUUID().toString();
        listener.onCall(n, parentId, callId);

        if (n <= 1) {
            return n;
        } else {
            //for next calls, current callId becomes parentId
            return fib(n - 1, callId) + fib(n - 2, callId);
        }
    }
}
