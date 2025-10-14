import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class RecursiveGrowthDemonstrator {
    public static long fibonacciRecursive(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    public static void main(String[] args) {
        String filename = "fibonacci_data.csv"; 
        int last_n = 50; 
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write the CSV file header
            writer.println("n,Fn,time_sec");            
            for (int n = 1; n <= last_n; n++) {
                long startTime = System.nanoTime();
                long result = fibonacciRecursive(n);
                long endTime = System.nanoTime();

                double durationSec = (double) (endTime - startTime) / 1_000_000_000.0;
                writer.printf("%d,%d,%.10f%n", n, result, durationSec);
                System.out.printf("F(%d) calculated in %.4f sec.%n", n, durationSec);
            }
        } catch (IOException e) {
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }
}
