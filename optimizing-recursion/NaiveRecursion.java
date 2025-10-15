public class NaiveRecursion {
    public static int fib0(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fib0(n - 1) + fib0(n - 2);
    }
}
