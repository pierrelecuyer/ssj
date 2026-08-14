package rqmcexperiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;


import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.TallyStore;

/**
 * Generates standalone LaTeX documents that compare the distributions of the
 * average @f$A_r@f$ and median @f$M_r@f$ for SAMO 2025 experiments.
 *
 * <p>For each input data file, the program bootstraps samples of size @f$r@f$
 * and draws the histograms of @f$A_r@f$ and @f$M_r@f$ on the same PGFPlots
 * axis. Each output document contains one table per value of @f$s@f$; a table
 * may span several pages. RQMC methods appear in rows, while sample sizes
 * appear in columns, with @f$n=2^k@f$.
 */
public class HistSamo25ArMrAll {

   // Fixed parameters for this particular paper.
   static String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datacrn/";
   static String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/histogramscrn/";
     
   // Generates one LaTeX histogram document for each model listed below.
   public static void main(String[] args) throws IOException {

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
      int m = 10000;

      // Choose and initialize the random stream here. Reproducing a run
      // requires the same generator and initial seed.
      RandomStream stream = new LFSR258();
      boolean crnboot = true;  // To use common random numbers across all histograms.
      for (String modelTag : modelTags)
         HistSamo25ArMr.writeModelFile (inputFolder, outputFolder, modelTag, methods, 
               sDims, ks, m, stream, crnboot);
      System.out.println("ALL DONE");
   }
}
