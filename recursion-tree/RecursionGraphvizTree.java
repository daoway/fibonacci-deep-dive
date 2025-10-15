import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class RecursionGraphvizTree {

    private static int idCounter = 0;

    public static int fib(int n, MutableGraph graph, MutableNode parent) {
        int myId = idCounter++;

        MutableNode myNode =
                mutNode("fib" + myId).add(Label.of("fib(" + n + ")"));
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
        MutableGraph g = mutGraph("Fibonacci").setDirected(true);
        fib(5, g, null);

        Graphviz.fromGraph(g)
                .scale(3.0)
                .render(Format.PNG)
                .toFile(new File("fib_tree.png"));

        Graphviz.fromGraph(g)
                .render(Format.DOT)
                .toFile(new File("fib_tree.dot"));

        Graphviz.fromGraph(g)
                .scale(3.0)
                .render(Format.SVG)
                .toFile(new File("fib_tree.svg"));
    }
}
