package rqmcexperiments;

import java.io.*;

import umontreal.ssj.hups.*;
import umontreal.ssj.mcqmctools.*;
// import umontreal.ssj.mcqmctools.RQMCExperiment64;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Num;

// Tools to generate and store RQMC replicates for WSC 2024 paper

public class TestRQMC64 extends RQMCExperiment64 {
//public class TestRQMC64 extends RQMCExperiment {

   // static Tally statReps = new Tally(); // Collects stats on RQMC replicates.

   public static void testSobol(int k, int s) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      RandomStream stream = new LFSR258();
      // RandomStream stream = new LFSR113();

      // Sobol' points
      DigitalNetBase2 p = new SobolSequence(k, k, s); // n = 2^{k} points in s dim., w=5.
      // PointSetRandomization rands = new RandomShift(stream); // Digital shift
      BakerTransformedPointSet pBaker = new BakerTransformedPointSet(p);

      // Sob
      p.printGeneratorMatricesBits(s);
      System.out.println(p.formatPoints());

      System.out.println("Sobol + baker:");
      System.out.println(pBaker.formatPoints());

      // Sob-RDS
      System.out.println("Sobol + RDS:");
      p = new SobolSequence(k, k, s);
      p.addRandomShift(stream);
      System.out.println(p.formatPoints());

      // Sob-RDSB
      System.out.println("Sobol + RDS + baker:");
      System.out.println(pBaker.formatPoints());

      // Sob-LMS
      System.out.println("*******************************\nSobol + LMS:");
      p = new SobolSequence(k, 31, s);
      PointSetRandomization randlms = new LMScramble(stream);
      randlms.randomize(p);
      p.printGeneratorMatricesBits(s);
      p.printGeneratorMatricesColumns(s);
      System.out.println(p.formatPoints());

