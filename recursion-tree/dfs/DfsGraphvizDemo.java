package dfs;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import java.io.File;
import java.io.IOException;

public class DfsGraphvizDemo {
    public static void main(String[] args) throws IOException {
        // Створюємо дерево вручну
        TreeNode root = new TreeNode(1);
        TreeNode a = new TreeNode(2);
        TreeNode b = new TreeNode(3);
        TreeNode c = new TreeNode(4);
        root.children.add(a);
        root.children.add(b);
        a.children.add(c);

        // Підключаємо Graphviz listener
        GraphvizDfsListener listener = new GraphvizDfsListener();
        DfsTraversal dfs = new DfsTraversal(listener);

        // Запускаємо DFS
        dfs.dfs(root, null);

        // Візуалізуємо
        Graphviz.fromGraph(listener.getGraph())
                .scale(2.5)
                .render(Format.PNG)
                .toFile(new File("dfs_tree.png"));
    }
}
