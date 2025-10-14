import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FibAnimationDOT {

    static class Node {
        int id;
        int n;
        Integer parentId;
        Node(int id, int n, Integer parentId) { this.id = id; this.n = n; this.parentId = parentId; }
    }

    static int idCounter = 0;
    static int frameCounter = 0;
    static List<Node> nodes = new ArrayList<>();

    public static int fib0(int n, Integer parentId) throws IOException {
        int myId = idCounter++;
        Node node = new Node(myId, n, parentId);
        nodes.add(node);

        // Створюємо DOT-файл для кадру
        generateFrame(myId);

        int result;
        if (n == 0) result = 0;
        else if (n == 1) result = 1;
        else {
            int left = fib0(n - 1, myId);
            int right = fib0(n - 2, myId);
            result = left + right;
        }
        return result;
    }

    private static void generateFrame(int activeId) throws IOException {
        String filename = String.format("frame_%03d.dot", frameCounter++);
        FileWriter fw = new FileWriter(filename);
	
	fw.write("digraph G {\n");
	fw.write("  graph [dpi=300];\n"); // високий DPI
	fw.write("  node [shape=circle, width=1.0, height=1.0, fontsize=20];\n");

        for (Node node : nodes) {
            String color = node.id == activeId ? "yellow" : "lightgray";
            String label = "fib(" + node.n + ")";
            fw.write(String.format("  node%d [label=\"%s\", style=filled, fillcolor=%s];\n",
                    node.id, label, color));

            if (node.parentId != null) {
                fw.write(String.format("  node%d -> node%d;\n", node.parentId, node.id));
            }
        }

        fw.write("}\n");
        fw.close();
        System.out.println("Generated " + filename);
    }

    public static void main(String[] args) throws IOException {
        idCounter = 0;
        frameCounter = 0;
        nodes.clear();

        int n = 5;
        fib0(n, null);
        System.out.println("All frames generated. Convert them to PNG and make GIF.");
    }
}
