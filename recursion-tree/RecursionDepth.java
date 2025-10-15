public class RecursionDepth {
    private static int depth = 0;

    public static void recurse() {
        depth++;
        recurse();
    }

    public static void main(String[] args) {
        try {
            recurse();
        } catch (StackOverflowError e) {
            System.out.println("Max recursion depth: " + depth);
        }
    }
}
