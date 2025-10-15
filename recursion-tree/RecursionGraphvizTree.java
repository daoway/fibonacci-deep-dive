import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class RecursionGraphvizTree {

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    public static int fib(int n, MutableGraph graph, MutableNode parent) {
        int myId = idCounter.getAndIncrement();

        String nodeName = "fib" + myId;
        Label nodeLabel = Label.of("fib(" + n + ")");

        MutableNode myNode = mutNode(nodeName).add(nodeLabel);
        graph.add(myNode);

        if (parent != null) {
            parent.addLink(myNode);
        }

        int result;
        if (n == 0) {
            result = 0;
        } else if (n == 1) {
            result = 1;
        } else {
            int left = fib(n - 1, graph, myNode);
            int right = fib(n - 2, graph, myNode);
            result = left + right;
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        MutableGraph graph = mutGraph("Fibonacci").setDirected(true);
        fib(5, graph, null);
        Graphviz.fromGraph(graph)
                .render(Format.DOT)
                .toFile(new File("fib_tree.dot"));
        Graphviz.fromGraph(graph)
                .render(Format.PNG)
                .toFile(new File("fib_tree.png"));
    }
}
