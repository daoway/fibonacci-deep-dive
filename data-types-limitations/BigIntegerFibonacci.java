import java.math.BigInteger;

public class BigIntegerFibonacci implements FibonacciSequence<BigInteger> {
    @Override
    public BigInteger fib(int n) {
        if (n <= 0) {
            return BigInteger.ZERO;
        }
        if (n == 1) {
            return BigInteger.ONE;
        }

        BigInteger prev = BigInteger.ZERO;
        BigInteger curr = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            BigInteger next = prev.add(curr);
            prev = curr;
            curr = next;
        }
        return curr;
    }
}
