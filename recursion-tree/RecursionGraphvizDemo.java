import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Format;

import java.io.File;
import java.io.IOException;

public class RecursionGraphvizDemo {
    public static void main(String[] args) throws IOException {
        GraphvizRecursionListener listener = new GraphvizRecursionListener();
        FibonacciRecursive fib = new FibonacciRecursive(listener);

        fib.fib(5, null);

        Graphviz.fromGraph(listener.getGraph())
                .scale(3.0)
                .render(Format.PNG)
                .toFile(new File("fib_tree_clean.png"));
    }
}
