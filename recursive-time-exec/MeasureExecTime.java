import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MeasureExecTime {
    private static final String CSV_HEADER = "n,Fn,time_sec";

    public static void main(String[] args) {
        FibonacciSequence<Long> naiveFibonacci = new NaiveRecursion();
        measure(naiveFibonacci, "fibonacci_data.csv", 50);
    }

    private static void measure(FibonacciSequence<Long> algorithmImpl,
                                String filename, int count) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(CSV_HEADER);
            for (int n = 1; n <= count; n++) {
                long startTime = System.nanoTime();
                long result = algorithmImpl.fib(n);
                long endTime = System.nanoTime();
                double durationSec =
                        (double) (endTime - startTime) / 1_000_000_000.0;
                writer.printf("%d,%d,%.10f%n", n, result, durationSec);
                System.out.printf("F(%d) calculated in %.4f sec.%n", n,
                        durationSec);
            }
        } catch (IOException e) {
            System.err.println(
                    "Error while writing to file: " + e.getMessage());
        }
    }
}
