package dfs;

import java.util.UUID;

public class DfsTraversal {
    private final RecursionListener listener;

    public DfsTraversal(RecursionListener listener) {
        this.listener = listener;
    }

    public void dfs(TreeNode node, Integer parentId) {
        if (node == null) return;
        int callId = UUID.randomUUID().hashCode();
        listener.onCall(node.value, parentId, callId);

        for (TreeNode child : node.children) {
            dfs(child, callId);
        }
    }
}
