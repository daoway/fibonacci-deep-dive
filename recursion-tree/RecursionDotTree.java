import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RecursionDotTree {

    static int idCounter = 0;

    public static int fib0(int n, StringBuilder sb, NodeId parent) {
        int myId = idCounter++;
        sb.append("node%d [label=\"fib(%d)\"];%n".formatted(myId, n));

        if (parent != null) {
            sb.append("  node%d -> node%d;%n".formatted(parent.id, myId));
        }

        int result;

        if (n == 0) {
            result = 0;
        } else if (n == 1) {
            result = 1;
        } else {
            int left = fib0(n - 1, sb, new NodeId(myId));
            int right = fib0(n - 2, sb, new NodeId(myId));
            result = left + right;
        }

        return result;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        fib0(5, sb, null);
        try {
            Files.write(Paths.get("fib_tree.dot"), """
                    digraph G {
                        %s
                    }%n
                    """.formatted(sb.toString()).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class NodeId {
        int id;

        NodeId(int id) {
            this.id = id;
        }
    }
}
