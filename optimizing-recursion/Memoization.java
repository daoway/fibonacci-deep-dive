import java.util.HashMap;
import java.util.Map;

public class Memoization {
    private static final Map<Integer, Integer> memo = new HashMap<>();

    public static int fibMemo(int n) {
        if (n <= 1) {
            return n;
        }
        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        int result = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, result);
        return result;
    }

}
