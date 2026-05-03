package rqmcexperiments;

import java.io.*;

import umontreal.ssj.hups64.*;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.mcqmctools.RQMCExperiment64;
import umontreal.ssj.randvar.NormalGen;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stochprocess.*;
import umontreal.ssj.util.Chrono;

/**
 * This is the main program used to make experiments for the WSC 2026 paper on RQMC.
 * It makes RQMC replicates for selected functions, a fixed list of RQMC methods
 * specified in `WSC26RQMCSamples64`, for selected numbers of dimensions s
 * and numbers of points @f$2^k@f$. The output is a set of files, two for each function 
 * and dimension, that contain data sets with the log-variance and the mean for each RQMC estimator
 * (each column) and each value of `k` (each row). 
 */

public class WSC26RepsRQMC64 extends RQMCExperiment64 {

   public static void main(String[] args) throws IOException {

      // WSC26RQMCSamples64.directory = "C:/Users/Lecuyer/Documents/conf/wsc26/mydata/";
      // WSC26RQMCSamples64.directory = "C:/Users/Lecuyer/Dropbox/wsc26/data/";   // Retained for 32 bits
      // WSC26RQMCSamples64.directory = "C:/Users/Lecuyer/Dropbox/wsc26/data64/"; // Retained for 64 bits
      WSC26RQMCSamples64.directory = "C:/Users/Lecuyer/Dropbox/wsc26/test/";  // For testing

      MonteCarloModelDouble model;
      Chrono timerTotal = new Chrono();
      RandomStream noise = new LFSR258();

      // For Asian Option model.
      double strike = 92.0;
      double s0 = 100.0;
      double r = 0.05;
      double sigma = 0.5;
      NormalGen gen = new NormalGen(new LFSR258());  // For Asian only
  
      for (int s = 2; s <= 2; s *= 2) {
      // for (int s = 4; s <= 32; s *= 2) {
         System.out.println("WSC26RepsRQMC64, hups64, run with s = " + s);
         // Uncomment the model you want below. ***
         // model = new SmoothPerBeta52(s, 1.0);
         // model = new SmoothPerB4(s, 1.0);
         // model = new SumUeU(s);
         // model = new MC2(s);
         // model = new Polynomial(s);
         // model = new Oscillatory(s);
         // model = new Gaussian(s);
         // model = new SmoothGauss(s);
         // model = new PieceLinGauss(s);
         // model = new IndSumNormal(s);
         // model = new SumUniforms(s);
         // model = new RidgeJohnsonSU(s);
         // model = new IndBox(s);


         // The following is for the Asian option model only; uncomment what is used.
         //
         // StochasticProcess brown = new BrownianMotion(0, 0, 1, gen);
         // model = new AsianOption(r=0.05, s, 1.0 / s, 1.0, strike=92.0);
         // Sequential sampling:
         // ((AsianOption) model).setProcess(new GeometricBrownianMotion(s0, r, sigma, new BrownianMotion(0, 0, 1, gen)));
         // ((AsianOption) model).setTag("AsianSeq92");
         // Bridge sampling:
         // ((AsianOption) model).setProcess(new GeometricBrownianMotion(s0, r, sigma, new BrownianMotionBridge(0, 0, 1, gen)));
         // ((AsianOption) model).setTag("AsianBridge");
         // PCA sampling:
         // ((AsianOption) model).setProcess(new GeometricBrownianMotion(s0, r, sigma, new BrownianMotionPCA(0, 0, 1, gen)));
         // ((AsianOption) model).setTag("AsianPCA92");
         
         //  RQMCSamples23.redirectToFile((model.getTag() + "-" + s));
         // WSC26RQMCSamples64.simulAllSizes(model, s, 8, 18, 1000);
         
         int m = 10000; // Number of RQMC randomizations.
         int mink = 8;
         int maxk = 16;
         //WSC26RQMCSamples64.simulAllSizes(new SmoothPerB4(s, 1.0), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new SumUeU(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new MC2(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new Polynomial(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new Oscillatory(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new Gaussian(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new SmoothGauss(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new PieceLinGauss(s), s, mink, maxk, m);
         //WSC26RQMCSamples64.simulAllSizes(new IndSumNormal(s), s, mink, maxk, m);
         WSC26RQMCSamples64.simulAllSizes(new XY(), 2, mink, maxk, m);
         
         // WSC26RQMCSamples64.simulRepsNUS(new MC2(s), 2, 2, 1);
         //WSC26RQMCSamples64.simulRepsLMS(new SmoothPerB4(s, 1.0), s, mink, m);
         //WSC26RQMCSamples64.simulRepsLMS(new SumUeU(s), s, mink, m);
         //WSC26RQMCSamples64.simulRepsLMS(new MC2(s), s, mink, m);
         //WSC26RQMCSamples64.simulRepsLMS(new Polynomial(s), s, mink, m);
         //WSC26RQMCSamples64.simulRepsLMS(new Oscillatory(s), s, mink, m);
         //WSC26RQMCSamples64.simulRepsLMS(new Gaussian(s), s, mink, m);
      
      }
      System.out.println("Total time for everything: " + timerTotal.format() +
            "\n=========================================== \n");
   }
}
