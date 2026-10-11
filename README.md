# SEPM-001 — Lab Thread Counter

**Title:** Comparison of Thread Counting Using Static and Non-Static Variables  
**Language:** Java  
**Class/file:** `Shahrier_Thread.java`

## 1. Objective

To compare a shared `static` counter with per-object (non-static) counters in a multithreaded Java program, and to investigate lost updates caused by unsynchronized access to shared mutable state.

## 2. Concepts

- **Static variable:** belongs to the class and is shared by all instances of that class.
- **Instance variable:** belongs to one object. Each worker object in this program has its own `instanceCounter`.
- **Thread safety:** `AtomicLong.incrementAndGet()` performs an atomic update, so concurrent increments are not lost.
- **Race condition:** `unsafeStaticCounter++` is not atomic. Two threads may read the same old value and overwrite each other's updates.
- **`join()`:** makes the main thread wait for each worker to finish before calculating final results.

## 3. Experiments

- **Experiment A (`true`):** uses `AtomicLong` for the shared static counter.
- **Experiment B (`false`):** uses an ordinary, unsynchronized `static long` to demonstrate possible lost increments.
- Every worker object increments its own instance counter exactly `K` times. The program sums these instance counters after all threads finish.

Expected total: `E = N × K`

Absolute difference: `D = |S − I|`

Percentage difference relative to the non-static total:

`P = (|S − I| / I) × 100`

If `I = 0`, the percentage is `0%` when both totals are zero; otherwise it is undefined.

## 4. Compile and run (Windows)

Open Command Prompt or PowerShell in this directory.

```bash
javac Shahrier_Thread.java
```

Run the thread-safe experiment:

```bash
java Shahrier_Thread 10 50000 true
```

Run the unsafe experiment:

```bash
java Shahrier_Thread 10 50000 false
```

The arguments are:

1. Number of threads `N`
2. Increments per thread `K`
3. `true` for thread-safe mode or `false` for unsafe mode

## 5. Required test cases

Run **both modes** for every row. For the unsafe mode, run each test case at least five times and record each actual output in `results.csv` or your own spreadsheet.

| Test | Threads N | Increments/thread K | Expected N × K |
|---|---:|---:|---:|
| TC1 | 1 | 1,000 | 1,000 |
| TC2 | 2 | 10,000 | 20,000 |
| TC3 | 5 | 10,000 | 50,000 |
| TC4 | 10 | 50,000 | 500,000 |
| TC5 | 20 | 50,000 | 1,000,000 |
| TC6 | 50 | 50,000 | 2,500,000 |
| TC7 | 100 | 50,000 | 5,000,000 |

Example commands for TC1:

```bash
java Shahrier_Thread 1 1000 true
java Shahrier_Thread 1 1000 false
```

Change the arguments for each test case using the table above.

## 6. Result recording

For each run, record the **actual** static count and non-static total printed by the program. Do not invent or edit output values. The thread-safe mode should produce equal totals and a 0% difference. The unsafe mode may or may not lose increments on a particular run; even if one run has no loss, record it honestly.

The unsafe static count can vary between repeated runs because thread scheduling and runtime conditions vary. The percentage difference is not guaranteed to rise steadily as thread count rises.

## 7. Observation and conclusion

Complete this section after running the experiments. Use your own results and describe:

- Whether the thread-safe static count matched the summed instance counters in all tests.
- Whether the unsafe static count was lower than the expected total.
- How results changed across thread counts and repeated runs.
- Why `static` describes ownership, not thread safety.
- Why `AtomicLong` prevents lost increments while ordinary `long++` does not.

**Conclusion template (edit to match your actual observations):**

“The experiment demonstrated that a static variable is shared by all objects, whereas each object has its own instance variable. In the thread-safe experiment, `AtomicLong` preserved all shared-counter increments, so the static total matched the summed instance total. In the unsafe experiment, concurrent updates could be lost because `long++` is not atomic. The amount of loss varied between runs and was not guaranteed to increase monotonically with thread count.”

Do not claim that lost updates occurred in a particular test unless your recorded output demonstrates it.

## 8. Analysis questions

1. What is the difference between a static variable and a non-static variable in Java?
2. Why do all threads share the same static counter?
3. Why does each thread have its own non-static counter in this experiment?
4. Why is `join()` required before calculating the final counts?
5. Why can the unsynchronized static count be lower than the expected count?
6. Does increasing the number of threads always increase the percentage difference? Explain using your observations.
7. Why might two runs with the same number of threads produce different results?
8. What changes when `AtomicLong` is used instead of a regular `long`?
9. How would you modify the program so that all threads share one instance counter as well as the static counter?

## 9. Submission checklist

- [ ] Put this folder in the existing GitHub repository as `Lab_Thread_Counter/`.
- [ ] Run and verify both modes for all seven test cases.
- [ ] Run each unsafe test case at least five times.
- [ ] Save actual terminal screenshots in `screenshots/`.
- [ ] Write the source code by hand, then scan/photograph it clearly into `handwritten_code/`.
- [ ] Fill in the result sheet using actual results.
- [ ] Update the observation and conclusion to match the evidence.
- [ ] Export the complete report as a PDF into `report/`.
- [ ] Submit the GitHub repository URL, directory path `Lab_Thread_Counter`, and PDF report through Canvas.

## 10. Important

The Java source and this README are starter materials. The results, screenshots, handwritten source, and final conclusions must come from your own execution and work. Do not submit fabricated output.
