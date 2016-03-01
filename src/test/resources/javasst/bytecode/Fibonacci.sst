class Fibonacci {

    public int fibonacci(int n)
    {
        if (n <= 0)
        {
            return 0;
        } else {
            return fibonacci(n,1,0);
        }
    }

    public int fibonacci(int n, int eax, int ebx)
    {
        n = n - 1;

        if (n == 0)
        {
            return eax+ebx;
        } else {
            return fibonacci(n,ebx,eax+ebx);
        }
    }
}
