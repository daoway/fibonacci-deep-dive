import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

# --- 1. Define the filename and check for its existence ---
FILENAME = 'fibonacci_data.csv'

if not os.path.exists(FILENAME):
    print(f"Error: File '{FILENAME}' not found.")
    print("Please run the Java code first to generate the data file.")
    exit()

# --- 2. Read and Prepare Data ---
try:
    # Read the data from the CSV file
    df = pd.read_csv(FILENAME)
except Exception as e:
    print(f"Error reading CSV file: {e}")
    exit()

# Filter out very small execution times (mostly for n < 30) 
# as they introduce noise, focusing the graph on the exponential growth phase.
# We'll keep only data points where time is greater than 1 millisecond (0.001 sec).
df_filtered = df[df['time_sec'] > 0.001].copy()

if df_filtered.empty:
    print("Not enough data points with significant execution time (above 0.001 sec) to plot exponential growth.")
    print("Try increasing the 'last_n' value in your Java code (e.g., to 45).")
    exit()


# --- 3. Plotting the Exponential Growth ---
plt.figure(figsize=(12, 7))

# Plot the actual recursive time
plt.plot(df_filtered['n'], df_filtered['time_sec'], 
         marker='o', linestyle='-', color='red', label='Actual Recursive Time (sec)', linewidth=2)

plt.title('Exponential Time Growth: Naive Recursive Fibonacci (O($\phi^n$))', fontsize=16)
plt.xlabel('Fibonacci Number Index (n)', fontsize=14)
plt.ylabel('Execution Time (seconds)', fontsize=14)
plt.legend(fontsize=12)
plt.grid(True, which='both', linestyle='--', linewidth=0.5)

# Highlight the steep rise for visual emphasis
if len(df_filtered) > 5:
    steep_start_n = df_filtered[df_filtered['time_sec'] > df_filtered['time_sec'].max() * 0.1]['n'].min()
    plt.axvline(x=steep_start_n, color='gray', linestyle=':', linewidth=1.5, 
                label=f'Steep Growth Point (n $\\approx$ {steep_start_n})')
    plt.legend(fontsize=12)


# --- 4. Adding Annotations for Educational Value ---

# Find the last calculated point
last_n_point = df_filtered.iloc[-1]
plt.annotate(
    f'Time: {last_n_point["time_sec"]:.2f} sec\n(n={last_n_point["n"]})',
    xy=(last_n_point['n'], last_n_point['time_sec']),
    xytext=(last_n_point['n'] - 10, last_n_point['time_sec'] * 0.8),
    arrowprops=dict(facecolor='blue', shrink=0.05, width=1, headwidth=8),
    fontsize=11,
    color='blue'
)

# Text explanation on the graph
plt.text(df_filtered['n'].min() + 2, df_filtered['time_sec'].max() * 0.9,
         'O($\phi^n$): Time nearly multiplies by 1.618 with each increment of n.',
         fontsize=12, bbox=dict(facecolor='yellow', alpha=0.5))

plt.savefig("exponential-time-growth.png")
plt.show()
