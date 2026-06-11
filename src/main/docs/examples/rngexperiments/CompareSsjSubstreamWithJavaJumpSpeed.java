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
 * Fixed jump speed benchmark.
 *
 * Compared operations:
 *
 * SSJ-style generators:
 * resetNextSubstream()
 *
 * Java jumpable generators:
 * JumpableGenerator.jump()
 *
 * This is a conceptual comparison. SSJ substream jumps and Java jump()
 * do not necessarily use the same jump distance.
 *
 * The benchmark uses one warmup run followed by timed runs. Each generator is
 * reset or recreated once before warmup, then timed runs continue from the state
 * reached after warmup.
 */
public class RngJumpSpeed {

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
        String outputFile = "/home/otman/Documents/GitHub/Data/o-MWC-test/RngJumpSpeedSub.res";

        out.append("RNG fixed jump speed benchmark\n");
        out.append("M = ").append(M_LABEL).append(" jumps per run (").append(M).append(")\n");
        out.append("Warmup runs = ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" jumps, excluded from timing result\n");
        out.append("Timed runs = ").append(TIMED_RUNS).append(" runs of ")
           .append(M_LABEL).append(" jumps\n");
        out.append("Reported time = average time per jump over ").append(TIMED_RUNS)
           .append(" timed runs, each with ").append(M_LABEL).append(" jumps\n\n");

        out.append("Benchmark notes:\n");
        out.append("- SSJ-style generators use resetNextSubstream().\n");
        out.append("- Java generators use RandomGenerator.JumpableGenerator.jump().\n");
        out.append("- This is a conceptual comparison only; the jump distances are not equal.\n");
        out.append("- One nextDouble() or nextLong() call is made after each run and added to a sink to keep the final state observable.\n");
        out.append("- The output reports only the average time per jump.\n\n");

        out.append("Generators:\n");
        out.append("- MWC64k2a2\n");
        out.append("- MWC64k3a2\n");
        out.append("- MRG32k3a\n");
        out.append("- LFSR258\n");
        out.append("- Xoroshiro128PlusPlus\n");
        out.append("- Xoshiro256PlusPlus\n\n");

        out.append("============================================================\n");
        out.append("Fixed jump speed\n");
        out.append("============================================================\n");

        benchmarkMWC64k2a2NextSubstream(out);
        benchmarkMWC64k3a2NextSubstream(out);
        benchmarkMRG32k3aNextSubstream(out);
        benchmarkLFSR258NextSubstream(out);

        benchmarkJavaJump(out, JAVA_XOROSHIRO128_PP);
        benchmarkJavaJump(out, JAVA_XOSHIRO256_PP);

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

    private static void benchmarkMWC64k2a2NextSubstream(StringBuilder out) {
        out.append("\nMWC64k2a2 resetNextSubstream()\n");
        out.append("Jump distance: 2**62\n");

        MWC64k2a2 gen = new MWC64k2a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkMWC64k3a2NextSubstream(StringBuilder out) {
        out.append("\nMWC64k3a2 resetNextSubstream()\n");
        out.append("Jump distance: 2**118\n");

        MWC64k3a2 gen = new MWC64k3a2();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkMRG32k3aNextSubstream(StringBuilder out) {
        out.append("\nMRG32k3a resetNextSubstream()\n");
        out.append("Jump distance: 2**76\n");

        MRG32k3a gen = new MRG32k3a();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkLFSR258NextSubstream(StringBuilder out) {
        out.append("\nLFSR258 resetNextSubstream()\n");
        out.append("Jump distance: 2**100\n");

        LFSR258 gen = new LFSR258();
        gen.resetStartStream();

        for (int run = 0; run < WARMUP_RUNS; run++) {
            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();
            sink += gen.nextDouble();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen.resetNextSubstream();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextDouble();
        }

        printResult(out, totalTime);
    }

    private static void benchmarkJavaJump(StringBuilder out, String algorithm) {
        out.append("\n").append(algorithm).append(" jump()\n");

        RandomGenerator gen0 = RandomGeneratorFactory.of(algorithm).create(JAVA_SEED);

        if (!(gen0 instanceof RandomGenerator.JumpableGenerator)) {
            out.append("Skipped: this generator does not implement JumpableGenerator.\n");
            return;
        }

        RandomGenerator.JumpableGenerator gen =
                (RandomGenerator.JumpableGenerator) gen0;

        out.append("Jump distance reported by Java: ")
           .append(formatJumpDistance(gen.jumpDistance()))
           .append("\n");

        for (int run = 0; run < WARMUP_RUNS; run++) {
            for (int i = 0; i < M; i++)
                gen.jump();
            sink += gen.nextLong();
        }

        double totalTime = 0.0;

        for (int run = 0; run < TIMED_RUNS; run++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                gen.jump();

            long end = System.nanoTime();

            totalTime += (end - start) * 1.0e-9;
            sink += gen.nextLong();
        }

        printResult(out, totalTime);
    }

    private static void printResult(StringBuilder out, double totalTime) {
        double averageRunSeconds = totalTime / TIMED_RUNS;
        double nsPerJump = averageRunSeconds * 1.0e9 / M;

        out.append("Warmup: ").append(WARMUP_RUNS).append(" run of ")
           .append(M_LABEL).append(" jumps, excluded.\n");
        out.append("Average time per jump over ").append(TIMED_RUNS)
           .append(" timed runs, each with ").append(M_LABEL).append(" jumps: ")
           .append(formatNs(nsPerJump)).append(" ns\n");
    }
}