import numpy as np

# Parameters from the fitted model
a = 2.7e-9  # Scaling factor
b = 1.600990  # Base of the exponential

# Configurable range for n
start_n = 20    # Starting value of n
end_n = 100      # Ending value of n (inclusive)
step_n = 10     # Step size for n

# Generate n values based on start, end, and step
n_values = np.arange(start_n, end_n + 1, step_n)

# Compute T(n) = a * b^n (in seconds)
t_values = a * b**n_values

# Function to format time with appropriate units
def format_time_with_units(t):
    if t < 60:
        return f"{t:.2e}", "sec"  # Scientific for small times
    elif t < 3600:
        return f"{t/60:.4f}", "min"  # Convert to minutes
    elif t < 86400:
        return f"{t/3600:.4f}", "hr"  # Convert to hours
    elif t < 31536000:
        return f"{t/86400:.4f}", "days"  # Convert to days
    else:
        return f"{t/31536000:.4f}", "yr"  # Convert to years

# Generate LaTeX table code
latex_code = r"""
\begin{table}[h]
\centering
\caption{Estimated execution time of recursive Fibonacci algorithm using the model $T(n) = 2.7 \times 10^{-9} \cdot 1.600990^n$}
\begin{tabular}{|c|c|c|}
\hline
$n$ & $T(n)$ & Unit \\
\hline
"""

# Add rows to the table
for n, t in zip(n_values, t_values):
    formatted_t, unit = format_time_with_units(t)
    latex_code += f"{n} & {formatted_t} & {unit} \\\\ \n\\hline\n"

# Close the table
latex_code += r"""
\end{tabular}
\end{table}
"""

# Print the LaTeX code
print(latex_code)

#Optional: Save to a file
with open('fibonacci_times_table.tex', 'w', encoding='utf-8') as f:
    f.write(latex_code)
print("LaTeX code saved to 'fibonacci_times_table.tex'")
