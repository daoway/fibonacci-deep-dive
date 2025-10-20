public class FastRecursiveExponentiation {
    double fastPow(double a, int n) {
        if (n == 0) {
            return 1;
        }
        double half = fastPow(a, n / 2);
        if (n % 2 == 0) {
            return half * half;
        } else {
            return a * half * half;
        }
    }
}
