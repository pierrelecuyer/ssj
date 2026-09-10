package rqmcexperiments;

import java.io.*;
import umontreal.ssj.mcqmctools.RQMCExperiment64;
import umontreal.ssj.util.Chrono;
import java.util.Arrays;

/**
 * New version of the main program to generate and store large RQMC samples
 * for various functions and various methods, that was used for WSC 2023 paper.
 * This program uses the class `WSC23MoreSamples.java`, which uses `hups64`.
 */
public class Samo25SamplesMain extends RQMCExperiment64 {

   public static void main(String[] args) throws IOException {
      // Samo25Samples64.directory = "C:/Users/Lecuyer/Dropbox/wsc26/data64/"; // Retained for 64 bits
      // Samo25Samples.directory = "C:/Users/Lecuyer/Dropbox/wsc23/test/";     // For testing
      // Samo25Samples.directory = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";    // For Samo paper, no CRNs.
      Samo25Samples.directory = "C:/Users/Lecuyer/Dropbox/samo25/datacrn/";    // For Samo paper, with CRNs.

      boolean crn = true;  // Use common random numbers across all cases for randomizations.
      
      int m = 10000; // Number of RQMC randomizations.
      // int million = 10000000; // Number of RQMC randomizations.
      // int m = 10; // Number of RQMC randomizations.
      int mink = 8;
      int maxk = 16;
      
      // System.out.println("Generating vector for lattices: " + Arrays.toString(Samo25Samples.a18));
      Chrono timerTotal = new Chrono();
      
      for (int s = 1; s <= 32; s *= 2) {
      // for (int s = 2; s <= 32; s *= 2) {
         // System.out.println("WSC23MoreReps, run with s = " + s);      
         // Uncomment the models you want to run. ***
         // Samo25Samples.simulRepsAllSizes(new SmoothPerBeta52(s, 1.0), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new SmoothPerB4(s, 1.0), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new SumUeU(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new MC2(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new Polynomial(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new Oscillatory(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new Gaussian(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new SmoothGauss(s), s, mink, maxk, m, crn);
         // Samo25Samples.simulRepsAllSizes(new RidgeJohnsonSU(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new PieceLinGauss(s), s, mink, maxk, m, crn);
         Samo25Samples.simulRepsAllSizes(new IndSumNormal(s), s, mink, maxk, m, crn);
         // Samo25Samples.simulRepsAllSizes(new IndBox(s), s, mink, maxk, m, crn);
         // Samo25Samples.simulRepsLatRv(new SumUeU(s), s, mink, m, crn);
         // Samo25Samples.simulRepsSelectedTypes(new SumUeU(s), s, 10, m, crn);
      }
      
      // Samo25Samples.simulRepsSpecificCases (million, crn);
      // Samo25Samples.simulTrace (m, crn);

      System.out.println("Total time for everything: " + timerTotal.format() +
            "\n=========================================== \n");
   }
}
