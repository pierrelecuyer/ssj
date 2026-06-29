package rqmcexperiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.stat.TallyStore;
import umontreal.ssj.util.Misc;

/**
 * Suppose we have a large number of independent observations from a given distribution
 * and we want to estimate the true mean of the distribution by picking a small number
 * @f$r@f$ of observations at random from the large set, with replacement, and taking either their
 * average @f$A_r@f$ or their median @f$M_r@f$ as an estimator of the mean.
 * We want to estimate the MSE of @f$A_r@f$ and @f$M_r@f$ and
 * the ratio @f$\mathrm{MSE}[A_r] / \mathrm{MSE}[M_r]@f$ to compare them.
 * The true mean is assumed to be known and given by the variable `ExactMean`.
 * It is zero by default, but can be changed via `setExactMean`.
 * To estimate the MSEs, we pick @f$r@f$ observations at random from the set
 * and compute the squares @f$A_r^2@f$ and @f$M_r^2@f$, repeat that @f$m@f$ times,
 * and take the averages. The method `computeMseArMr` does that. 
 * 
 * This class is organized to do this not only for a single distribution (data set), but for a large 
 * collection of data sets that are stored in data files in exactly the same way and with the
 * same naming convention as for the class `HistCollectionLatex.java`.
 * The top-level entry method is {@link computeFolderMSE}. It takes the input and output directories, 
 * a model tag (name), the number @f$s@f$ of dimensions, 
 * a set of RQMC method names, a set of values of @f$k@f$, the number of observations per input file, 
 * the number @f$m@f$ of replications to estimate the MSE, the value of @f$r@f$, and a random stream.
 * The input file names to search will be constructed based on this information. 
 * After estimating the two MSEs for each method and each @f$k@f$, three `.res` files will be created 
 * for this model and value of @f$s@f$, each one containing a table whose columns 
 * correspond to RQMC methods, the rows are for the values of @f$k@f$,
 * and the entries are the MSE or ratio values.  
 */
public class MeanMedianMSE {

   public static double ExactMean = 0.0;

   /**
    * Computes the MSE from a tally of observations.
    *
    * @param tally tally containing the observations
    * @return estimated MSE relative to {@link #ExactMean}
   */
   public static double mse(Tally tally) {
      double bias = tally.average() - ExactMean;
      return tally.variance() + bias * bias;
   }

   /**
    * Reads observations from an input data file and puts then in an array.
    * 
    * @param filename input data file
    * @return array containing the observations
    * @throws IllegalArgumentException if the file contains no observations
   */
   public static double[] readDataValues(String filename) {
      TallyStore tally = new TallyStore();
      // Use fillFromFile(filename, skip) if the file contains comments.
      tally.fillFromFile(filename);
      if (tally.numberObs() == 0)
         throw new IllegalArgumentException("No data values found in " + filename);
      return tally.getArray();
   }

   /**
    * Performs bootstrap simulations to obtain realizations of @f$A_r@f$ and @f$M_r@f$,
    * and store them in `statAver` and `statMed`.
    * 
    * **********  Better to re-use the same collectors and return them, so we can also 
    *             look at the average, kurtosis, etc. if desired.         ***********************  
    *
    * @param values input data observations
    * @param m number of bootstrap samples
    * @param r size of each bootstrap sample
    * @param stream random stream used for sampling
    * @return array containing the MSE of @f$A_r@f$ and @f$M_r@f$
   */
   public static void computeMSEArMr(double[] values, int m, int r, RandomStream stream,
         TallyStore statAver, TallyStore statMed) {
      int numSim = values.length;
      double[] sample = new double[r];
      for (int i = 0; i < m; i++) {
         double sum = 0.0;
         for (int j = 0; j < r; j++) {
            double value = values[stream.nextInt(0, numSim - 1)];
            sample[j] = value;
            sum += value;
         }
         statAver.add(sum / r);
         statMed.add(Misc.getMedian(sample, r));
      }
   }

