package dfs;

public class DfsStepDemo {
    public static void main(String[] args) {
        // будуємо тестове дерево
        TreeNode root = new TreeNode(1);
        TreeNode a = new TreeNode(2);
        TreeNode b = new TreeNode(3);
        TreeNode c = new TreeNode(4);
        TreeNode d = new TreeNode(5);
        root.children.add(a);
        root.children.add(b);
        a.children.add(c);
        b.children.add(d);

        StepByStepGraphvizListener listener = new StepByStepGraphvizListener();
        DfsTraversal dfs = new DfsTraversal(listener);
        dfs.dfs(root, null);

        System.out.println("✅ DFS visualization frames generated.");
    }
}
