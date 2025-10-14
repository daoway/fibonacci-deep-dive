public class FibonacciTarget {
    public static void main(String[] args){
        System.out.println("Starting Fibonacci calculation...");
        int n = 5;
        long result = fibonacci(n);
        
        System.out.println("fibonacci(" + n + ") = " + result);
    }
    
    public static long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}