public class MatrixFibonacci implements FibonacciSequence<Long> {
    @Override
    public Long fib(int n) {
        if (n <= 0) {
            return 0L;
        }
        long[][] matrix = {{1, 1}, {1, 0}};
        long[][] result = matrixPower(matrix, n - 1);
        return result[0][0];
    }

    private long[][] matrixPower(long[][] matrix, int n) {
        long[][] result = {{1, 0}, {0, 1}};
        while (n > 0) {
            if (n % 2 == 1) {
                result = matrixMultiply(result, matrix);
            }
            matrix = matrixMultiply(matrix, matrix);
            n /= 2;
        }
        return result;
    }

    private long[][] matrixMultiply(long[][] a, long[][] b) {
        long[][] result = new long[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
}
