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

public class StepByStepListener implements RecursionListener {
    private static final int IMAGE_WIDTH = 1200;
    private static final int IMAGE_HEIGHT = 800;
    private final Map<String, MutableNode> nodes = new HashMap<>();
    private MutableGraph graph;
    private int stepCounter = 0;
    public StepByStepListener() {
        graph = mutGraph("DFS").setDirected(true);
        graph.graphAttrs().add("viewport", "1200,800");
        graph.graphAttrs().add("dpi", "96");
        graph.nodeAttrs().add(Shape.CIRCLE);
        graph.nodeAttrs().add("fixedsize", "true");
        graph.nodeAttrs().add("width", "0.4");
        graph.nodeAttrs().add("height", "0.4");
        graph.nodeAttrs().add("fontsize", "12");
    }

    @Override
    public void onCall(int value, String parentId, String callId) {
        MutableNode node = mutNode("node_" + callId)
                .add(Label.of(String.valueOf(value)))
                .add(Shape.CIRCLE)
                .add(Style.FILLED)
                .add(Color.WHITE.fill())
                .add(Color.BLACK)
                .add("fixedsize", "true")
                .add("width", "0.4")
                .add("height", "0.4")
                .add("fontsize", "12");

        graph.add(node);
        nodes.put(callId, node);

        if (parentId != null && nodes.containsKey(parentId)) {
            nodes.get(parentId).addLink(node);
        }

        highlight(callId);
    }

    private void highlight(String activeId) {
        for (MutableNode n : nodes.values()) {
            n.add(Style.FILLED)
                    .add(Color.WHITE.fill())
                    .add(Color.BLACK);
        }

        MutableNode active = nodes.get(activeId);
        if (active != null) {
            active.add(Style.FILLED)
                    .add(Color.rgb("ff6666").fill())
                    .add(Color.BLACK);
        }

        try {
            stepCounter++;
            Graphviz.fromGraph(graph)
                    .width(IMAGE_WIDTH)
                    .height(IMAGE_HEIGHT)
                    .render(Format.PNG)
                    .toFile(new File("step_%02d.png".formatted(stepCounter)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
