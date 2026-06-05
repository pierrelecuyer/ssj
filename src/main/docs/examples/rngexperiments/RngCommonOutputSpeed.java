package rngexperiments;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import umontreal.ssj.rng.MWC64k2a2;
import umontreal.ssj.rng.MWC64k3a2;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.LFSR258;

/**
 * Simple output speed benchmark for common random stream methods.
 *
 * Compared generators:
 *
 * MWC64k2a2
 * MWC64k3a2
 * MRG32k3a
 * LFSR258
 * L64X128StarStarRandom
 * L64X128MixRandom
 * L64X256MixRandom
 * L64X1024MixRandom
 *
 * Tested methods:
 *
 * nextDouble()
 * nextDoubleNonzero()
 * nextInt(i, j)
 * nextLong(i, j)
 *
 * The benchmark uses one warmup run followed by timed runs. The generator is
 * reset once before the warmup run, then the timed runs consume consecutive
 * values without resetting.
 *
 * The benchmark avoids generator wrappers, lambdas, reflection, and adapter
 * classes. Each timed loop calls the generator method directly.
 */
public class RngCommonOutputSpeed {

    static final boolean WRITE_FILE = true;

    static final int M = 100_000_000;
    static final String M_LABEL = "1e8";

    static final int WARMUP_RUNS = 1;
    static final int TIMED_RUNS = 5;

    static final int INT_I = 0;
    static final int INT_J = 1_000_000;

    static final long LONG_I = 0L;
    static final long LONG_J = 1_000_000_000_000L;

    static final long JAVA_SEED = 12345L;

    static final String JAVA_L64X128_STARSTAR = "L64X128StarStarRandom";
    static final String JAVA_L64X128_MIX = "L64X128MixRandom";
    static final String JAVA_L64X256_MIX = "L64X256MixRandom";
    static final String JAVA_L64X1024_MIX = "L64X1024MixRandom";

    static double doubleSink = 0.0;
    static long intSink = 0L;
    static long longSink = 0L;

