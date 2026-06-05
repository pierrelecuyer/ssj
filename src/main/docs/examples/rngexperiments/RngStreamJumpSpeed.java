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
 * Stream-level jump speed benchmark.
 *
 * Compared operations:
 *
 * SSJ-style generators:
 * new stream creation through the constructor
 *
 * Java leapable generators:
 * LeapableGenerator.leap()
 *
 * This is a conceptual comparison only. The jump distances are not necessarily
 * equal, and SSJ-style generators do not expose a public "next stream" jump
 * method. For SSJ-style generators, this benchmark creates new stream
 * instances to simulate moving to successive streams.
 *
 * The benchmark uses one warmup run followed by timed runs.
 */
public class RngStreamJumpSpeed {

    static final boolean WRITE_FILE = true;

    static final int M = 1_000_000;
    static final String M_LABEL = "1e6";

    static final int WARMUP_RUNS = 1;
    static final int TIMED_RUNS = 5;

    static final long JAVA_SEED = 12345L;

    static final String JAVA_XOROSHIRO128_PP = "Xoroshiro128PlusPlus";
    static final String JAVA_XOSHIRO256_PP = "Xoshiro256PlusPlus";

    static double sink = 0.0;

    public static void main(String[] args) throws IOException {
        StringBuilder out = new StringBuilder();
        String outputFile = "/home/otman/Documents/GitHub/Data/o-MWC-test/RngStreamJumpSpeed.res";

        out.append("RNG stream-level jump speed benchmark\n");
        out.append("M = ").append(M_LABEL).append(" stream operations per run (").append(M).append(")\n");
        out.append("Warmup runs = ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" stream operations, excluded from timing result\n");
        out.append("Timed runs = ").append(TIMED_RUNS).append(" runs of ")
           .append(M_LABEL).append(" stream operations\n");
        out.append("Reported time = average time per stream operation over ").append(TIMED_RUNS)
           .append(" timed runs, each with ").append(M_LABEL).append(" operations\n\n");

        out.append("Benchmark notes:\n");
        out.append("- SSJ-style generators have no public next-stream jump method.\n");
        out.append("- For SSJ-style generators, one operation creates one new stream instance.\n");
        out.append("- Java generators use RandomGenerator.LeapableGenerator.leap().\n");
        out.append("- This is a conceptual comparison only; the jump distances and mechanisms are not necessarily equal.\n");
        out.append("- SSJ-style timings include object construction and package-seed advancement.\n");
        out.append("- Java timings measure leap() on an existing generator.\n");
        out.append("- One nextDouble() or nextLong() call is made after each run and added to a sink to keep the final state observable.\n");
        out.append("- The output reports only the average time per stream operation.\n\n");

        out.append("Generators:\n");
        out.append("- MWC64k2a2\n");
        out.append("- MWC64k3a2\n");
        out.append("- MRG32k3a\n");
        out.append("- LFSR258\n");
        out.append("- Xoroshiro128PlusPlus\n");
        out.append("- Xoshiro256PlusPlus\n\n");

        out.append("============================================================\n");
        out.append("Stream-level operation speed\n");
        out.append("============================================================\n");

        benchmarkMWC64k2a2StreamCreation(out);
        benchmarkMWC64k3a2StreamCreation(out);
        benchmarkMRG32k3aStreamCreation(out);
        benchmarkLFSR258StreamCreation(out);

        benchmarkJavaLeap(out, JAVA_XOROSHIRO128_PP);
        benchmarkJavaLeap(out, JAVA_XOSHIRO256_PP);

        out.append("\nFinal sink, used to prevent dead-code elimination:\n");
        out.append("sink = ").append(sink).append("\n");

        System.out.println(out);

        if (WRITE_FILE) {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(out.toString());
            writer.close();
        }
    }

    private static String formatNs(double ns) {
        return String.format(Locale.US, "%.3f", ns);
    }

    private static String formatJumpDistance(double distance) {
        if (distance > 0.0) {
            int exponent = (int) Math.round(Math.log(distance) / Math.log(2.0));
            double power = Math.pow(2.0, exponent);

            if (Double.compare(power, distance) == 0)
                return "2**" + exponent;
        }

        return String.format(Locale.US, "%.0f", distance);
    }

    private static void benchmarkMWC64k2a2StreamCreation(StringBuilder out) {
        out.append("\nMWC64k2a2 new stream creation\n");
        out.append("Stream distance: 2**113\n");

        for (int run = 0; run < WARMUP_RUNS; run++) {
            MWC64k2a2 gen = null;
            for (int i = 0; i < M; i++)
                gen = new MWC64k2a2();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            MWC64k2a2 gen = null;

            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen = new MWC64k2a2();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkMWC64k3a2StreamCreation(StringBuilder out) {
        out.append("\nMWC64k3a2 new stream creation\n");
        out.append("Stream distance: 2**169\n");

        for (int run = 0; run < WARMUP_RUNS; run++) {
            MWC64k3a2 gen = null;
            for (int i = 0; i < M; i++)
                gen = new MWC64k3a2();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            MWC64k3a2 gen = null;

            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen = new MWC64k3a2();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkMRG32k3aStreamCreation(StringBuilder out) {
        out.append("\nMRG32k3a new stream creation\n");
        out.append("Stream distance: 2**127\n");

        for (int run = 0; run < WARMUP_RUNS; run++) {
            MRG32k3a gen = null;
            for (int i = 0; i < M; i++)
                gen = new MRG32k3a();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            MRG32k3a gen = null;

            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen = new MRG32k3a();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkLFSR258StreamCreation(StringBuilder out) {
        out.append("\nLFSR258 new stream creation\n");
        out.append("Stream distance: 2**200\n");

        for (int run = 0; run < WARMUP_RUNS; run++) {
            LFSR258 gen = null;
            for (int i = 0; i < M; i++)
                gen = new LFSR258();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            LFSR258 gen = null;

            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen = new LFSR258();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkJavaLeap(StringBuilder out, String algorithm) {
        out.append("\n").append(algorithm).append(" leap()\n");

        RandomGenerator gen0 = RandomGeneratorFactory.of(algorithm).create(JAVA_SEED);

        if (!(gen0 instanceof RandomGenerator.LeapableGenerator)) {
            out.append("Skipped: this generator does not implement LeapableGenerator.\n");
            return;
        }

        RandomGenerator.LeapableGenerator gen =
                (RandomGenerator.LeapableGenerator) gen0;

        out.append("Leap distance reported by Java: ")
           .append(formatJumpDistance(gen.leapDistance()))
           .append("\n");

        for (int run = 0; run < WARMUP_RUNS; run++) {
            for (int i = 0; i < M; i++)
                gen.leap();
            sink += gen.nextLong();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen.leap();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextLong();
        }

        printResult(out, totalTime);
    }

    private static void printResult(StringBuilder out, double totalTime) {
        double averageRunSeconds = totalTime / TIMED_RUNS;
        double nsPerOperation = averageRunSeconds * 1.0e9 / M;

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" stream operations, excluded.\n");
        out.append("Average time per stream operation over ").append(TIMED_RUNS)
           .append(" timed runs, each with ").append(M_LABEL).append(" operations: ")
           .append(formatNs(nsPerOperation)).append(" ns\n");
    }
}