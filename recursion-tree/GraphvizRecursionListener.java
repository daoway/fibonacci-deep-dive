import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphvizRecursionListener implements RecursionListener {
    private final MutableGraph graph = mutGraph("Fibonacci").setDirected(true);
    private final Map<Integer, MutableNode> nodes = new HashMap<>();

    @Override
    public void onCall(int n, Integer parentId, int callId) {
        MutableNode node = mutNode("fib_" + callId)
                .add(Label.of("fib(" + n + ")"));
        graph.add(node);
        nodes.put(callId, node);

        if (parentId != null && nodes.containsKey(parentId)) {
            nodes.get(parentId).addLink(node);
        }
    }

    public MutableGraph getGraph() {
        return graph;
    }
}
