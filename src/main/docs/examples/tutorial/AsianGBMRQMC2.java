package tutorial;

import java.io.IOException;
import umontreal.ssj.rng.*;
import umontreal.ssj.hups.*;
import umontreal.ssj.mcqmctools.*;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.util.Chrono;

// An extension of AsianGBM2 that uses an RQMC.
public class AsianGBMRQMC2 extends AsianGBM2 {

   public AsianGBMRQMC2 (double r, double sigma, double strike,
                    double s0, int s, double[] zeta) {
       super (r, sigma, strike, s0, s, zeta);
   }

   public static void main (String[] args)  throws IOException {
      int d = 12;
      double[] zeta = new double[d+1];
      for (int j=0; j<=d; j++)
         zeta[j] = (double)j / (double)d;
      AsianGBMRQMC2 model = new AsianGBMRQMC2 (0.05, 0.5, 100.0, 100.0, d, zeta);
      Tally statMC  = new Tally ("value of Asian option");
      Tally statRQMC = new Tally ("RQMC averages for Asian option under GBM");

      // We first perform a Monte Carlo experiment, to compare with RQMC.
      int n = 100000;
      System.out.println ("Ordinary MC:\n");
      System.out.println (MonteCarloExperiment.simulateRunsDefaultReportStudent 
          (model, n, new MRG32k3a(), statMC));
      System.out.println ("------------------------\n");

      // Then we make a RQMC experiment.
      DigitalNet p = new SobolSequence (16, 31, d); // n = 2^{16} points in d dim.
      PointSetRandomization rand = new LMScrambleShift (new MRG32k3a());
      int m = 50;                     // Number of RQMC randomizations.
      System.out.println (RQMCExperiment.simulReplicatesRQMCDefaultReport 
           (model, p, rand, m, statRQMC));
      System.out.println ("----------------------------------------------------\n");
      
      // This single function makes both the MC and RQMC experiments and also 
      // compares the variances and efficiencies.
      System.out.println (RQMCExperiment.makeComparisonExperimentMCvsRQMC
              (model, new MRG32k3a(), p, rand, n, m));
   }
}
