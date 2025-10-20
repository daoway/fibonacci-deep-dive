import sympy as sp

# Define symbol and generating function
x = sp.symbols('x')
F = x / (1 - x - x ** 2)

def fib_from_derivative(n: int) -> int:
    """
    Compute the n-th Fibonacci number using the Maclaurin series:
        F_n = F^(n)(0) / n!
    where F(x) = x / (1 - x - x^2)
    """
    Fn_deriv = sp.diff(F, x, n)
    Fn_at_0 = Fn_deriv.subs(x, 0)
    Fn_value = Fn_at_0 / sp.factorial(n)
    return int(Fn_value.evalf())

# LaTeX table header
print(r"\begin{tabular}{c|c|c}")
print(r"n & Maclaurin (deriv) & Iterative \\")
print(r"\hline")

# Table rows
for n in range(1, 21):
    fib_sym = fib_from_derivative(n)
    fib_num = int(sp.fibonacci(n).evalf())  # library function
    print(f"{n} & {fib_sym} & {fib_num} \\\\")

print(r"\end{tabular}")
