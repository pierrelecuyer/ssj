package rqmcexperiments;

import java.io.IOException;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
 
/**
 * Uses the tools in `MeanMedianMSE` and `HistSamo25ArMr` to make bootstrap samples and
 * produce standalone LaTeX histograms that compare the distributions of the
 * average @f$A_r@f$ and median @f$M_r@f$ for SAMO 2025 experiments, for all the cases.
 * This provides a very large collection of histograms.
 * There is one output document for each model and it contains one table of histograms 
 * per value of @f$s@f@.  
 */
public class HistSamo25ArMrAll {
   
   // Generates one LaTeX histogram document for each model listed below.
   public static void main(String[] args) throws IOException {

      boolean crnboot = false;  // To use common random numbers across all histograms.
      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/histograms/";
    
      String[] modelTags = {
         "SmoothPerB4", "SumUeU", "MC2", "Polynomial", "Oscillatory",
         "Gaussian", "SmoothGauss", "PieceLinGauss", "IndSumNormal"
      };
      String[] methods = {
         "Lat-RS", "Lat-RSB", "Lat-Rv", "Lat-Rpv", "Lat-RvRS", "Lat-RvRSB",
         "Lat-RpvRS", "Lat-RpvRSB", "Sob-RDS", "Sob-RDSB", "Sob-LMS", "Sob-LMS-RDS",
         "Sob-LMS-RDS-IRB", "Sob-NUS"
      };
      int[] sDims = {2, 4, 8, 16, 32};
      int[] ks = {10, 12, 14, 16};

      int r = 11;
      int numBins = 100;
      int numObs = 10000;   // Number of observations in the input data files.
      int numReps = 10000;  // Number of bootstrap subsamples of A_r and M_r.
      int[] marks = new int[] {0, 99, 499, numReps-1, numReps-100, numReps-500}; // This is for 10^4 reps.
      RandomStream stream = new LFSR258();      // Maybe reset the main seed ??? 

      for (String modelTag : modelTags)
         HistSamo25ArMr.writeModelFile (inputFolder, outputFolder, modelTag, methods, 
               sDims, ks, numReps, r, numObs, numBins, marks, stream, crnboot);
      System.out.println("\nALL DONE!");
   }
}
