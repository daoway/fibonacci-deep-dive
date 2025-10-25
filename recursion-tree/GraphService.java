import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class GraphService {
    private static final int IMAGE_WIDTH = 800;
    private static final int IMAGE_HEIGHT = 600;

    private final MutableGraph graph;
    private final Map<String, MutableNode> nodes = new HashMap<>();
    private int stepCounter = 0;

    public GraphService() {
        this.graph = createGraph();
    }

    private MutableGraph createGraph() {
        MutableGraph g = mutGraph("RecursionGraph").setDirected(true);
        g.graphAttrs()
                .add("viewport", "%s,%s".formatted(IMAGE_WIDTH, IMAGE_HEIGHT));
        g.graphAttrs().add("dpi", "96");
        g.nodeAttrs().add(Shape.CIRCLE);
        g.nodeAttrs().add("fixedsize", "true");
        g.nodeAttrs().add("width", "0.8");
        g.nodeAttrs().add("height", "0.8");
        g.nodeAttrs().add("fontsize", "14");
        return g;
    }

    public void addNode(int value, String parentId, String callId) {
        MutableNode node = createNode(value, callId);
        graph.add(node);
        nodes.put(callId, node);

        if (parentId != null && nodes.containsKey(parentId)) {
            nodes.get(parentId).addLink(node);
        }
    }

    private MutableNode createNode(int value, String callId) {
        return mutNode("node_" + callId)
                .add(Label.of(String.valueOf(value)))
                .add(Shape.CIRCLE)
                .add(Style.FILLED)
                .add(Color.WHITE.fill())
                .add(Color.BLACK);
    }

    public void highlight(String activeId) {
        // Reset colors
        for (MutableNode n : nodes.values()) {
            n.add(Style.FILLED)
                    .add(Color.WHITE.fill())
                    .add(Color.BLACK);
        }

        // Highlight active node
        MutableNode active = nodes.get(activeId);
        if (active != null) {
            active.add(Style.FILLED)
                    .add(Color.rgb("ff6666").fill())
                    .add(Color.BLACK);
        }
    }

    public void saveStepImage() {
        try {
            stepCounter++;
            Graphviz.fromGraph(graph)
                    .width(IMAGE_WIDTH)
                    .height(IMAGE_HEIGHT)
                    .render(Format.PNG)
                    .toFile(new File("step_%02d.png".formatted(stepCounter)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