    public static void main(String[] args) throws IOException {
        StringBuilder out = new StringBuilder();
        String output_file = "/home/otman/Documents/GitHub/Data/o-MWC-test/RngCommonOutputSpeed2.res";

        out.append("RNG common output speed benchmark\n");
        out.append("M = ").append(M_LABEL).append(" calls per run (").append(M).append(")\n");
        out.append("Warmup runs = ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded from timing result\n");
        out.append("Timed runs = ").append(TIMED_RUNS).append(" runs of ")
           .append(M_LABEL).append(" calls\n");
        out.append("Average time = average of ").append(TIMED_RUNS)
           .append(" timed runs, each with ").append(M_LABEL).append(" calls\n\n");

        out.append("Generators:\n");
        out.append("- MWC64k2a2\n");
        out.append("- MWC64k3a2\n");
        out.append("- MRG32k3a\n");
        out.append("- LFSR258\n");
        out.append("- L64X128StarStarRandom\n");
        out.append("- L64X128MixRandom\n");
        out.append("- L64X256MixRandom\n");
        out.append("- L64X1024MixRandom\n\n");

        out.append("Benchmark notes:\n");
        out.append("- Each generator is reset once before warmup. Timed runs continue from the state reached after warmup.\n");
        out.append("- The mean is computed over ").append(TIMED_RUNS).append(" * ")
           .append(M_LABEL).append(" timed values; warmup values are excluded.\n");
        out.append("- MWC nextDoubleNonzero() tests use the RandomStream default method that rejects 0.0.\n");
        out.append("- nextInt(i, j) and nextLong(i, j) measure bounded public API methods, including range conversion.\n");
        out.append("- These are not raw int or raw 64-bit output speed tests.\n");
        out.append("- Final sink values are checksums used to prevent dead-code elimination. Signed overflow is possible for longSink.\n");
        out.append("- Java LXM generators are benchmarked through the public java.util.random.RandomGenerator API.\n\n");

        out.append("Method conventions:\n");
        out.append("- MWC64k2a2 and MWC64k3a2 nextDouble(): values are in [0, 1); 0.0 is possible and 1.0 is excluded.\n");
        out.append("- MWC64k2a2 and MWC64k3a2 nextDouble(): the top 53 bits of the 64-bit output are multiplied by 2^(-53).\n");
        out.append("- MWC64k2a2 and MWC64k3a2 nextDoubleNonzero(): values are in (0, 1); 0.0 is rejected.\n");
        out.append("- MRG32k3a and LFSR258 nextDouble(): standard SSJ implementations return values in (0, 1).\n");
        out.append("- Java LXM nextDouble(): values are in [0, 1); 0.0 is possible and 1.0 is excluded.\n");
        out.append("- SSJ-style nextInt(i, j) and nextLong(i, j): inclusive bounds [i, j].\n");
        out.append("- MWC64k2a2 and MWC64k3a2 nextLong(i, j): use nextNumber() directly with rejection sampling.\n");
        out.append("- Java nextInt(origin, bound) and nextLong(origin, bound): exclusive upper bound [origin, bound).\n");
        out.append("- For Java integer and long ranges, this benchmark uses j + 1 as the exclusive upper bound.\n\n");

        out.append("============================================================\n");
        out.append("nextDouble() and nextDoubleNonzero() speed\n");
        out.append("Mean is computed over timed values only. Expected mean is close to 0.5.\n");
        out.append("============================================================\n");

        benchmarkMRG32k3aNextDouble(out);
        benchmarkLFSR258NextDouble(out);
        benchmarkMWC64k2a2NextDouble(out);
        benchmarkMWC64k2a2NextDoubleNonzero(out);
        benchmarkMWC64k3a2NextDouble(out);
        benchmarkMWC64k3a2NextDoubleNonzero(out);
        benchmarkL64X128StarStarRandomNextDouble(out);
        benchmarkL64X128MixRandomNextDouble(out);
        benchmarkL64X256MixRandomNextDouble(out);
        benchmarkL64X1024MixRandomNextDouble(out);

        out.append("\n");
        out.append("============================================================\n");
        out.append("nextInt(i, j) speed\n");
        out.append("SSJ-style range: [").append(INT_I).append(", ").append(INT_J).append("]\n");
        out.append("Java range: [").append(INT_I).append(", ").append(INT_J + 1).append(")\n");
        out.append("Mean is computed over timed values only. Expected mean is close to ")
        .append(String.format(Locale.US, "%.0f", (INT_I + INT_J) / 2.0)).append(".\n");
        out.append("============================================================\n");
        benchmarkMWC64k2a2NextInt(out);
        benchmarkMWC64k3a2NextInt(out);
        benchmarkMRG32k3aNextInt(out);
        benchmarkLFSR258NextInt(out);
        benchmarkL64X128StarStarRandomNextInt(out);
        benchmarkL64X128MixRandomNextInt(out);
        benchmarkL64X256MixRandomNextInt(out);
        benchmarkL64X1024MixRandomNextInt(out);

        out.append("\n");
        out.append("============================================================\n");
        out.append("nextLong(i, j) speed\n");
        out.append("SSJ-style range: [").append(LONG_I).append(", ").append(LONG_J).append("]\n");
        out.append("Java range: [").append(LONG_I).append(", ").append(LONG_J + 1L).append(")\n");
        out.append("Mean is computed over timed values only. Expected mean is close to ")
        .append(String.format(Locale.US, "%.0f", (LONG_I + LONG_J) / 2.0)).append(".\n");
        out.append("============================================================\n");
        benchmarkMWC64k2a2NextLong(out);
        benchmarkMWC64k3a2NextLong(out);
        benchmarkMRG32k3aNextLong(out);
        benchmarkLFSR258NextLong(out);
        benchmarkL64X128StarStarRandomNextLong(out);
        benchmarkL64X128MixRandomNextLong(out);
        benchmarkL64X256MixRandomNextLong(out);
        benchmarkL64X1024MixRandomNextLong(out);

        out.append("\n");
        out.append("Final checksums, used to prevent dead-code elimination:\n");
        out.append("doubleSink = ").append(doubleSink).append("\n");
        out.append("intSink    = ").append(intSink).append("\n");
        out.append("longSink   = ").append(longSink).append("\n");

        System.out.println(out);

        if (WRITE_FILE) {
            FileWriter writer = new FileWriter(output_file);
            writer.write(out.toString());
            writer.close();
        }
    }

    private static String formatTime(double seconds) {
        return String.format(Locale.US, "%.4f", seconds);
    }

    private static String formatMean(double mean) {
        return String.format(Locale.US, "%.6f", mean);
    }

