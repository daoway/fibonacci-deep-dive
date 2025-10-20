import sympy as sp

x = sp.symbols('x')
F = x / (1 - x - x**2)

# 5-та похідна
F5_deriv = sp.diff(F, x, 5)

# Отримуємо LaTeX-подання
F5_latex = sp.latex(F5_deriv)

print("F^(5)(x) =")
print(F5_latex)
