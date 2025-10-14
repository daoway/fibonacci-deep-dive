public class StackTraceDepth {
    static int depth = 0;
    
    public static int fib0(int n) throws Exception{
        depth++;
        System.out.println("fib0(" + n + ") depth=" + depth + " frames=" + Thread.currentThread().getStackTrace().length);
        
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
    
    public static void main(String[] args) throws Exception {
        System.out.println("Result: " + fib0(5));
    }
}
