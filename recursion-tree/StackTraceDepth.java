public class StackTraceDepth {
    static int depth = 0;

    public static int fib0(int n) {
        depth++;
        
        int stackTraceSize = Thread.currentThread().getStackTrace().length;

        System.out.printf("fib0(%s) depth=%s frames=%s%n", n, depth,
                stackTraceSize);

        if (n == 0) {
            depth--;
            return 0;
        }
        if (n == 1) {
            depth--;
            return 1;
        }

        int result = fib0(n - 1) + fib0(n - 2);
        depth--;
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Result: " + fib0(5));
    }
}
