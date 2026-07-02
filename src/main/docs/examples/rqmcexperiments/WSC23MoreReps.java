package rqmcexperiments;

import java.io.*;
import umontreal.ssj.mcqmctools.RQMCExperiment64;
import umontreal.ssj.util.Chrono;

/**
 * New version of the main program to generate and store large RQMC samples
 * for various functions and various methods, that was used for WSC 2023 paper.
 * This program uses the class `WSC23MoreSamples.java`, which uses `hups64`.
 */
public class WSC23MoreReps extends RQMCExperiment64 {

   public static void main(String[] args) throws IOException {
      // WSC26RQMCSamples64.directory = "C:/Users/Lecuyer/Dropbox/wsc26/data64/"; // Retained for 64 bits
      // WSC23MoreSamples.directory = "C:/Users/Lecuyer/Dropbox/wsc23/test/";        // For testing
      WSC23MoreSamples.directory = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";        // For Samo paper

      Chrono timerTotal = new Chrono();
      for (int s = 1; s <= 1; s *= 2) {
      // for (int s = 2; s <= 32; s *= 2) {
         System.out.println("WSC23MoreReps, run with s = " + s);

         //  RQMCSamples23.redirectToFile((model.getTag() + "-" + s));        
         int m = 10000; // Number of RQMC randomizations.
         // int m = 10; // Number of RQMC randomizations.
         int mink = 8;
         int maxk = 16;
         
         // Uncomment the models you want to run. ***
         //WSC23MoreSamples.simulRepsAllSizes(new SmoothPerBeta52(s, 1.0), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new SmoothPerB4(s, 1.0), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new SumUeU(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new MC2(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new Polynomial(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new Oscillatory(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new Gaussian(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new SmoothGauss(s), s, mink, maxk, m);
         //WSC23MoreSamples.simulRepsAllSizes(new RidgeJohnsonSU(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new PieceLinGauss(s), s, mink, maxk, m);
         WSC23MoreSamples.simulRepsAllSizes(new IndSumNormal(s), s, mink, maxk, m);
         //WSC23MoreSamples.simulRepsAllSizes(new IndBox(s), s, mink, maxk, m);
              
      }
      System.out.println("Total time for everything: " + timerTotal.format() +
            "\n=========================================== \n");
   }
}
