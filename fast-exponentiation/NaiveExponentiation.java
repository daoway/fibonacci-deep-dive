public class NaiveExponentiation {
    double pow(double a, int n) {
        if (n == 0) {
            return 1;
        }
        return a * pow(a, n - 1);
    }
}
