package rngexperiments;

import java.io.FileWriter;
import java.math.BigInteger;
import java.io.IOException;

import umontreal.ssj.rng.MWC64k2a2;
import umontreal.ssj.rng.MWC64k3a2;

public class MirrorCppMwc64k2a2AndK3a2 {

   /*
    * Java mirror of the C++ MWC speed and jump tests.
    *
    * Mirrors TestMWCSpeed.cc / MWCSpeed10.res and
    * TestMWCJump.cc / MWCJump.res for MWC64k2a2 and MWC64k3a2.
    * The C++ speed and jump tests do not use the same MWC coefficients.
    */
   private static final long N_SPEED = 10_000_000_000L;
   private static final long JUMP_SIZE = 5000L;
   private static final long N_JUMPS = 1000_000L;

   private static final long[] SEED_K2 = {12345L, 12345L, 12345L};
   private static final long[] SEED_K3 = {12345L, 12345L, 12345L, 12345L};

   public static void main(String[] args) throws IOException {
      StringBuilder out = new StringBuilder();

      printFirstRawValuesMWC64k2a2(out, 10);
      printFirstRawValuesMWC64k3a2(out, 10);

      runSpeedRawTestMWC64k2a2(out);
      runSpeedRawTestMWC64k3a2(out);

      runSpeedU01TestMWC64k2a2(out);
      runSpeedU01TestMWC64k3a2(out);

      runJumpTestMWC64k2a2(out);
      runJumpTestMWC64k3a2(out);

      System.out.println(out);

//      FileWriter writer = new FileWriter("MirrorCppMwc64k2a2AndK3a2.res");
//      writer.write(out.toString());
//      writer.close();
   }

   /*
    * Prints the first raw values for MWC64k2a2.
    */
   private static void printFirstRawValuesMWC64k2a2(StringBuilder out, int n) {
      MWC64k2a2 rng = new MWC64k2a2();
      rng.setSeed(SEED_K2);

      printFirstRawHeader(out, "MWC64k2a2", SEED_K2, n);
      for (long i = 0; i < n; i++) {
         out.append(Long.toUnsignedString(rng.nextRaw()) + "     \n");
      }
   }

   /*
    * Prints the first raw values for MWC64k3a2.
    */
   private static void printFirstRawValuesMWC64k3a2(StringBuilder out, int n) {
      MWC64k3a2 rng = new MWC64k3a2();
      rng.setSeed(SEED_K3);

      printFirstRawHeader(out, "MWC64k3a2", SEED_K3, n);
      for (long i = 0; i < n; i++) {
         out.append(Long.toUnsignedString(rng.nextRaw()) + "     \n");
      }
   }

   /*
    * Mirrors the raw-output speed block from TestMWCSpeed.cc for MWC64k2a2.
    */
   private static void runSpeedRawTestMWC64k2a2(StringBuilder out) {
      MWC64k2a2 rng = new MWC64k2a2();

      rng.setSeed(SEED_K2);
      long sum = 0L;
      long start = System.nanoTime();
      for (long i = 0; i < N_SPEED; i++) {
         sum += rng.nextRaw();
      }
      long end = System.nanoTime();

      printRawSpeedResult(out, "MWC64k2a2", sum, start, end);
   }

   /*
    * Mirrors the raw-output speed block from TestMWCSpeed.cc for MWC64k3a2.
    */
   private static void runSpeedRawTestMWC64k3a2(StringBuilder out) {
      MWC64k3a2 rng = new MWC64k3a2();

      rng.setSeed(SEED_K3);
      long sum = 0L;
      long start = System.nanoTime();
      for (long i = 0; i < N_SPEED; i++) {
         sum += rng.nextRaw();
      }
      long end = System.nanoTime();

      printRawSpeedResult(out, "MWC64k3a2", sum, start, end);
   }

   /*
    * Mirrors the U(0,1) speed block from TestMWCSpeed.cc for MWC64k2a2.
    */
   private static void runSpeedU01TestMWC64k2a2(StringBuilder out) {
      MWC64k2a2 rng = new MWC64k2a2();
      rng.setSeed(SEED_K2);

      double sum = 0.0;

      long start = System.nanoTime();

      for (long i = 0; i < N_SPEED; i++) {
         sum += rng.nextDouble();
      }

      long end = System.nanoTime();

      printU01SpeedResult(out, "MWC64k2a2", sum, start, end);
   }

   /*
    * Mirrors the U(0,1) speed block from TestMWCSpeed.cc for MWC64k3a2.
    */
   private static void runSpeedU01TestMWC64k3a2(StringBuilder out) {
      MWC64k3a2 rng = new MWC64k3a2();
      rng.setSeed(SEED_K3);

      double sum = 0.0;

      long start = System.nanoTime();

      for (long i = 0; i < N_SPEED; i++) {
         sum += rng.nextDouble();
      }

      long end = System.nanoTime();

      printU01SpeedResult(out, "MWC64k3a2", sum, start, end);
   }

