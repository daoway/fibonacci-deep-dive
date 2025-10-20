import pandas as pd
from scipy.optimize import curve_fit


# Define the exponential model function
def exponential_model(n, a, b):
    return a * b ** n


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
