public class IterativeExponentiation {
    double binaryPow(double a, int n) {
        double result = 1;
        double base = a;
        while (n > 0) {
            if ((n & 1) == 1) {
                result *= base;
            }
            base *= base;
            n = n / 2;
        }
        return result;
    }
}
