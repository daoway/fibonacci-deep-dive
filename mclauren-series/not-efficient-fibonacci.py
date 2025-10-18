import sympy as sp

# Символьна змінна
x = sp.symbols('x')

# Генеруюча функція Фібоначчі: F(x) = x / (1 - x - x^2)
F = x / (1 - x - x**2)

# 5-та похідна
F5_deriv = sp.diff(F, x, 5)

# Підставляємо x=0
F5_at_0 = F5_deriv.subs(x, 0)

# Ділимо на 5!
F5_value = F5_at_0 / sp.factorial(5)

print("F(x) =", F)
print("F⁽⁵⁾(x) =", F5_deriv)
print("F⁽⁵⁾(0) =", F5_at_0)
print("F⁽⁵⁾(0)/5! =", F5_value)
