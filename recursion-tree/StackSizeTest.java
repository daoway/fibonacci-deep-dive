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
            writer.write("Xss_Value,Max_Depth,Frame_Size\n");

            // Цикл по кожному значенню -Xss
            for (String xss : xssValues) {
                // Конвертуємо Xss у байти (k=1024, m=1024*1024)
                long stackSizeInBytes;
                if (xss.endsWith("k")) {
                    stackSizeInBytes = Long.parseLong(xss.replace("k", "")) * 1024;
                } else if (xss.endsWith("m")) {
                    stackSizeInBytes = Long.parseLong(xss.replace("m", "")) * 1024 * 1024;
                } else {
                    stackSizeInBytes = 0; // Некоректне значення
                }

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

                // Обчислюємо розмір фрейму (StackSize / Depth)
                double frameSize = stackSizeInBytes / Double.parseDouble(depth);

                // Записуємо результат у CSV
                writer.write(xss + "," + depth + "," + String.format("%.2f", frameSize) + "\n");
                System.out.println("Test for -Xss" + xss + ": Depth = " + depth + ", Frame Size = " + String.format("%.2f", frameSize) + " bytes");
            }

            System.out.println("Results saved to " + outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}