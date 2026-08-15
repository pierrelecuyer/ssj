package rqmcexperiments;

import java.io.IOException;

import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.util.Chrono;

/**
 * Example that uses {@link MeanMedianMSE} to generate tables for the MSEs 
 * of @f$A_r@f$, @f$M_r@f$, and their ratio. For each combination of model and
 * dimension @f$s@f$, three {@code .res} files are generated. These files contain
 * tables whose columns are the RQMC methods, whose rows are the values
 * of @f$k = \log_2 n@f$, where @f$n@f$ is the number of RQMC points, and whose
 * entries are MSE or ratio values. One {@code .csv} file is also generated for
 * each model and value of @f$r@f$; it contains the moments and MSE estimates,
 * with one row for each existing input file.
 * 
 * The boolean `crnboot` decides if we use common random numbers (CRN) across all
 * cases, or not.
 */
public class MSESamo25 {

   /**
    * Configures and runs MSE experiments with the samo25 data.
    */
   public static void main(String[] args) throws IOException {

      boolean crnboot = false;   // Do we want CRNs? 
      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/mse/";
      
      // String[] modelTags = {"MC2"};
      String[] modelTags = new String[] {
            "SmoothPerB4", "SumUeU", "MC2", "Polynomial", "Oscillatory",
            "Gaussian", "SmoothGauss", "PieceLinGauss", "IndSumNormal"
      };
      int[] dims = {1, 2, 4, 8, 16, 32};
      String[] methods = {
            "Lat-RS", "Lat-RSB", "Lat-Rv", "Lat-Rpv",
            "Lat-RvRS", "Lat-RvRSB", "Lat-RpvRS", "Lat-RpvRSB",
            "Sob-RDS", "Sob-RDSB", "Sob-LMS", "Sob-LMS-RDS",
            "Sob-LMS-RDS-IRB", "Sob-NUS"
      };
      int[] ks =  {8, 10, 12, 14, 16};
      int[] rs = {11, 63};          // Number of observations averaged or used in each bootstrap median sample.

      int numObs = 10000;   // Number of observations in the input data files.
      int numReps = 10000;  // Number of bootstrap subsamples to estimate the MSE[M_r].

      RandomStream stream = new LFSR258();
      Chrono timerTotal = new Chrono();

      // This is to estimate the MSE for each case and put that in `mse` directory.
      for (String model : modelTags)
         for (int r : rs)
            MeanMedianMSE.estimateMSEOneModel(inputFolder, outputFolder, model, dims,
                methods, ks, numObs, numReps, r, stream, crnboot);
                
      // This part is to generate tables to plot the MSEs as functions of r.
      // String[] modelTags2 = new String[] {"MC2"};
      int[] dims2 = {4, 16};
      int[] ks2 =  {10, 14};
      int[] rs2 =  {11, 23, 47, 95, 189};
      for (String model : modelTags)
         MeanMedianMSE.estimateMSEManyr(inputFolder, outputFolder, model, dims2,
               methods, ks2, numObs, numReps, rs2, stream, crnboot);

      // Grouping by category to make scatter plots.
      String[] modelTags3 = new String[] {
            "SmoothPerB4", "SumUeU", "MC2", "SmoothGauss", "PieceLinGauss", "IndSumNormal"
      };
      String[] methodsa = {
            "Lat-RS", "Lat-RSB", "Sob-RDS", "Sob-RDSB", "Sob-NUS"
      };
      String[] methodsb = {
            "Lat-RvRS", "Lat-RvRSB", "Lat-RpvRS", "Lat-RpvRSB", "Sob-LMS-RDS"
      };
      String[] methodsc = {
            "Lat-Rv", "Lat-Rpv", "Sob-LMS"
      };
      int[] dims3 = {2, 4, 8, 16, 32};
      int[] ks3 = {10, 12, 14, 16}; 
      int[] rs3 = {5, 11, 63};       
      for (int r : rs3) {
         MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "a", methodsa,
            modelTags3, dims3, ks3, numObs, numReps, r, stream, crnboot);
         MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "b", methodsb,
            modelTags3, dims3, ks3, numObs, numReps, r, stream, crnboot);
         MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "c", methodsc,
            modelTags3, dims3, ks3, numObs, numReps, r, stream, crnboot);
      }
      
      String[] modelTagsPieceLinGaus = new String[] {"PieceLinGauss"};
      String[] modelTagsIndSumNormal = new String[] {"IndSumNormal"};
      
      int r = 11;
      MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "a-PieceLinGauss", methodsa,
            modelTagsPieceLinGaus, dims3, ks3, numObs, numReps, r, stream, crnboot);
      MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "b-PieceLinGauss", methodsb,
            modelTagsPieceLinGaus, dims3, ks3, numObs, numReps, r, stream, crnboot);
      MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "c-PieceLinGauss", methodsc,
            modelTagsPieceLinGaus, dims3, ks3, numObs, numReps, r, stream, crnboot);

      MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "a-IndSumNormal", methodsa,
            modelTagsIndSumNormal, dims3, ks3, numObs, numReps, r, stream, crnboot);
      MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "b-IndSumNormal", methodsb,
            modelTagsIndSumNormal, dims3, ks3, numObs, numReps, r, stream, crnboot);
      MeanMedianMSE.estimateMSEOneCategory(inputFolder, outputFolder, "c-IndSumNormal", methodsc,
            modelTagsIndSumNormal, dims3, ks3, numObs, numReps, r, stream, crnboot);

      System.out.println("\nTotal time for everything: " + timerTotal.format() +
            "\n=========================================== \n");
   }
}
