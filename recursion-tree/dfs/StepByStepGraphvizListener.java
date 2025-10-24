package dfs;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class StepByStepGraphvizListener implements RecursionListener {
    private final MutableGraph graph = mutGraph("DFS").setDirected(true);
    private final Map<Integer, MutableNode> nodes = new HashMap<>();
    private int stepCounter = 0;

    @Override
    public void onCall(int value, Integer parentId, int callId) {
        // Створюємо вузол, якщо його ще не було
        MutableNode node = mutNode("node_" + callId)
                .add(Label.of("Node " + value))
                .add(Color.BLACK);
        graph.add(node);
        nodes.put(callId, node);

        // Додаємо ребро, якщо є батько
        if (parentId != null && nodes.containsKey(parentId)) {
            nodes.get(parentId).addLink(node);
        }

        // Підсвічуємо цей вузол
        highlight(callId);
    }

    private void highlight(int activeId) {
        // Скидаємо колір усіх вузлів
        for (MutableNode n : nodes.values()) {
            n.add(Color.BLACK);
            n.add(Style.FILLED);
            n.add(Color.rgb("FFFFFF")); // білий фон
        }

        // Підсвічуємо активний
        MutableNode active = nodes.get(activeId);
        if (active != null) {
            active.add(Color.rgb("ff6666")); // червоний
        }

        // Рендеримо крок
        try {
            stepCounter++;
            Graphviz.fromGraph(graph)
                    .scale(2.0)
                    .render(Format.PNG)
                    .toFile(new File(String.format("dfs_step_%02d.png", stepCounter)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MutableGraph getGraph() {
        return graph;
    }
}
