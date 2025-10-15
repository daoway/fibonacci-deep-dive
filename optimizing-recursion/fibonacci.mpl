with(combinat, fibonacci);

fibonacci_numbers := seq(fibonacci(i), i = 0 .. 100000):

f := fopen("fibonacci_numbers.txt", WRITE):

for num in fibonacci_numbers do
    fprintf(f, "%a\n", num);
end do:

fclose(f);
