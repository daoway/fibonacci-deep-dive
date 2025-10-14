public class SimpleIdent {
    static int depth = 0;

    public static int fib0(int n) throws Exception {
        // print with indentation
        String indent = "  ".repeat(depth);
        System.out.println(indent + "fib0(" + n + ") depth=" + depth);

        depth++;
        int result;
        if (n == 0) result = 0;
        else if (n == 1) result = 1;
        else result = fib0(n - 1) + fib0(n - 2);
        depth--;

        System.out.println(indent + "=> fib0(" + n + ") = " + result);
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Result: " + fib0(5));
    }
}
