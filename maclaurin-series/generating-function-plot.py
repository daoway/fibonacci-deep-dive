import numpy as np
import matplotlib.pyplot as plt

# Define x values in (0,1)
x = np.linspace(0.001, 0.999, 1000)  # не включаємо 0 і 1 через полюси

# Define the generating function F(x) = x / (1 - x - x^2)
F = x / (1 - x - x**2)

# Plot
plt.figure(figsize=(8,5))
plt.plot(x, F, color='blue', lw=2)
plt.title(r"Generating function $F(x) = \frac{x}{1 - x - x^2}$ for $x \in (0,1)$")
plt.xlabel("x")
plt.ylabel("F(x)")
plt.grid(True)
plt.show()