   /**
    * Writes a formatted result table to a file.
    *
    * @param file output file
    * @param table formatted table
    * @throws RuntimeException if the file cannot be written
    */
   public static void writeTable(File file, String table) {
      try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
         out.print(table);
      } catch (IOException e) {
         throw new RuntimeException("Could not write " + file.getAbsolutePath(), e);
      }
   }

   /**
    * Constructs the input data file name for one model, dimension, method, and
    * value of @f$k@f$.
    *
    * @param inputFolder directory containing the input data files
    * @param model model name used in the filename
    * @param s model dimension
    * @param method RQMC method name
    * @param k value of @f$k@f$
    * @param numObs number of observations contained in the filename
    * @return the data file, or `null` if it is missing
    */
   public static File getDataFile(String inputFolder, String model,
         int s, String method, int k, int numObs) {
      String fileName = model + "-" + s + "-" + method
            + "-" + k + "-" + numObs + ".dat";
      File file = new File(inputFolder, fileName);
      if (!file.isFile()) {
         System.out.println("Missing file: " + file.getAbsolutePath());
         return null;
      }
      return file;
   }

   /**
    * Computes and writes the three MSE tables for one model and dimension.
    * Missing input files are reported and skipped.
    *
    * @param inputFolder directory containing the input data files
    * @param outputFolder directory in which result tables are written
    * @param model model name used in input filenames
    * @param s model dimension
    * @param methods RQMC method names
    * @param ks values of @f$k@f$
    * @param numObs number of observations identified in each filename
    * @param m number of bootstrap samples
    * @param r size of each bootstrap sample
    * @param stream random stream used for sampling
    * @throws IllegalArgumentException if the result directory cannot be created
    */
   public static void computeFolderMSE(String inputFolder, String outputFolder,
         String model, int s,
         String[] methods, int[] ks, int numObs,
         int m, int r, RandomStream stream) {

      File resultFolder = new File(outputFolder);
      if (!resultFolder.exists() && !resultFolder.mkdirs())
         throw new IllegalArgumentException(
               "Could not create result folder " + resultFolder.getAbsolutePath());
      StringBuilder header = new StringBuilder(" k ");
      for (String method : methods)
         header.append(" ").append(method).append(" ");
      header.append("\n");

      StringBuilder arRows = new StringBuilder();
      StringBuilder mrRows = new StringBuilder();
      StringBuilder ratioRows = new StringBuilder();
      
      TallyStore statAver = new TallyStore("A_r");
      TallyStore statMed = new TallyStore("M_r");
      for (int i = 0; i < ks.length; i++) {
         int k = ks[i];
         // Use a new substream for this k.
         stream.resetNextSubstream();
         arRows.append(k).append("  ");
         mrRows.append(k).append("  ");
         ratioRows.append(k).append("  ");

         for (String method : methods) {
            File file = getDataFile(
                  inputFolder, model, s, method, k, numObs);
            if (file == null) {
               arRows.append("Missing  ");
               mrRows.append("Missing  ");
               ratioRows.append("Missing  ");
               continue;
            }
            double[] values = readDataValues(file.getAbsolutePath());
            // Reuse the same substream for every method at this k.
            stream.resetStartSubstream();
            computeMSEArMr(values, m, r, stream, statAver, statMed);
            double arMse = mse(statAver);
            double mrMse = mse(statMed);        
            arRows.append(arMse).append("  ");
            mrRows.append(mrMse).append("  ");
            ratioRows.append(arMse / mrMse).append("  ");
         }
         arRows.append("\n");
         mrRows.append("\n");
         ratioRows.append("\n");
      }
      String arTable = header.toString() + arRows.toString();
      String mrTable = header.toString() + mrRows.toString();
      String ratioTable = header.toString() + ratioRows.toString();
      File arFile = new File(resultFolder,
            model + "-" + s + "-" + r + "-MSE-Ar.res");
      File mrFile = new File(resultFolder,
            model + "-" + s + "-" + r + "-MSE-Mr.res");
      File ratioFile = new File(resultFolder,
            model + "-" + s + "-" + r + "-MSE-Ratio-ArOverMr.res");
      writeTable(arFile, arTable);
      writeTable(mrFile, mrTable);
      writeTable(ratioFile, ratioTable);

      System.out.println("MSE result tables written to:");
      System.out.println("  " + arFile.getAbsolutePath());
      System.out.println("  " + mrFile.getAbsolutePath());
      System.out.println("  " + ratioFile.getAbsolutePath());
   }

}
