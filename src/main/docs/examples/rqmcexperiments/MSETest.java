package rqmcexperiments;

import java.io.*;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;

public class MSETest {

   public static void main(String[] args) throws IOException {
      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/mse/";
      
      String[] modelTags = {"MC2"};
      int[] dims = {4};
      String[] methods = {
            "Lat-RvRS"
      };
      int[] ks =  {10};
      int[] rs = {11};          // Number of observations averaged or used in each bootstrap median sample.

      int numObs = 10000;   // Number of observations in the input data files.
      int numReps = 10000;  // Number of bootstrap subsamples to estimate the MSE[M_r].

      RandomStream stream = new LFSR258();

      System.out.println("Starting MSETest ");     
      for (String model : modelTags)
         for (int r : rs)
            MeanMedianMSE.quantilesAndMSEArMrOneModel(inputFolder, outputFolder, model, dims,
                methods, ks, numObs, numReps, r, stream, false);
      }
}
