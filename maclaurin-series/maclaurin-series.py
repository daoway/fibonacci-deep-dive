import sympy as sp

# Define symbol and generating function
x = sp.symbols('x')
F = x / (1 - x - x ** 2)


def fib_from_derivative(n: int) -> int:
    Fn_deriv = sp.diff(F, x, n)  # Take n-th derivative
    Fn_at_0 = Fn_deriv.subs(x, 0)
    Fn_value = Fn_at_0 / sp.factorial(n)
    return int(Fn_value.evalf())


print(f"{'n':>3} | {'Maclaurin (deriv)':>20} | {'Iterative':>12}")
print("-" * 42)

for n in range(1, 21):
    fib_sym = fib_from_derivative(n)
    fib_num = int(sp.fibonacci(n).evalf())  # library function
    print(f"{n:3d} | {fib_sym:20d} | {fib_num:12d}")
