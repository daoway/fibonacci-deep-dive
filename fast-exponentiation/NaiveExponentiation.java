public class NaiveExponentiation {
    double pow(double a, int n) {
        if (n == 0)
            return 1;
        return a * pow(a, n - 1);
    }
    public static void main(String[] args) {
        NaiveExponentiation ne = new NaiveExponentiation();
        System.out.println(ne.pow(2, 10)); // Should print 1024.0
    }
}
