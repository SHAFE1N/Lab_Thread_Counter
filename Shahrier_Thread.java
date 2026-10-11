import java.util.concurrent.atomic.AtomicLong;

/**
 * SEPM-001: Comparison of Static and Non-Static Variables in Multithreading.
 *
 * Usage:
 *   javac Shahrier_Thread.java
 *   java Shahrier_Thread <threads> <incrementsPerThread> <true|false>
 *
 * true  = thread-safe static counter using AtomicLong
 * false = ordinary unsynchronized static long (race-condition demonstration)
 */
public class Shahrier_Thread extends Thread {
    private static final AtomicLong safeStaticCounter = new AtomicLong(0);
    private static long unsafeStaticCounter = 0;

    private final boolean threadSafe;
    private final long incrementsPerThread;
    private long instanceCounter = 0;

    private Shahrier_Thread(long incrementsPerThread, boolean threadSafe) {
        this.incrementsPerThread = incrementsPerThread;
        this.threadSafe = threadSafe;
    }

    @Override
    public void run() {
        for (long i = 0; i < incrementsPerThread; i++) {
            if (threadSafe) {
                safeStaticCounter.incrementAndGet();
            } else {
                // Intentionally unsafe: ++ is a read-modify-write operation.
                unsafeStaticCounter++;
            }
            instanceCounter++;
        }
    }

    public long getInstanceCounter() {
        return instanceCounter;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            printUsage();
            return;
        }

        final int numberOfThreads;
        final long incrementsPerThread;
        final boolean threadSafe;

        try {
            numberOfThreads = Integer.parseInt(args[0]);
            incrementsPerThread = Long.parseLong(args[1]);
            if (!args[2].equalsIgnoreCase("true")
                    && !args[2].equalsIgnoreCase("false")) {
                printUsage();
                return;
            }
            threadSafe = Boolean.parseBoolean(args[2]);
        } catch (NumberFormatException ex) {
            System.out.println("Error: thread count and increments must be integers.");
            printUsage();
            return;
        }

        if (numberOfThreads < 1 || incrementsPerThread < 0) {
            System.out.println("Error: threads must be >= 1 and increments must be >= 0.");
            return;
        }

        safeStaticCounter.set(0);
        unsafeStaticCounter = 0;

        Shahrier_Thread[] threads = new Shahrier_Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Shahrier_Thread(incrementsPerThread, threadSafe);
            threads[i].start();
        }

        // Wait for every worker to finish before reading final totals.
        for (Shahrier_Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread interrupted; results may be incomplete.");
                return;
            }
        }

        long expected = (long) numberOfThreads * incrementsPerThread;
        long staticCount = threadSafe
                ? safeStaticCounter.get()
                : unsafeStaticCounter;

        long nonStaticTotal = 0;
        for (Shahrier_Thread thread : threads) {
            nonStaticTotal += thread.getInstanceCounter();
        }

        long difference = Math.abs(staticCount - nonStaticTotal);
        String percentage;
        if (nonStaticTotal == 0) {
            percentage = (staticCount == 0) ? "0.00%" : "undefined (non-static total is zero)";
        } else {
            double p = (double) difference / nonStaticTotal * 100.0;
            percentage = String.format(java.util.Locale.US, "%.2f%%", p);
        }

        System.out.println("========== THREAD COUNTER LAB ==========");
        System.out.println("Mode: " + (threadSafe
                ? "Thread-safe (AtomicLong)"
                : "Unsafe (ordinary static long)"));
        System.out.println("Threads (N): " + numberOfThreads);
        System.out.println("Increments per thread (K): " + incrementsPerThread);
        System.out.println("Expected count (N x K): " + expected);
        System.out.println("Static count: " + staticCount);
        System.out.println("Non-static total: " + nonStaticTotal);
        System.out.println("Absolute difference: " + difference);
        System.out.println("Percentage difference: " + percentage);
        System.out.println("========================================");
    }

    private static void printUsage() {
        System.out.println("Usage: java Shahrier_Thread <threads> <incrementsPerThread> <true|false>");
        System.out.println("Example (safe): java Shahrier_Thread 10 50000 true");
        System.out.println("Example (unsafe): java Shahrier_Thread 10 50000 false");
    }
}
