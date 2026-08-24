package rqmcexperiments;

import java.io.IOException;

import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;

/**
 * This program builds tables giving the quantiles of the distributions 
 * of @f$A_r@f$ and @f$M_r@f$. For each combination of model,
 * value or @f$r@f$, and dimension @f$s@f$, a {@code .csv} file is constructed
 * via the method {@link MeanMedianMSE.estimateQuantilesOneModel}.
 * 
 * The boolean `crnboot` decides if we use common random numbers (CRN) across all
 * cases, or not.
 */
public class QuantilesSamo25 {

   public static void main(String[] args) throws IOException {

      boolean crnboot = false;   // Do we want CRNs? 
      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/mse/";
      
      String[] modelTags = {"MC2"};
      String[] methods = {
            "Lat-RS", "Lat-Rv", "Lat-Rpv", "Lat-RvRS", 
            "Sob-LMS", "Sob-LMS-RDS",
      };
      int[] dims = {8};
      int[] ks =  {8, 10, 12, 14, 16};
      int[] rs = {11};
      int numObs = 10000;   // Number of observations in the input data files.
      int numReps = 10000;  // Number of bootstrap subsamples to estimate the MSE[M_r].
      RandomStream stream = new LFSR258();

      // This is to estimate the MSE for each case and put that in `mse` directory.
      for (String model : modelTags)
         for (int r : rs)
            MeanMedianMSE.estimateQuantilesOneModel(inputFolder, outputFolder, model, dims,
                methods, ks, numObs, numReps, r, stream, crnboot);

      System.out.println("\nAll Done.");
   }
}
