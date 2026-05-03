package rqmcexperiments;

import java.io.*;
import umontreal.ssj.mcqmctools.RQMCExperiment;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;

// Generate and store RQMC replicates for various models, for WSC 2024 Wafom paper

public class WSC24condLMS extends RQMCExperiment {

   public static void main(String[] args) throws IOException {

      int s = 6;
      int k = 10;
      int mink = 8;
      int maxk = 20;
      int numLMS = 100; // Number of LMS replications.
      int numRDS = 100; // Number of RDS replications per LMS.
      int numModels = 6;
      MonteCarloModelDouble[] models = new MonteCarloModelDouble[numModels];
      models[0] = new Oscillatory(s);
      models[1] = new Polynomial(s);
      models[2] = new Exponential(s);
      models[3] = new Gaussian(s);
      models[4] = new CornerPeak(s);
      models[5] = new PieceLinGauss(s);

      // This is for section 2.4 of the WSC24 paper.
      WSC24RQMCSamples.simulRepsCondLMS(s, k, numLMS, numRDS, models, "jk");
      // This is for section 2.3 of the WSC24 paper.
      WSC24RQMCSamples.simulTruncLMS(s, mink, maxk, numLMS, models, "jk");
   }
}
