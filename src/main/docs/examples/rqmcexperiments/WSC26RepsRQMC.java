package rqmcexperiments;

import java.io.*;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.hups.*;
import umontreal.ssj.mcqmctools.RQMCExperiment;
import umontreal.ssj.randvar.NormalGen;
import umontreal.ssj.rng.LFSR113;   // LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stochprocess.*;
import umontreal.ssj.util.Chrono;

// Generate and store RQMC replicates for various models, for WSC 2026 paper

public class WSC26RepsRQMC extends RQMCExperiment {

   public static void main(String[] args) throws IOException {

      // WSC26RQMCSamples.directory = "C:/Users/Lecuyer/Documents/conf/wsc26/mydata/";
      // WSC26RQMCSamples.directory = "C:/Users/Lecuyer/Dropbox/wsc26/data/";
      WSC26RQMCSamples.directory = "C:/Users/Lecuyer/Dropbox/wsc26/test/";

      int m = 100; // Number of RQMC randomizations.
      int mink = 8;
      int maxk = 10;
      MonteCarloModelDouble model;
      Chrono timerTotal = new Chrono();

      // For Asian Option model.
      double strike = 92.0;
      double s0 = 100.0;
      double r = 0.05;
      double sigma = 0.5;
      RandomStream noise = new LFSR113();
      // RandomStream noise = new LFSR258();
      NormalGen gen = new NormalGen(new LFSR113());
  
      for (int s = 8; s <= 8; s *= 4) {
         System.out.println("WSC26RepsRQMC, hups, 32 bits, run with s = " + s);
         // for (int s = 4; s <= 32; s *= 2) {
         // Uncomment the model you want below. ***
         // model = new SmoothPerBeta52(s, 1.0);
         // model = new SmoothPerB4(s, 1.0);
         // model = new SumUeU(s);
         // model = new MC2(s);
         // model = new PolynomialGenz(s);
         // model = new Oscillatory(s);
         // model = new Gaussian(s);
         // model = new IndBox(s);
         // model = new PieceLinGauss(s);
         // model = new IndSumNormal(s);
         // model = new SmoothGauss(s);
         // model = new SumUniforms(s);
         // model = new RidgeJohnsonSU(s);


         // StochasticProcess brown = new BrownianMotion(0, 0, 1, gen);
         //
         // model = new AsianOption(r=0.05, s, 1.0 / s, 1.0, strike=92.0);
         // Sequential sampling:
         // ((AsianOption) model).setProcess(new GeometricBrownianMotion(s0, r, sigma, new BrownianMotion(0, 0, 1, gen)));
         // ((AsianOption) model).setTag("AsianSeq92");
         // Bridge sampling:
         // ((AsianOption) model).setProcess(new GeometricBrownianMotion(s0, r, sigma, new BrownianMotionBridge(0, 0, 1, gen)));
         // ((AsianOption) model).setTag("AsianBridge");
         // PCA sampling:
         //((AsianOption) model).setProcess(new GeometricBrownianMotion(s0, r, sigma, new BrownianMotionPCA(0, 0, 1, gen)));
         //((AsianOption) model).setTag("AsianPCA92");
         
         //  RQMCSamples23.redirectToFile((model.getTag() + "-" + s));
         // WSC26RQMCSamples.simulAllSizes(model, s, 8, 18, 1000);
         
         m = 10000; // Number of RQMC randomizations.
         mink = 16;
         maxk = 16;
         /*
         WSC26RQMCSamples.simulAllSizes(new SmoothPerB4(s, 1.0), s, mink, maxk, m);
         WSC26RQMCSamples.simulAllSizes(new SumUeU(s), s, mink, maxk, m);
         WSC26RQMCSamples.simulAllSizes(new MC2(s), s, mink, maxk, m);
         //WSC26RQMCSamples.simulAllSizes(new Polynomial(s), s, mink, maxk, m);
         //WSC26RQMCSamples.simulAllSizes(new Oscillatory(s), s, mink, maxk, m);
         //WSC26RQMCSamples.simulAllSizes(new Gaussian(s), s, mink, maxk, m);
         WSC26RQMCSamples64.simulAllSizes(new SmoothGauss(s), s, mink, maxk, m);
         WSC26RQMCSamples64.simulAllSizes(new PieceLinGauss(s), s, mink, maxk, m);
         WSC26RQMCSamples64.simulAllSizes(new IndSumNormal(s), s, mink, maxk, m);   
         */  
         
         WSC26RQMCSamples.simulRepsLMS(new SmoothPerB4(s, 1.0), s, mink, m);
         WSC26RQMCSamples.simulRepsLMS(new SmoothPerB4(s, 1.0), s, mink, m);
         //WSC26RQMCSamples.simulRepsLMS(new SumUeU(s), s, mink, m);
         //WSC26RQMCSamples.simulRepsLMS(new MC2(s), s, mink, m);
         //WSC26RQMCSamples.simulRepsLMS(new Polynomial(s), s, mink, m);
         //WSC26RQMCSamples.simulRepsLMS(new Oscillatory(s), s, mink, m);
         //WSC26RQMCSamples.simulRepsLMS(new Gaussian(s), s, mink, m);

      }
      System.out.println("\nTotal time for everything: " + timerTotal.format() + "\n======================== \n");
   }
}
