public class StackFrameSizeTest {
    private static int depth = 0;

    public static void recurse() {
        depth++;
        recurse();
    }

    public static void main(String[] args) {
        try {
            recurse();
        } catch (StackOverflowError e) {
            // Припускаємо, що розмір стека задано через -Xss (наприклад, -Xss1m або -Xss2m)
            long stackSizeInBytes = 1024 * 1024; // За замовчуванням 1 MB, змінити на 2 * 1024 * 1024 для 2 MB
            double frameSize = (double) stackSizeInBytes / depth;
            System.out.println("Max recursion depth: " + depth);
            System.out.printf("Estimated frame size: %.2f bytes per call%n", frameSize);
            System.out.println("Note: Actual frame size may vary due to JVM overhead or optimizations.");
        }
    }
}