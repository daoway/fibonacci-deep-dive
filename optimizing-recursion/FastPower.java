public class FastPower {
    public static long pow(long base, long exponent) {
        long result = 1;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result *= base;
            }
            base *= base;
            exponent /= 2;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(pow(3, 13));  // 1,594,323
    }
}