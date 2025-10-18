public class FastPower {
    public static long pow(long x, long n) {
        if (n < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative");
        }
        if (n == 0) {
            return 1;
        }

        long result = 1;
        while (n > 0) {
            if (n % 2 == 1) { // Якщо експонента непарна
                result *= x;
            }
            x *= x; // Зводимо базу в квадрат
            n /= 2; // Ділимо експоненту на 2
        }

        return result;
    }

    public static void main(String[] args) {
        // Приклади використання
        System.out.println(pow(2, 10)); // 1024
        System.out.println(pow(3, 7));  // 2187
    }
}