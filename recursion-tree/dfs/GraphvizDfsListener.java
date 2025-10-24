package dfs;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphvizDfsListener implements RecursionListener {
    private final MutableGraph graph = mutGraph("DFS").setDirected(true);
    private final Map<Integer, MutableNode> nodes = new HashMap<>();

    @Override
    public void onCall(int value, Integer parentId, int callId) {
        MutableNode node = mutNode("node_" + callId)
                .add(Label.of("Node " + value));
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
