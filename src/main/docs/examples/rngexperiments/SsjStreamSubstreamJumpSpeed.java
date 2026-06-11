package rngexperiments;

import java.io.FileWriter;
import java.io.IOException;

import umontreal.ssj.rng.BasicRandomStreamFactory;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.MWC64k2a2;
import umontreal.ssj.rng.MWC64k3a2;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.RandomStreamFactory;

/**
 * Speed test for fixed stream and substream jumps in several SSJ random streams.
 *
 * This example compares two fixed-jump operations:
 *
 * 1. Stream jumps:
 *    Creating new stream objects with a RandomStreamFactory. In SSJ,
 *    constructing a new stream advances the package-level seed to the next
 *    stream.
 *
 * 2. Substream jumps:
 *    Calling resetNextSubstream() repeatedly on an existing stream.
 *
 * Each generator is tested for N runs. Run 1 is printed as a warm-up and is
 * excluded from the reported average.
 */
public class SsjStreamSubstreamJumpSpeed {

    /** Number of jumps or new streams per run. */
    private static final int M = 1_000_000;

    /** Number of runs. Run 1 is warm-up and is not included in the average. */
    private static final int N = 6;

    /**
     * Sink used to prevent the JVM from removing stream creation as dead code.
     */
    private static RandomStream streamSink;

    public static void main(String[] args) throws IOException {
        StringBuilder out = new StringBuilder();

        out.append("Jump speed test\n");
        out.append("M = ").append(M).append(" jumps per run\n");
        out.append("N = ").append(N).append(" runs\n");
        out.append("Times are reported as total milliseconds per run.\n");
        out.append("Run 1 is a warm-up run and is not included in the average.\n");
        out.append("Average is computed over runs 2 to ").append(N).append(".\n\n");

        testStreamJump(out);
        testSubstreamJump(out);

        System.out.println(out);

//        FileWriter writer = new FileWriter("SsjStreamSubstreamJumpSpeed.res");
//        writer.write(out.toString());
//        writer.close();
    }

    /**
     * Tests stream jumps by repeatedly creating new stream objects.
     */
    private static void testStreamJump(StringBuilder out) {
        out.append("===== Stream jump: constructor + stream jump + object allocation =====\n\n");

        runStreamJump("MRG32k3a", new BasicRandomStreamFactory(MRG32k3a.class), out);
        runStreamJump("LFSR258", new BasicRandomStreamFactory(LFSR258.class), out);
        runStreamJump("MWC64k2a2", new BasicRandomStreamFactory(MWC64k2a2.class), out);
        runStreamJump("MWC64k3a2", new BasicRandomStreamFactory(MWC64k3a2.class), out);

        out.append("\n");
    }

    /**
     * Tests substream jumps by repeatedly calling resetNextSubstream().
     */
    private static void testSubstreamJump(StringBuilder out) {
        out.append("===== Substream jump: resetNextSubstream() =====\n\n");

        runSubstreamJump("MRG32k3a", new BasicRandomStreamFactory(MRG32k3a.class), out);
        runSubstreamJump("LFSR258", new BasicRandomStreamFactory(LFSR258.class), out);
        runSubstreamJump("MWC64k3a2", new BasicRandomStreamFactory(MWC64k3a2.class), out);
        runSubstreamJump("MWC64k2a2", new BasicRandomStreamFactory(MWC64k2a2.class), out);

        out.append("\n");
    }

    /**
     * Measures stream jumps by repeatedly asking the factory for new streams.
     */
    private static void runStreamJump(String name, RandomStreamFactory factory, StringBuilder out) {
        out.append(name).append("\n");

        double total = 0.0;

        for (int rep = 1; rep <= N; rep++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                streamSink = factory.newInstance();

            double time = (System.nanoTime() - start) / 1_000_000.0;

            if (rep > 1)
                total += time;

            out.append("Run ").append(rep).append(": ").append(time).append(" ms");

            if (rep == 1)
                out.append("  (warm-up, not included in average)");

            out.append("\n");
        }

        out.append("Average over runs 2 to ").append(N).append(": ")
           .append(total / (N - 1)).append(" ms\n\n");
        out.append(streamSink + "\n");
    }

    /**
     * Measures substream jumps by creating one stream and repeatedly calling
     * resetNextSubstream() on it.
     */
    private static void runSubstreamJump(String name, RandomStreamFactory factory, StringBuilder out) {
        out.append(name).append("\n");

        RandomStream stream = factory.newInstance();
        double total = 0.0;

        for (int rep = 1; rep <= N; rep++) {
            long start = System.nanoTime();

            for (int i = 0; i < M; i++)
                stream.resetNextSubstream();

            double time = (System.nanoTime() - start) / 1_000_000.0;

            if (rep > 1)
                total += time;

            out.append("Run ").append(rep).append(": ").append(time).append(" ms");

            if (rep == 1)
                out.append("  (warm-up, not included in average)");

            out.append("\n");
        }

        out.append("Average over runs 2 to ").append(N).append(": ")
           .append(total / (N - 1)).append(" ms\n\n");
        out.append(stream + "\n");
    }
}