   /*
    * Mirrors TestMWCJump.cc / MWCJump.res for MWC64k2a2.
    * The C++ jump test uses different MWC coefficients from the C++ speed test.
   */
   private static void runJumpTestMWC64k2a2(StringBuilder out) {
      MWC64k2a2 rng = new MWC64k2a2();
      rng.setSeed(SEED_K2);

      printJumpHeader(out, "MWC64k2a2");
      out.append("Successive jumps ahead:\n");
      out.append("initial state = " + state(rng.getState()) + "\n");

      long start = System.nanoTime();

      for (int i = 1; i <= 4; i++) {
         rng.advanceStateByJump(BigInteger.valueOf(JUMP_SIZE));
         out.append("after jump " + i + " = " + state(rng.getState()) + "\n");
      }

      long end = System.nanoTime();

      out.append("time for 4 jumps = " + seconds(start, end) + " s\n");

      /*
       * One big jump from the original seed.
       * This corresponds to jumpSize2 = n0 * jumpSize = 4 * 5000 = 20000.
       */
      rng.setSeed(SEED_K2);

      start = System.nanoTime();

      rng.advanceStateByJump(BigInteger.valueOf(JUMP_SIZE).multiply(BigInteger.valueOf(4L)));

      end = System.nanoTime();

      out.append("\n");
      out.append("One large jump:\n");
      out.append("jumpSize2 = " + (4L * JUMP_SIZE) + "\n");
      out.append("state = " + state(rng.getState()) + "\n");
      out.append("time = " + seconds(start, end) + " s\n");

      /*
       * Timing test:
       * jump ahead by jumpSize, repeated N_JUMPS times.
       */
      rng.setSeed(SEED_K2);

      start = System.nanoTime();

      for (long i = 0; i < N_JUMPS; i++) {
         rng.advanceStateByJump(BigInteger.valueOf(JUMP_SIZE));
      }

      end = System.nanoTime();

      out.append("\n");
      out.append("Repeated jump timing:\n");
      out.append("number of jumps = " + N_JUMPS + "\n");
      out.append("jumpSize = " + JUMP_SIZE + "\n");
      out.append("final state = " + state(rng.getState()) + "\n");
      out.append("time = " + seconds(start, end) + " s\n");
   }

   /*
    * Mirrors TestMWCJump.cc / MWCJump.res for MWC64k3a2.
    * The C++ jump test uses these coefficients:
    *   A2 = 0x320fbe97bef0f95L, A3 = 0x4a1849ec18bfa6L;
    */
   private static void runJumpTestMWC64k3a2(StringBuilder out) {
      MWC64k3a2 rng = new MWC64k3a2();
      rng.setSeed(SEED_K3);

      printJumpHeader(out, "MWC64k3a2");
      out.append("Successive jumps ahead:\n");
      out.append("initial state = " + state(rng.getState()) + "\n");

      long start = System.nanoTime();

      for (int i = 1; i <= 4; i++) {
         rng.advanceStateByJump(BigInteger.valueOf(JUMP_SIZE));
         out.append("after jump " + i + " = " + state(rng.getState()) + "\n");
      }

      long end = System.nanoTime();

      out.append("time for 4 jumps = " + seconds(start, end) + " s\n");

      /*
       * One big jump from the original seed.
       * This corresponds to jumpSize2 = n0 * jumpSize = 4 * 5000 = 20000.
       */
      rng.setSeed(SEED_K3);

      start = System.nanoTime();

      rng.advanceStateByJump(BigInteger.valueOf(JUMP_SIZE).multiply(BigInteger.valueOf(4L)));

      end = System.nanoTime();

      out.append("\n");
      out.append("One large jump:\n");
      out.append("jumpSize2 = " + (4L * JUMP_SIZE) + "\n");
      out.append("state = " + state(rng.getState()) + "\n");
      out.append("time = " + seconds(start, end) + " s\n");

      /*
       * Timing test:
       * jump ahead by jumpSize, repeated N_JUMPS times.
       */
      rng.setSeed(SEED_K3);

      start = System.nanoTime();

      for (long i = 0; i < N_JUMPS; i++) {
         rng.advanceStateByJump(BigInteger.valueOf(JUMP_SIZE));
      }

      end = System.nanoTime();

      out.append("\n");
      out.append("Repeated jump timing:\n");
      out.append("number of jumps = " + N_JUMPS + "\n");
      out.append("jumpSize = " + JUMP_SIZE + "\n");
      out.append("final state = " + state(rng.getState()) + "\n");
      out.append("time = " + seconds(start, end) + " s\n");
   }

   private static void printFirstRawHeader(StringBuilder out, String mwc, long[] seed, int n) {
      out.append("-----" + mwc + " first raw values------------------\n");
      out.append("Using the seed " + state(seed) + ", the first " + n + " values are:\n");
   }

   private static void printRawSpeedResult(StringBuilder out, String mwc,
         long sum, long start, long end) {
      out.append("=============================================================\n");
      out.append(mwc + " raw speed test\n");
      out.append("n = " + N_SPEED + "\n");
      out.append("sum = " + Long.toUnsignedString(sum) + "\n");
      out.append("time = " + seconds(start, end) + " s\n");
   }

   private static void printU01SpeedResult(StringBuilder out, String mwc,
         double sum, long start, long end) {
      out.append("\n");
      out.append("======================" + mwc + "=======================================\n");
      out.append(mwc + " U(0,1) speed test\n");
      out.append("n = " + N_SPEED + "\n");
      out.append("average = " + (sum / N_SPEED) + "\n");
      out.append("time = " + seconds(start, end) + " s\n");
   }

   private static void printJumpHeader(StringBuilder out, String mwc) {
      out.append("=============================================================\n");
      out.append(mwc + " jump test\n");
      out.append("jumpSize = " + JUMP_SIZE + "\n");
      out.append("n jumps for timing = " + N_JUMPS + "\n");
   }

   /*
    * Print unsigned 64-bit state values.
    */
   private static String state(long[] s) {
      StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < s.length; i++) {
         if (i > 0)
            sb.append(" ");
         sb.append(Long.toUnsignedString(s[i]));
      }
      return sb.append("]").toString();
   }

   private static double seconds(long start, long end) {
      return (end - start) / 1.0e9;
   }
}
