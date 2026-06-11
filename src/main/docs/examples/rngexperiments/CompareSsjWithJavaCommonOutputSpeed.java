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
 * MRG32k3a
 * LFSR258
 * MWC64k2a2
 * MWC64k3a2
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
 * SSJ generators use their default stream seeds. Java LXM generators are
 * created with JAVA_SEED for reproducibility.
 * Each timed loop calls the generator method directly.
 */
public class CompareSsjWithJavaCommonOutputSpeed {

    static final int M = 10_000_000;
    static final String M_LABEL = "1e7";

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
        String outputFile = "CompareSsjWithJavaCommonOutputSpeed.res";

        printIntro(out);

        printDoubleSectionHeader(out);
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

        printIntSectionHeader(out);
        benchmarkMRG32k3aNextInt(out);
        benchmarkLFSR258NextInt(out);
        benchmarkMWC64k2a2NextInt(out);
        benchmarkMWC64k3a2NextInt(out);
        benchmarkL64X128StarStarRandomNextInt(out);
        benchmarkL64X128MixRandomNextInt(out);
        benchmarkL64X256MixRandomNextInt(out);
        benchmarkL64X1024MixRandomNextInt(out);

        printLongSectionHeader(out);
        benchmarkMRG32k3aNextLong(out);
        benchmarkLFSR258NextLong(out);
        benchmarkMWC64k2a2NextLong(out);
        benchmarkMWC64k3a2NextLong(out);
        benchmarkL64X128StarStarRandomNextLong(out);
        benchmarkL64X128MixRandomNextLong(out);
        benchmarkL64X256MixRandomNextLong(out);
        benchmarkL64X1024MixRandomNextLong(out);

        printFinalSinks(out);