    private static void benchmarkMWC64k2a2NextDouble(StringBuilder out) {
        out.append("\nMWC64k2a2 nextDouble()\n");
        out.append("Convention: values are in [0, 1); 0.0 is possible and 1.0 is excluded.\n");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k2a2NextDoubleNonzero(StringBuilder out) {
        out.append("\nMWC64k2a2 nextDoubleNonzero()\n");
        out.append("Convention: values are in (0, 1); 0.0 is rejected by nextDoubleNonzero().\n");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDoubleNonzero();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDoubleNonzero();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k3a2NextDouble(StringBuilder out) {
        out.append("\nMWC64k3a2 nextDouble()\n");
        out.append("Convention: values are in [0, 1); 0.0 is possible and 1.0 is excluded.\n");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k3a2NextDoubleNonzero(StringBuilder out) {
        out.append("\nMWC64k3a2 nextDoubleNonzero()\n");
        out.append("Convention: values are in (0, 1); 0.0 is rejected by nextDoubleNonzero().\n");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDoubleNonzero();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDoubleNonzero();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMRG32k3aNextDouble(StringBuilder out) {
        out.append("\nMRG32k3a nextDouble()\n");
        out.append("Convention: standard SSJ implementation returns values in (0, 1).\n");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkLFSR258NextDouble(StringBuilder out) {
        out.append("\nLFSR258 nextDouble()\n");
        out.append("Convention: standard SSJ implementation returns values in (0, 1).\n");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X128StarStarRandomNextDouble(StringBuilder out) {
        out.append("\nL64X128StarStarRandom nextDouble()\n");
        out.append("Convention: java.util.random nextDouble() returns values in [0, 1).\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_STARSTAR);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X128MixRandomNextDouble(StringBuilder out) {
        out.append("\nL64X128MixRandom nextDouble()\n");
        out.append("Convention: java.util.random nextDouble() returns values in [0, 1).\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X256MixRandomNextDouble(StringBuilder out) {
        out.append("\nL64X256MixRandom nextDouble()\n");
        out.append("Convention: java.util.random nextDouble() returns values in [0, 1).\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X256_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X1024MixRandomNextDouble(StringBuilder out) {
        out.append("\nL64X1024MixRandom nextDouble()\n");
        out.append("Convention: java.util.random nextDouble() returns values in [0, 1).\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X1024_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sum = 0.0;
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            doubleSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextDouble();
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            doubleSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k2a2NextInt(StringBuilder out) {
        out.append("\nMWC64k2a2 nextInt(i, j)\n");
        out.append("Convention: inclusive bounds [").append(INT_I).append(", ").append(INT_J).append("].\n");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k3a2NextInt(StringBuilder out) {
        out.append("\nMWC64k3a2 nextInt(i, j)\n");
        out.append("Convention: inclusive bounds [").append(INT_I).append(", ").append(INT_J).append("].\n");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMRG32k3aNextInt(StringBuilder out) {
        out.append("\nMRG32k3a nextInt(i, j)\n");
        out.append("Convention: inclusive bounds [").append(INT_I).append(", ").append(INT_J).append("].\n");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkLFSR258NextInt(StringBuilder out) {
        out.append("\nLFSR258 nextInt(i, j)\n");
        out.append("Convention: inclusive bounds [").append(INT_I).append(", ").append(INT_J).append("].\n");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X128StarStarRandomNextInt(StringBuilder out) {
        out.append("\nL64X128StarStarRandom nextInt(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextInt(")
           .append(INT_I).append(", ").append(INT_J + 1).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_STARSTAR);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X128MixRandomNextInt(StringBuilder out) {
        out.append("\nL64X128MixRandom nextInt(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextInt(")
           .append(INT_I).append(", ").append(INT_J + 1).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X256MixRandomNextInt(StringBuilder out) {
        out.append("\nL64X256MixRandom nextInt(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextInt(")
           .append(INT_I).append(", ").append(INT_J + 1).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X256_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X1024MixRandomNextInt(StringBuilder out) {
        out.append("\nL64X1024MixRandom nextInt(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextInt(")
           .append(INT_I).append(", ").append(INT_J + 1).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X1024_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sum = 0L;
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            intSink += sum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long sum = 0L;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++)
                sum += gen.nextInt(INT_I, INT_J + 1);
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            intSink += sum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k2a2NextLong(StringBuilder out) {
        out.append("\nMWC64k2a2 nextLong(i, j)\n");
        out.append("Convention: inclusive bounds [").append(LONG_I).append(", ").append(LONG_J).append("].\n");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMWC64k3a2NextLong(StringBuilder out) {
        out.append("\nMWC64k3a2 nextLong(i, j)\n");
        out.append("Convention: inclusive bounds [").append(LONG_I).append(", ").append(LONG_J).append("].\n");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkMRG32k3aNextLong(StringBuilder out) {
        out.append("\nMRG32k3a nextLong(i, j)\n");
        out.append("Convention: inclusive bounds [").append(LONG_I).append(", ").append(LONG_J).append("].\n");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkLFSR258NextLong(StringBuilder out) {
        out.append("\nLFSR258 nextLong(i, j)\n");
        out.append("Convention: inclusive bounds [").append(LONG_I).append(", ").append(LONG_J).append("].\n");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X128StarStarRandomNextLong(StringBuilder out) {
        out.append("\nL64X128StarStarRandom nextLong(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextLong(")
           .append(LONG_I).append(", ").append(LONG_J + 1L).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_STARSTAR);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J + 1L);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X128MixRandomNextLong(StringBuilder out) {
        out.append("\nL64X128MixRandom nextLong(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextLong(")
           .append(LONG_I).append(", ").append(LONG_J + 1L).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J + 1L);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X256MixRandomNextLong(StringBuilder out) {
        out.append("\nL64X256MixRandom nextLong(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextLong(")
           .append(LONG_I).append(", ").append(LONG_J + 1L).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X256_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J + 1L);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void benchmarkL64X1024MixRandomNextLong(StringBuilder out) {
        out.append("\nL64X1024MixRandom nextLong(i, j + 1)\n");
        out.append("Convention: exclusive upper bound; Java call is nextLong(")
           .append(LONG_I).append(", ").append(LONG_J + 1L).append(").\n");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X1024_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long checksum = 0L;
            for (int i = 0; i < M; i++)
                checksum += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += checksum;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long checksum = 0L;
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                long x = gen.nextLong(LONG_I, LONG_J + 1L);
                checksum += x;
                sum += x;
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
            longSink += checksum;
        }

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" calls, excluded.\n");
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }
}