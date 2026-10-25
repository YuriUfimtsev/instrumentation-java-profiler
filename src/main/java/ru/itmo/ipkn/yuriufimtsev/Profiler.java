package ru.itmo.ipkn.yuriufimtsev;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Profiler {

    private static final Map<String, MethodStats> methodsStats = new ConcurrentHashMap<>();
    private static final ThreadLocal<Deque<Long>> callStack = ThreadLocal.withInitial(ArrayDeque::new);

    private Profiler() {}

    public static void enter(String methodName) {
        callStack.get().push(System.nanoTime());
    }

    public static void exit(String methodName) {
        long elapsed = System.nanoTime() - callStack.get().pop();
        methodsStats.computeIfAbsent(methodName, k -> new MethodStats()).record(elapsed);
    }

    public static void reset() {
        methodsStats.clear();
    }

    public static void printReport(int topN) {
        List<Map.Entry<String, MethodStats>> entries = new ArrayList<>(methodsStats.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue().totalNanos, a.getValue().totalNanos));

        long maxNanos = entries.isEmpty() ? 1 : entries.getFirst().getValue().totalNanos;

        System.out.println();
        System.out.println("------ Profiler Report (top " + topN + " by total time) ----------");
        System.out.printf("%-40s | %8s | %14s | %14s | %7s%n", "Method", "Calls", "Total (ms)", "Avg (ms)", "% total");
        System.out.println("-".repeat(40) + "-+-" + "-".repeat(8) + "-+-" + "-".repeat(14) + "-+-" + "-".repeat(14) + "-+-" + "-".repeat(7));

        int count = 0;
        for (Map.Entry<String, MethodStats> entry : entries) {
            if (count >= topN) break;
            ++count;
            MethodStats methodStats = entry.getValue();

            double totalMs = methodStats.totalNanos / 1_000_000.0;
            double avgMs = totalMs / methodStats.calls;
            double pct = 100.0 * methodStats.totalNanos / maxNanos;
            System.out.printf("%-40s | %8d | %14.3f | %14.6f | %6.1f%%%n", entry.getKey(), methodStats.calls, totalMs, avgMs, pct);
        }
        System.out.println();
    }

    public static void printReport() {
        printReport(Integer.MAX_VALUE);
    }

    private static class MethodStats {
        long totalNanos;
        long calls;

        synchronized void record(long nanos) {
            totalNanos += nanos;
            calls++;
        }
    }
}
