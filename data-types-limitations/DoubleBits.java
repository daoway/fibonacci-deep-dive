public class DoubleBits {
    public static void main(String[] args) {
        printDoubleBits(0.1);
        printDoubleBits(0.2);
        printDoubleBits(0.3);
    }

    private static void printDoubleBits(double value) {
        long bits = Double.doubleToRawLongBits(value);
        String binary = String.format("%64s", Long.toBinaryString(bits)).replace(' ', '0');
        String sign = binary.substring(0, 1);
        String exponent = binary.substring(1, 12);
        String mantissa = binary.substring(12);

        System.out.println("Value: " + value);
        System.out.println("Binary: " + binary);
        System.out.println("\tSign: " + sign);
        System.out.println("\tExponent: " + exponent);
        System.out.println("\tMantissa: " + mantissa);
    }
}
