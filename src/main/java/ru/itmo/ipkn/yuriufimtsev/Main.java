package ru.itmo.ipkn.yuriufimtsev;

import java.util.Random;

public class Main {

    private static final Random RANDOM = new Random(42);

    public static void main(String[] args) {
        System.out.println("Starting profiled workload...\n");

        Profiler.enter("main");

        bubbleSort();
        heavyLoop();
        repeatedCompute();
        frequentTiny();

        Profiler.exit("main");

        Profiler.printReport(10);
    }

    private static void bubbleSort() {
        Profiler.enter("bubbleSort");

        int[] array = new int[15_000];
        for (int i = 0; i < array.length; i++) {
            array[i] = RANDOM.nextInt();
        }

        Profiler.enter("bubbleSort.innerSort");
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        }
        Profiler.exit("bubbleSort.innerSort");

        Profiler.exit("bubbleSort");
    }

    private static void heavyLoop() {
        Profiler.enter("heavyLoop");

        double sum = 0;
        for (int i = 1; i <= 20_000_000; i++) {
            sum += Math.sin(i) * Math.cos(i) * Math.log(i + 1);
        }
        if (sum == Double.NaN) System.out.println(sum);

        Profiler.exit("heavyLoop");
    }

    private static void repeatedCompute() {
        Profiler.enter("repeatedCompute");

        for (int i = 0; i < 5; i++) {
            Profiler.enter("repeatedCompute.fibonacci");
            long result = fibonacci(38);
            Profiler.exit("repeatedCompute.fibonacci");
        }

        Profiler.exit("repeatedCompute");
    }

    private static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    private static void frequentTiny() {
        Profiler.enter("frequentTiny");

        long accumulator = 0;
        for (int i = 0; i < 5_000_000; i++) {
            accumulator += tinyFunction(i);
        }
        if (accumulator == Long.MIN_VALUE) System.out.println(accumulator);

        Profiler.exit("frequentTiny");
    }

    private static long tinyFunction(int x) {
        Profiler.enter("tinyFunction");
        long result = (long) x * x + 1;
        Profiler.exit("tinyFunction");
        return result;
    }
}