      // Sob-LMS-RDS
      System.out.println("After the LMS + RDS:");
      p.addRandomShift(stream);
      // System.out.println(p.formatPoints());
   }

   public static void testSobolNUS(int k, int s) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      RandomStream stream = new LFSR258();
      // RandomStream stream = new LFSR113();

      // Sob-NUS
      DigitalNetBase2 p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim., w=5.
      System.out.println("********************************\nSobol + NUS:");
      // p = new SobolSequence(k, 31, s); // sets w
      CachedPointSet cp = new CachedPointSet(p);
      System.out.println("The generator matrices for the points p before NUS:");
      p.printGeneratorMatricesColumns(s);
      System.out.println("The cached points cp before NUS:");
      System.out.println(cp.formatPoints());
      stream.resetStartStream();
      PointSetRandomization randNUS = new NestedUniformScrambling(stream, 30); // Number of bits that are scrambled.
      // RQMCPointSet ps = new RQMCPointSet(cp, randNUS);
      // ps.randomize();
      // randNUS.randomize(cp);
      p.nestedUniformScramble(stream, cp.getArray(), 30);
      cp.sortByCoordinate(1);
      System.out.println("===============================");
      System.out.println("The cached points cp after NUS:");
      System.out.println(cp.formatPoints());

      PointSetIterator streamP = cp.iterator();
      double[] point = new double[s];
      double sum = 0.0;
      streamP.resetStartStream();
      for (int i = 0; i < n; i++) {
         streamP.nextPoint(point, s);
         for (int j = 0; j < s; j++)
            sum += point[j];
      }
      System.out.println("Average of all coordinates = " + sum / (n * s) + "\n");

      p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      cp = new CachedPointSet(p);
      stream.resetStartStream();
      System.out.println("===================================================");
      p.nestedUniformScramble(stream, cp.getArray(), k);
      cp.sortByCoordinate(1);
      System.out.println("===============================");
      System.out.println("The cached points cp after NUS64 with numBits = " + k);
      System.out.println(cp.formatPoints());

      p = new SobolSequence(k, 35, s); // n = 2^{k} points in s dim.
      cp = new CachedPointSet(p);
      stream.resetStartStream();
      System.out.println("===================================================");
      p.nestedUniformScramble64(stream, cp.getArray(), k);
      cp.sortByCoordinate(1);
      System.out.println("===============================");
      System.out.println("The cached points cp after NUS64 with numBits = " + k);
      System.out.println(cp.formatPoints());

      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      cp = new CachedPointSet(p);
      stream.resetStartStream();
      System.out.println("===================================================");
      p.nestedUniformScramble64(stream, cp.getArray(), 53);
      cp.sortByCoordinate(1);
      System.out.println("===============================");
      System.out.println("The cached points cp after NUS64 with numBits = " + 53);
      System.out.println(cp.formatPoints());

      double[][] output = cp.getArray(); // new double[n][2];
      sum = 0.0;
      for (int i = 0; i < n; i++) {
         for (int j = 0; j < s; j++)
            sum += output[i][j];
      }
      System.out.println("Points obtained from cp.getArray():");
      System.out.println("Average of all coordinates = " + sum / (n * s) + "\n");
   }

   public static void testSobolLMS(int k, int s, int w, RandomStream stream) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      // RandomStream stream = new LFSR258();
      // RandomStream stream = new LFSR113();
      // RandomStream stream = new MRG32k3a();
      // ((MRG32k3a())stream).increasedPrecision();
      stream.resetStartStream();      
      DigitalNetBase2 p = new SobolSequence(k, w, s); // n = 2^{k} points in s dim., w=5.
      System.out.println("********************************\nSobol + LMS:");
      System.out.println(stream.toString());
      System.out.println("The generator matrices for the points p before LMS:");
      p.printGeneratorMatricesColumns(s);
      PointSetRandomization randlms = new LMScrambleShift(stream);  // Number of bits that are scrambled.
      randlms.randomize(p);
      System.out.println("The generator matrices for the points p AFTER LMS:");
      p.printGeneratorMatricesBits(s);
      p.printGeneratorMatricesColumns(s);
      System.out.println("The randomized points:");
      System.out.println(p.formatPoints());

      CachedPointSet cp = new CachedPointSet(p);
      cp.sortByCoordinate(0);
      System.out.println("The cached points, sorted:");
      System.out.println(cp.formatPoints());

      PointSetIterator streamP = cp.iterator();
      double[] point = new double[s];
      double sum = 0.0;
      streamP.resetStartStream();
      for (int i = 0; i < n; i++) {
         streamP.nextPoint(point, s);
         for (int j = 0; j < s; j++)
            sum += point[j];
      }
      System.out.println("Average of all coordinates = " + sum / (n * s) + "\n");
   }

   public static void testSobolInterlace(int k, int s) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      RandomStream stream = new LFSR258();
      // RandomStream stream = new LFSR113();

      // Sobol' points
      DigitalNetBase2 p = new SobolSequence(k, k, s); // n = 2^{k} points in s dim., w=5.
      // PointSetRandomization rands = new RandomShift(stream); // Digital shift
      BakerTransformedPointSet pBaker = new BakerTransformedPointSet(p);

      // Sob-Int2 Sob-interlaced-order2
      System.out.println("\n*******************************\nDigitalNet with interlacing");
      DigitalNetBase2 p2 = new SobolSequence(k, 31, 2 * s); // n = 2^{k} points in 2s dim.
      p = p2.matrixInterlace(2, s);
      pBaker = new BakerTransformedPointSet(p);
      p2.printGeneratorMatricesBits(2 * s);
      p.printGeneratorMatricesBits(s);
      p.printGeneratorMatricesColumns(s);
      System.out.println(p.formatPoints());

   }

   public static void main(String[] args) throws IOException {
      System.out.println("\n*******************************\nTestRQMC64 ");
      int k = 4;
      int s = 1;
      int w = 30;
      // testSobol(10, 2);
      // testSobolNUS(3, 2);
      // testSobolInterlace(10, 2);

      // RandomStream stream = new LFSR258();
      // RandomStream stream = new LFSR113();
      // RandomStream stream = new MRG32k3a();
      // ((MRG32k3a())stream).increasedPrecision();
      testSobolLMS(k, s, w, new LFSR113());
      testSobolLMS(k, s, w, new LFSR258());
      testSobolLMS(k, s, w, new MRG32k3a());
      
   }
}
