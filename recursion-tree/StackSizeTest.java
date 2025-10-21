import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class StackSizeTest {
    public static void main(String[] args) {
        // Список значень -Xss для тестування (можна додати/змінити)
        List<String> xssValues = Arrays.asList("512k", "1m", "2m", "4m", "8m");
        String outputFile = "results.csv";

        try (FileWriter writer = new FileWriter(outputFile)) {
            // Записуємо заголовок CSV
            writer.write("Xss_Value,Max_Depth\n");

            // Цикл по кожному значенню -Xss
            for (String xss : xssValues) {
                // Запускаємо окремий процес java з -Xss
                ProcessBuilder pb = new ProcessBuilder("java", "-Xss" + xss, "RecursionDepth");
                pb.redirectErrorStream(true); // Об'єднуємо stdout і stderr
                Process process = pb.start();

                // Читаємо вивід
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                String depth = "0"; // За замовчуванням, якщо не знайдемо
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Max recursion depth:")) {
                        depth = line.split(":")[1].trim(); // Витягуємо число
                    }
                }
                process.waitFor(); // Чекаємо завершення процесу

                // Записуємо результат у CSV
                writer.write(xss + "," + depth + "\n");
                System.out.println("Test for -Xss" + xss + ": Depth = " + depth);
            }

            System.out.println("Results saved to " + outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}