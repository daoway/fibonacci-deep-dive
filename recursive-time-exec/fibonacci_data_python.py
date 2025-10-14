import csv
import time

def fibonacci_recursive(n):
    """Naive recursive Fibonacci."""
    if n <= 1:
        return n
    return fibonacci_recursive(n - 1) + fibonacci_recursive(n - 2)


def main():
    filename = "fibonacci_data_python.csv"
    last_n = 50

    with open(filename, mode="w", newline="") as file:
        writer = csv.writer(file)
        # Write the header
        writer.writerow(["n", "Fn", "time_sec"])

        for n in range(1, last_n + 1):
            start_time = time.perf_counter()  # high-resolution timer
            result = fibonacci_recursive(n)
            end_time = time.perf_counter()

            duration_sec = end_time - start_time
            writer.writerow([n, result, f"{duration_sec:.10f}"])
            print(f"F({n}) calculated in {duration_sec:.4f} sec.")

if __name__ == "__main__":
    main()
