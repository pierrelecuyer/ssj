package rqmcexperiments;

import java.io.*;
import umontreal.ssj.hups.*;
import umontreal.ssj.mcqmctools.*;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Chrono;

// Tools to generate and store RQMC replicates for WSC 2024 paper

public class WSC24RQMCSamples extends RQMCExperiment {

   // static String directory = "src/main/resources/wsc24/";
   // static String directory = "C:/Users/Lecuyer/Dropbox/wsc24/RepsRQMC/";
   static String directory = "C:/Users/Lecuyer/Documents/conf/wsc24/wsc24wafom/";

   /**
    * Writes a summary report with mean, variance, etc., to a .sum file with the
    * given name, in given directory.
    */
   public static void reportToFile(TallyStore tally, String fileName) throws IOException {
      FileWriter file = new FileWriter(directory + fileName + ".sum");
      file.write(tally.reportAndCIStudent(0.95, 8));
      file.close();
   }

   /**
    * Returns the observations stored in this object as a `String`, with a line
    * feed after each observation.
    */
   public static String dataToString(MonteCarloModelDouble[] models, TallyStore[] tally) {
      int numModels = models.length;
      double[][] array = new double[numModels][];
      StringBuilder sb = new StringBuilder();
      for (int model = 0; model < numModels; model++) { // Each model
         sb.append("  " + models[model].getTag());
         array[model] = tally[model].getArray();
      }
      sb.append("\n");
      for (int i = 0; i < tally[0].numberObs(); i++) { // One line per observation
         for (int model = 0; model < numModels; model++)
            sb.append(array[model][i] + "  ");
         sb.append("\n");
      }
      return sb.toString();
   }

   /**
    * With the digital net p, performs numLMS independent LMS. For each LMS and
    * each of the numModels in models, run RQMC with numRDS independent RDS to
    * estimate the variance with RDS only. The variance estimates for each model
    * and each LMS are stored in statVariances. When numLMS==1, we do no LMS, only
    * RDS.
    */
   public static void simulCondVar(DigitalNetBase2 p, RandomStream noise, int numLMS, int numRDS,
         MonteCarloModelDouble[] models, TallyStore[] statVariances, TallyStore[] statAverages) {
      int numModels = models.length;
      int model;
      for (model = 0; model < numModels; model++) // Each model
         statVariances[model].init(); // Variances for different LMS, for each model.
      Tally statReps = new Tally(); // Used within one LMS.
      Tally statValue = new Tally(); // Used within each RQMC estimate.
      int n = p.getNumPoints();
      PointSetIterator stream = p.iterator();
      Chrono timer = new Chrono();
      for (int lms = 0; lms < numLMS; lms++) { // Each LMS.
         if (numLMS > 1) // If = 1, we do no LMS, only RDS.
            p.leftMatrixScramble(noise);
         // Here we use the same LMS but independent RDS's across models.
         for (model = 0; model < numModels; model++) { // Each model
            statReps.init();
            for (int r = 0; r < numRDS; r++) { // Each RDS
               p.addRandomShift(noise);
               stream.resetStartStream(); // This stream iterates over the points.
               statValue.init();
               simulateRuns(models[model], n, stream, statValue);
               statReps.add(statValue.average()); // For the estimator of the mean.
            }
            statAverages[model].add(statReps.average());
            // statVariances[model].add(statReps.variance());
            statVariances[model].add(Math.log10(statReps.variance())); // Log_10 of variance
         }
         System.out.println(" LMS " + lms);
      }
      for (model = 0; model < numModels; model++) { // Each model
         System.out.println(statVariances[model].reportAndCIStudent(0.95, 8));
         System.out.println(statAverages[model].reportAndCIStudent(0.95, 8));
      }
      System.out.println("CPU time: " + timer.format() + "\n");
   }

   /**
    * We take n=2^k Sobol points and the models in `models` in s dimensions, call
    * `simulCondVar` with that, and print the resulting variances in a file, one
    * column per model and one row per LMS, in a format ready for pgfplot.
    */
   public static void simulRepsCondLMS(int s, int k, int numLMS, int numRDS, MonteCarloModelDouble[] models,
         String dirnum) throws IOException {
      RandomStream noise = new MRG31k3p();
      System.out.println("Cond variance estimates with different models.\n\n");
      Chrono timer = new Chrono();
      int numModels = models.length;
      TallyStore statVariances[] = new TallyStore[numModels];
      TallyStore statAverages[] = new TallyStore[numModels];
      for (int model = 0; model < numModels; model++) { // Each model
         String modelTag = models[model].getTag();
         statVariances[model] = new TallyStore(modelTag + "-condLMS" + "-" + s + "-" + k);
         statAverages[model] = new TallyStore(modelTag + "-condLMS-average-" + "-" + s + "-" + k);
      }
      DigitalNetBase2 p; // n = 2^{k} points in s dim.
      if (dirnum == "jk")
         p = new SobolSequence(directory + "new-joe-kuo-6.21201", k, 31, s);
      else
         p = new SobolSequence(k, 31, s);
      simulCondVar(p, noise, numLMS, numRDS, models, statVariances, statAverages);
      FileWriter file = new FileWriter(
            directory + "results/condLMS-" + dirnum + "-" + s + "-" + k + "-" + numLMS + ".res");
      file.write(dataToString(models, statVariances));
      file.close();
      // FileWriter file2 = new FileWriter(directory + "condLMS-Averages-" + s + "-" +
      // k + "-" + numLMS + ".res");
      // file2.write(dataToString(models, statAverages));
      // file2.close();
      System.out.println(
            "Total time for simulRepsCondLMS: " + timer.format() + "\n=========================================== \n");
   }

