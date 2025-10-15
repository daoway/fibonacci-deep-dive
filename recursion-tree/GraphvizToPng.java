import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class GraphvizToPng {

    public static void main(String[] args) throws IOException {
        // 1. Створення графа в Java (як альтернатива завантаженню DOT-файлу)
        renderFromJavaApi();

        // 2. Рендеринг існуючого DOT-файлу
        // renderFromDotFile("path/to/your/input.dot", "path/to/output.png");
    }

    /**
     * Створює простий граф і рендерить його у PNG.
     */
    public static void renderFromJavaApi() throws IOException {
        Graph g = graph("example1").directed()
                .with(node("a").link(node("b")));

        Graphviz.fromGraph(g)
                .render(Format.PNG)
                .toFile(new File("output_api.png"));

        System.out.println("Файл output_api.png створено!");
    }

    /**
     * Завантажує DOT-файл і рендерить його у PNG.
     * * @param dotFilePath Шлях до вхідного .dot файлу
     * @param pngFilePath Шлях до вихідного .png файлу
     */
    public static void renderFromDotFile(String dotFilePath, String pngFilePath) throws IOException {
        MutableGraph g = new Parser().read(new File(dotFilePath));

        Graphviz.fromGraph(g)
                .render(Format.PNG)
                .toFile(new File(pngFilePath));

        System.out.println("Файл " + pngFilePath + " створено!");
    }
}