package rqmcexperiments;

import cern.colt.Arrays;
import umontreal.ssj.hups.DigitalNetBase2;
import umontreal.ssj.hups.LMScrambleShift;
import umontreal.ssj.hups.PointSet;
import umontreal.ssj.hups.PointSetIterator;
import umontreal.ssj.hups.PointSetRandomization;
import umontreal.ssj.hups.SobolSequence;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.TallyStore;
import umontreal.ssj.util.PrintfFormat;

public class TestLMSScramble {

   public static void main(String[] args) {
      int dim = 1;
      int k = 8;
      // int n = 8;
      double normFactor = 1.0 / ((double) (1L << (31)));

      // Sob-LMS
      RandomStream stream = new LFSR113();
      DigitalNetBase2 ptSet = new SobolSequence(8, 31, 2);
      // PointSetRandomization lms = new LMScrambleShift(stream);   
      PointSetIterator pts = ptSet.iterator();

      System.out.println("Original matrices: " + PrintfFormat.NEWLINE);
      ptSet.printGeneratorMatrices(2);
      ptSet.printGeneratorMatricesBits(2);
      
      System.out.println("leftMatrixScrambleSubdiag(stream)" + PrintfFormat.NEWLINE);
      ptSet.leftMatrixScrambleSubdiag(stream);
      ptSet.printGeneratorMatrices(2);
      ptSet.printGeneratorMatricesBits(2);
      
      System.out.println("Original matrices: " + PrintfFormat.NEWLINE);
      ptSet.printOriginalMatrices(2);
 
      System.out.println("leftMatrixScramble(stream)" + PrintfFormat.NEWLINE);
      // ptSet = new SobolSequence(8, 31, 2);
      stream.resetStartStream();
      ptSet.leftMatrixScramble(stream);
      ptSet.printGeneratorMatrices(2);
      ptSet.printGeneratorMatricesBits(2);
      
      System.out.println("Original matrices: " + PrintfFormat.NEWLINE);
      ptSet.printOriginalMatrices(2);
 
      System.out.println("leftMatrixScramble(31, stream)" + PrintfFormat.NEWLINE);
      // ptSet = new SobolSequence(8, 31, 2);
      stream.resetStartStream();
      ptSet.leftMatrixScramble(31, stream);
      ptSet.printGeneratorMatrices(2);
      ptSet.printGeneratorMatricesBits(2);
      
      
/*      double[] u = new double[dim];
      for (int i = 0; i < n; i++) {
         ptSet.nextPoint(u, dim);
         System.out.println(Integer.toString(i) + ": " + Arrays.toString(u));
      }
*/
      
   }
}
