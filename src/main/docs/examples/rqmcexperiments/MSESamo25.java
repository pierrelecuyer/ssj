package rqmcexperiments;

import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.util.Chrono;

/**
 * Example that uses {@link MeanMedianMSE} to generate tables for the MSEs of
 * @f$A_r@f$, @f$M_r@f$, and their ratio. For each combination of model and
 * dimension @f$s@f$, three .res files are generated. Each file contains a
 * data table whose columns are for the RQMC methods, the rows are for values of
 * @f$k = \log_2 n@f$ where @f$n@f$ is the number of RQMC points,
 * and the entries are MSE or ratio values.
 */
public class MSESamo25 {

   /**
    * Configures and runs MSE experiments with the samo25 data.
    */
   public static void main(String[] args) {
   
      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/mse/";
      // String[] modelTags = {"MC2"};
      String[] modelTags = new String[] {
            "SmoothPerB4", "SumUeU", "MC2", "Polynomial", "Oscillatory",
            "Gaussian", "SmoothGauss", "PieceLinGauss", "IndSumNormal"
      };
      int[] dims = {2, 4, 8, 16, 32};
      String[] methods = {
            "Lat-RS", "Lat-RSB", "Lat-Rv", "Lat-Rpv",
            "Lat-RvRS", "Lat-RvRSB", "Lat-RpvRS", "Lat-RpvRSB",
            "Sob-RDS", "Sob-RDSB", "Sob-LMS", "Sob-LMS-RDS",
            "Sob-LMS-RDS-IRB", "Sob-NUS"
      };
      int[] ks = {8, 10, 12, 14, 16};
      int r = 11;           // Sample size for the median estimator.

      int numObs = 10000;   // Number of observations in the input data files.
      int numReps = 10000;  // Number of bootstrap subsamples to estimate the MSE_Mr.

      RandomStream stream = new LFSR258();
      Chrono timerTotal = new Chrono();
      for (String model : modelTags)
         MeanMedianMSE.estimateMSEOneModel(inputFolder, outputFolder, model, dims,
               methods, ks, numObs, numReps, r, stream);
      System.out.println("\nTotal time for everything: " + timerTotal.format() +
            "\n=========================================== \n");
      }
}