   /**
    * Returns the observations stored in this object as a `String`, with a line
    * feed after each observation.
    */
   public static String dataToStringTruncLMS(MonteCarloModelDouble[] models, TallyStore[] tally) {
      int numModels = models.length;
      double[][] array = new double[numModels][];
      StringBuilder sb = new StringBuilder();
      for (int model = 0; model < numModels; model++) { // Each model
         sb.append("  " + models[model].getTag());
         array[model] = tally[model].getArray();
      }
      sb.append("\n");
      for (int i = 0; i < tally[0].numberObs(); i++) { // One line per observation
         for (int model = 0; model < numModels; model++)
            sb.append(array[model][i] + "  ");
         sb.append("\n");
      }
      return sb.toString();
   }

   /**
    * We take n=2^k Sobol points and the models in `models` in s dimensions, call
    * `simulCondVar` with that, and print the resulting variances in a file, one
    * column per model and one row per LMS, in a format ready for pgfplot. `dirnum`
    * must be `jk` or `lem`. If "jk" it takes the direction numbers from Joe and
    * Kuo, otherwise it takes the default values, from Lemieux et al.
    */
   public static void simulTruncLMS(int s, int mink, int maxk, int numReps, MonteCarloModelDouble[] models,
         String dirnum) throws IOException {
      int numModels = models.length;
      int numSizes = maxk - mink + 1;
      Tally statValue = new Tally(); // Used within each RQMC estimate.
      Tally statReps = new Tally("statReps");
      double[][] statVariancesRDS = new double[numModels][numSizes];
      double[][] statVariancesLMS = new double[numModels][numSizes];
      double[][] statAveragesLMS = new double[numModels][numSizes];
      RandomStream noise = new MRG31k3p();
      DigitalNetBase2 p;
      System.out.println("Truncated LMS variance rates with different models.\n\n");
      Chrono timer = new Chrono();
      for (int k = mink; k <= maxk; k++) { // Each digital net size
         System.out.println("k = " + k);
         if (dirnum == "jk")
            p = new SobolSequence(directory + "new-joe-kuo-6.21201", k, 31, s);
         else
            p = new SobolSequence(k, 31, s);
         PointSetIterator stream = p.iterator();
         int n = p.getNumPoints();
         for (int model = 0; model < numModels; model++) { // Each model
            statReps.init();
            for (int r = 0; r < numReps; r++) { // Each RQMC replicate with RDS
               p.addRandomShift(noise);
               stream.resetStartStream(); // This stream iterates over the points.
               simulateRuns(models[model], n, stream, statValue);
               statReps.add(statValue.average()); // Only RDS
            }
            statVariancesRDS[model][k - mink] = Math.log10(statReps.variance()); // Log_10 of variance
            // System.out.println(statReps.report());
         }
         for (int model = 0; model < numModels; model++) { // Each model
            statReps.init();
            for (int r = 0; r < numReps; r++) { // Each RQMC replicate with RDS
               p.leftMatrixScramble(noise);
               p.addRandomShift(noise);
               stream.resetStartStream();
               simulateRuns(models[model], n, stream, statValue);
               statReps.add(statValue.average()); // For LMS + RDS.
            }
            statVariancesLMS[model][k - mink] = Math.log10(statReps.variance());
            statAveragesLMS[model][k - mink] = statReps.average();
            // System.out.println(statReps.report());
         }
      }
      StringBuilder sb = new StringBuilder();
      sb.append(" k ");
      for (int model = 0; model < numModels; model++)
         sb.append("   " + models[model].getTag() + "-RDS     " + models[model].getTag() + "-LMS     "
               + models[model].getTag() + "-Aver  ");
      sb.append("\n");
      for (int i = 0; i < numSizes; i++) { // One line per value of k
         sb.append(mink + i + "  ");
         for (int model = 0; model < numModels; model++)
            sb.append(statVariancesRDS[model][i] + "  " + statVariancesLMS[model][i] + "  " + statAveragesLMS[model][i]
                  + "  ");
         sb.append("\n");
      }
      System.out.println(sb.toString());
      FileWriter file = new FileWriter(directory + "results24/truncLMS-" + dirnum + "-" + s + "-" + numReps + ".res");
      file.write(sb.toString());
      file.close();
      System.out.println(
            "Total time for simulRepsCondLMS: " + timer.format() + "\n=========================================== \n");
   }

}
