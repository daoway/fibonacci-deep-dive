import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

# Define the exponential model function
def exponential_model(n, a, b):
    return a * b**n

# Read data from CSV file
data = pd.read_csv('fibonacci_data.csv')

# Extract n and time_sec columns
n = data['n'].values
time_sec = data['time_sec'].values

# Fit the exponential model
popt, pcov = curve_fit(exponential_model, n, time_sec, p0=[1e-6, 1.618])  # Initial guess: a=1e-6, b=1.618
a, b = popt
print(f"Fitted model: T(n) = {a:.10f} * {b:.6f}^n")

# Compute predicted time values
predicted_time = exponential_model(n, a, b)

# Plot the results
plt.figure(figsize=(10, 6))
plt.scatter(n, time_sec, color='blue', label='Experimental data')
plt.plot(n, predicted_time, color='red', label=f'Model: T(n) = {a:.2e} * {b:.6f}^n')
plt.xlabel('n')
plt.ylabel('Execution time (sec)')
plt.title('Execution time of recursive Fibonacci algorithm')
plt.yscale('log')  # Log scale for better visualization of exponential growth
plt.legend()
plt.grid(True)
plt.savefig("experimental-data-formula.png")
plt.show()

# Compare b to the golden ratio
phi = (1 + np.sqrt(5)) / 2
print(f"Golden ratio (φ): {phi:.6f}")
print(f"Deviation of b from φ: {abs(b - phi):.6f}")
