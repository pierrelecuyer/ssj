package rqmcexperiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.TallyStore;

/**
 * Uses the tools in `MeanmedianMSE` and `HistSamo25ArMr` to make bootstrap samples and
 * produce standalone LaTeX histograms that compare the distributions of the
 * average @f$A_r@f$ and median @f$M_r@f$ for SAMO 2025 experiments.
 * This is for the selected histograms that go in the main paper.
 */
public class HistSamo25ArMrPaper {
 
   /**
    * Sets the parameters and writes the histogram LaTeX files for the SAMO paper
    */
   public static void main(String[] args) throws IOException {

      boolean crnboot = true;  // `true` means we use common random numbers.
      // Input data is taken from `inputFolder` and `latex code is put in `outputFolder`.
      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datacrn/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/histogramscrn/";
      
      String[] fileNames = new String[] {
         "SmoothPerB4-8-Lat-RS-16-10000", "SmoothPerB4-8-Lat-RvRS-16-10000", 
         "SmoothPerB4-8-Lat-RpvRS-16-10000", "SmoothPerB4-8-Sob-RDS-16-10000",
         "SmoothPerB4-8-Sob-LMS-RDS-16-10000", "SmoothPerB4-8-Sob-NUS-16-10000",
         "MC2-8-Sob-LMS-RDS-16-10000", "MC2-16-Sob-LMS-RDS-14-10000",
         "MC2-16-Sob-NUS-14-10000"
      };
      String[] titleNames = new String[] {
            "Lat-RS", "Lat-RvRS", "Lat-RpvRS","Sob-RDS", "Sob-LMS-RDS", "Sob-NUS",
            "Sob-LMS-RDS, $s=8$, $k=16$", "Sob-LMS-RDS, $s=16$, $k=14$",
            "Sob-NUS, $s=16$, $k=14$"
         };
      // Fixed parameters for this particular paper.
      int r = 11;
      int numBins = 100;
      int numObs = 10000;   // Number of observations in the input data files.
      int[] marks = new int[] {0, 99, 499, numObs-1, numObs-100, numObs-500};   // This is for 10^4 obs.
      int numReps = 10000;  // Number of bootstrap subsamples of A_r and M_r.
      RandomStream stream = new LFSR258();      // Maybe set the main seed ??? 
      
      TallyStore tallyInput = new TallyStore();   // The values of X.
      TallyStore statAver = new TallyStore();     // The values of A_r
      TallyStore statMed = new TallyStore();      // The values of M_r
      for (int i = 0; i < fileNames.length; i++) {  // Draw histograms for each case.
         System.out.println("makeDoublestogramLatex: " + fileNames[i]);  // Optional
         tallyInput.fillFromFile(inputFolder + fileNames[i] + ".dat");
         if (crnboot) stream.resetStartStream();
         MeanMedianMSE.bootstrapArMrValues(tallyInput, numReps, r, stream, statAver, statMed);       
         String latexCode = HistSamo25ArMr.makeDoubleHistogramLatex(statAver, statMed, 
               titleNames[i], numObs, r, numBins, marks);
         File outFile = new File(outputFolder, fileNames[i] + "-ArMr-hist.tex");  
         try (PrintWriter out = new PrintWriter(new FileWriter(outFile))) {
            out.print(latexCode);
            System.out.println("Hist printed to file: " + fileNames[i]);
         } catch (IOException e) {
            throw new RuntimeException("Could not write " + outFile.getAbsolutePath(), e);
         }
         
      }
      System.out.println("\nALL DONE!");
   }
}