        System.out.println(out);

//        FileWriter writer = new FileWriter(outputFile);
//        writer.write(out.toString());
//        writer.close();
    }

    private static void benchmarkMRG32k3aNextDouble(StringBuilder out) {
        printSSJDoubleHeader(out, "MRG32k3a");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkLFSR258NextDouble(StringBuilder out) {
        printSSJDoubleHeader(out, "LFSR258");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k2a2NextDouble(StringBuilder out) {
        printMWCDoubleHeader(out, "MWC64k2a2");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k2a2NextDoubleNonzero(StringBuilder out) {
        printMWCDoubleNonzeroHeader(out, "MWC64k2a2");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDoubleNonzero();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k3a2NextDouble(StringBuilder out) {
        printMWCDoubleHeader(out, "MWC64k3a2");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k3a2NextDoubleNonzero(StringBuilder out) {
        printMWCDoubleNonzeroHeader(out, "MWC64k3a2");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDoubleNonzero();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X128StarStarRandomNextDouble(StringBuilder out) {
        printJavaDoubleHeader(out, "L64X128StarStarRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_STARSTAR);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X128MixRandomNextDouble(StringBuilder out) {
        printJavaDoubleHeader(out, "L64X128MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X256MixRandomNextDouble(StringBuilder out) {
        printJavaDoubleHeader(out, "L64X256MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X256_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X1024MixRandomNextDouble(StringBuilder out) {
        printJavaDoubleHeader(out, "L64X1024MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X1024_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            double sink = 0.0;
            for (int i = 0; i < M; i++)
                sink += gen.nextDouble();
            doubleSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMRG32k3aNextInt(StringBuilder out) {
        printSSJIntHeader(out, "MRG32k3a");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkLFSR258NextInt(StringBuilder out) {
        printSSJIntHeader(out, "LFSR258");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k2a2NextInt(StringBuilder out) {
        printSSJIntHeader(out, "MWC64k2a2");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k3a2NextInt(StringBuilder out) {
        printSSJIntHeader(out, "MWC64k3a2");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X128StarStarRandomNextInt(StringBuilder out) {
        printJavaIntHeader(out, "L64X128StarStarRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_STARSTAR);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J + 1);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X128MixRandomNextInt(StringBuilder out) {
        printJavaIntHeader(out, "L64X128MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J + 1);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X256MixRandomNextInt(StringBuilder out) {
        printJavaIntHeader(out, "L64X256MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X256_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J + 1);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X1024MixRandomNextInt(StringBuilder out) {
        printJavaIntHeader(out, "L64X1024MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X1024_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextInt(INT_I, INT_J + 1);
            intSink += sink;
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
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMRG32k3aNextLong(StringBuilder out) {
        printSSJLongHeader(out, "MRG32k3a");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkLFSR258NextLong(StringBuilder out) {
        printSSJLongHeader(out, "LFSR258");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k2a2NextLong(StringBuilder out) {
        printSSJLongHeader(out, "MWC64k2a2");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkMWC64k3a2NextLong(StringBuilder out) {
        printSSJLongHeader(out, "MWC64k3a2");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X128StarStarRandomNextLong(StringBuilder out) {
        printJavaLongHeader(out, "L64X128StarStarRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_STARSTAR);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J + 1L);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X128MixRandomNextLong(StringBuilder out) {
        printJavaLongHeader(out, "L64X128MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X128_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J + 1L);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X256MixRandomNextLong(StringBuilder out) {
        printJavaLongHeader(out, "L64X256MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X256_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J + 1L);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void benchmarkL64X1024MixRandomNextLong(StringBuilder out) {
        printJavaLongHeader(out, "L64X1024MixRandom");

        RandomGeneratorFactory<RandomGenerator> factory =
                RandomGeneratorFactory.of(JAVA_L64X1024_MIX);
        RandomGenerator gen = factory.create(JAVA_SEED);

        for (int run = 0; run < WARMUP_RUNS; run++) {
            long sink = 0L;
            for (int i = 0; i < M; i++)
                sink += gen.nextLong(LONG_I, LONG_J + 1L);
            longSink += sink;
        }

        double totalTime = 0.0;
        double totalSum = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            double sum = 0.0;

            long start = System.nanoTime();
            for (int i = 0; i < M; i++) {
                sum += gen.nextLong(LONG_I, LONG_J + 1L);
            }
            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            totalSum += sum;
        }

        printTimedMean(out, totalTime, totalSum);
    }

    private static void printIntro(StringBuilder out) {
        out.append("RNG common output speed benchmark\n");
        out.append("M = ").append(M_LABEL).append(" calls per run (").append(M).append(")\n");
        out.append("Timed runs = ").append(TIMED_RUNS).append(" runs of ")
           .append(M_LABEL).append(" calls\n\n");

        out.append("Notes:\n");
        out.append("- SSJ generators use their default stream seeds.\n");
        out.append("- Java LXM generators are created with seed ").append(JAVA_SEED).append(".\n");
        out.append("- nextInt and nextLong measure bounded public API methods, not raw output speed.\n\n");
    }

    private static void printDoubleSectionHeader(StringBuilder out) {
        out.append("============================================================\n");
        out.append("nextDouble() and nextDoubleNonzero() speed\n");
        out.append("Run 1 is warmup and excluded. Expected mean is close to 0.5.\n");
        out.append("============================================================\n");
    }

    private static void printIntSectionHeader(StringBuilder out) {
        out.append("\n");
        out.append("============================================================\n");
        out.append("nextInt(i, j) speed\n");
        out.append("SSJ-style range: [").append(INT_I).append(", ").append(INT_J).append("]\n");
        out.append("Java range: [").append(INT_I).append(", ").append(INT_J + 1).append(")\n");
        out.append("Run 1 is warmup and excluded. Expected mean is close to ")
        .append(String.format(Locale.US, "%.0f", (INT_I + INT_J) / 2.0)).append(".\n");
        out.append("============================================================\n");
    }

    private static void printLongSectionHeader(StringBuilder out) {
        out.append("\n");
        out.append("============================================================\n");
        out.append("nextLong(i, j) speed\n");
        out.append("SSJ-style range: [").append(LONG_I).append(", ").append(LONG_J).append("]\n");
        out.append("Java range: [").append(LONG_I).append(", ").append(LONG_J + 1L).append(")\n");
        out.append("Run 1 is warmup and excluded. Expected mean is close to ")
        .append(String.format(Locale.US, "%.0f", (LONG_I + LONG_J) / 2.0)).append(".\n");
        out.append("============================================================\n");
    }

    private static void printFinalSinks(StringBuilder out) {
        out.append("\n");
        out.append("Final warmup sinks, used to prevent dead-code elimination:\n");
        out.append("doubleSink = ").append(doubleSink).append("\n");
        out.append("intSink    = ").append(intSink).append("\n");
        out.append("longSink   = ").append(longSink).append("\n");
    }

    private static String formatTime(double seconds) {
        return String.format(Locale.US, "%.4f", seconds);
    }

    private static String formatMean(double mean) {
        return String.format(Locale.US, "%.6f", mean);
    }

    private static void printTimedMean(StringBuilder out, double totalTime, double totalSum) {
        out.append("Average time of ").append(TIMED_RUNS).append(" timed runs, each with ")
           .append(M_LABEL).append(" calls: ").append(formatTime(totalTime / TIMED_RUNS)).append(" s\n");
        out.append("Mean over ").append(TIMED_RUNS).append(" * ").append(M_LABEL)
           .append(" timed values: ").append(formatMean(totalSum / ((double) TIMED_RUNS * M))).append("\n");
    }

    private static void printMWCDoubleHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextDouble()\n");
    }

    private static void printMWCDoubleNonzeroHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextDoubleNonzero()\n");
    }

    private static void printSSJDoubleHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextDouble()\n");
    }

    private static void printJavaDoubleHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextDouble()\n");
    }

    private static void printSSJIntHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextInt(")
           .append(INT_I).append(", ").append(INT_J).append(")\n");
    }

    private static void printJavaIntHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextInt(")
           .append(INT_I).append(", ").append(INT_J + 1).append(")\n");
    }

    private static void printSSJLongHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextLong(")
           .append(LONG_I).append(", ").append(LONG_J).append(")\n");
    }

    private static void printJavaLongHeader(StringBuilder out, String name) {
        out.append("\n").append(name).append(" nextLong(")
           .append(LONG_I).append(", ").append(LONG_J + 1L).append(")\n");
    }
}
