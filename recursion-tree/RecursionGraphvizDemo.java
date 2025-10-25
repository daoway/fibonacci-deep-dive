public class RecursionGraphvizDemo {
    public static void main(String[] args){
        RecursionListener listener = new StepByStepListener();
        FibonacciRecursive fib = new FibonacciRecursive(listener);

        fib.fib(5, null);
    }
}
