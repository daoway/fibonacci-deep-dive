import sympy as sp

x = sp.symbols('x')
F = x / (1 - x - x**2)
F5_deriv = sp.diff(F, x, 5)
F5_at_0 = F5_deriv.subs(x, 0)
F5_value = F5_at_0 / sp.factorial(5)

print("F(x) =", F)
print("F⁽⁵⁾(x) =", F5_deriv)
print("F⁽⁵⁾(0) =", F5_at_0)
print("F⁽⁵⁾(0)/5! =", F5_value)
