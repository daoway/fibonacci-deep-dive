import java.io.FileWriter;
import java.io.IOException;

public class RecursionDotTree {

    static class NodeId {
        int id;
        NodeId(int id) { this.id = id; }
    }

    static int idCounter = 0;

    public static int fib0(int n, FileWriter fw, NodeId parent)
            throws IOException {
        int myId = idCounter++;
        fw.write(String.format("  node%d [label=\"fib(%d)\"];\n", myId, n));

        if (parent != null) {
            fw.write(String.format("  node%d -> node%d;\n", parent.id, myId));
        }

        int result;
        if (n == 0) result = 0;
        else if (n == 1) result = 1;
        else {
            int left = fib0(n - 1, fw, new NodeId(myId));
            int right = fib0(n - 2, fw, new NodeId(myId));
            result = left + right;
        }

        return result;
    }

    public static void main(String[] args){
        try (FileWriter fw = new FileWriter("fib_tree.dot")) {
            fw.write("digraph G {\n");
            fib0(5, fw, null);
            fw.write("}\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
