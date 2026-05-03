package rqmcexperiments;

import java.io.*;

import umontreal.ssj.hups.*;
import umontreal.ssj.mcqmctools.*;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Num;

/**
 * Functions used by the program `WSC26RepsRQMC64` for the WSC 2026 paper.
 * The String `strPointSets` will be the header of all output data files.
 * We should comment the methods (lines) that are not used, so they match  
 * exactly the methods that are effectively used in `simulRepsAllTypes`. 
 */
public class WSC26RQMCSamples extends RQMCExperiment {

   static String directory = "C:/Users/Lecuyer/Dropbox/wsc26/data/";

   // Lattice generating vector for n=2^{14} found with gamma_j = 2/(2+j), used for the WSC23 paper.
   static int a14[] = { 1, 6229, 2691, 3349, 5893, 7643, 7921, 7055, 4829, 5177, 5459, 4863, 4901, 2833, 2385, 3729,
         981, 957, 4047, 1013, 1635, 2327, 7879, 2805, 2353, 1081, 3999, 879, 5337, 7725, 4889, 5103 };
   // This one is for n=2^{18}, found by CBC with same gamma_j.
   static int a18[] = { 1, 103259, 73357, 46713, 58781, 112041, 32459, 50551, 40125, 128245, 
         18285, 124265, 98539, 130087, 113373, 22191, 120679, 98411, 94845, 33103, 47891, 15941, 
         30147, 43921, 81129, 3289, 50935, 63965, 55749, 38101, 70631, 116243 };
 
   static final String strPointSets = " k "
         + " Strat "        // Stratification
         + " Lat-RS "       // Lattice + RS
         + " Lat-RST "      // Lattice + RS + tent
         + " Sob "          // Sobol, deterministic
//        + " Sob-T "        // Sobol + tent
//        + " Sob-RDS "      // Sobol + RDS
         + " Sob-RDST "
         + " Sob-LMS "      // Sobol + LMS
         + " Sob-LMS-RDS "  // Sobol + LMS + RDS
         + " Sob-LMS-RDST " 
         + " Sob-NUS "      // Sobol + NUS
         + " Sob-NUST "
         + " SobInt2-RDS "  // Sobol Interlaced + RDS
         + " SobInt2-RDST "
         + "\n";
   static int numTypesPts = 12;  // Number of types in `strPointSets`. 
   static int MAXK = 20;         // Upper bound on k.
   static double[][] statAverage = new double[MAXK+1][numTypesPts]; // To store the averages and variances
   static double[][] statVariance = new double[MAXK+1][numTypesPts]; // for different point sets and k.
   static double[][] statLogVariance = new double[MAXK+1][numTypesPts]; // for different point sets and k.
   static double[][] statLogVariance2 = new double[MAXK+1][numTypesPts]; // This one is recomputed by Colt function.
   static double[][] statKurtosis = new double[MAXK+1][numTypesPts]; // Excess kurtosis.
   static TallyStore statReps = new TallyStore(); // Collects stats on RQMC replicates.


   /**
    * Formats the table as a `String`, one row per value of k, one column with a
    * line feed after each observation.
    */
   public static String tableToString(int mink, int maxk, double[][] table) throws IOException {
      StringBuilder sb = new StringBuilder();
      sb.append(strPointSets);
      for (int k = mink; k <= maxk; k++) { // For each point set size
         sb.append(k + "  ");
         for (int type = 0; type < numTypesPts; type++) {
            sb.append(table[k][type] + "  ");
         }
         sb.append("\n");
      }
      return sb.toString();
   }

   /**
    * Perform m independent RQMC replications and save the average and variance
    * for that particular k and point set type in the appropriate tables.
    */
   public static void simulRepsRQMC(MonteCarloModelDouble model, PointSet p, PointSetRandomization rand, int m, int k,
         int typePts) {
      simulReplicatesRQMC(model, p, rand, m, statReps);
      statAverage[k][typePts] = statReps.average();
      statVariance[k][typePts] = statReps.variance();
      statLogVariance[k][typePts] = Math.log10(statReps.variance());   // We store log_10 of the variance.
      statLogVariance2[k][typePts] = Math.log10(statReps.variance2());
      statKurtosis[k][typePts] = statReps.kurtosis();
   }

   /**
    * For the given model and given `k`, perform m RQMC replications with n=2^k points,
    * for different types of RQMC points, with and without the tent transformation.
    */
   public static void simulRepsAllTypes(MonteCarloModelDouble model, int s, int k, int m) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      // String modelTag = model.getTag();
      // RandomStream stream = new LFSR258();
      RandomStream stream = new LFSR113();
      Chrono timer = new Chrono();
      int met = 0;
     
      // Stratif
      StratifiedUnitCube str = new StratifiedUnitCube (n, s);
      simulRepsRQMC(model, str, new RandomShift(stream), m, k, met++);
      
      // Lattice points
      Rank1Lattice pLat = new Rank1Lattice(n, a18, s);
      RandomShift randShift = new RandomShift(stream);
      BakerTransformedPointSet pLatBaker = new BakerTransformedPointSet(pLat);

      // Lat-RS
      simulRepsRQMC(model, pLat, randShift, m, k, met++);

      // Lat-RST
      simulRepsRQMC(model, pLatBaker, randShift, m, k, met++);

      // Sobol' points
      DigitalNetBase2 p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      PointSetRandomization norand = new EmptyRandomization(); // No randomization
      PointSetRandomization rands = new RandomShift(stream); // Digital shift
      BakerTransformedPointSet pBaker = new BakerTransformedPointSet(p);

      // Sob
      //simulRepsRQMC(model, p, norand, 2, k, met++);

      // Sob-T
      //simulRepsRQMC(model, pBaker, norand, 2, k, met++);

      // Sob-RDS
      //System.out.println("*****  Doing DigitalNet with RDS alone");
      // p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      simulRepsRQMC(model, p, rands, m, k, met++);
      // if (p instanceof DigitalNet) System.out.println("p is a DigitalNet, Sobol, after rands");

      // Sob-RDST
      pBaker = new BakerTransformedPointSet(p);
      simulRepsRQMC(model, pBaker, rands, m, k, met++);
      
      // Sob-LMS
      //System.out.println("*****  Doing DigitalNet with LMS alone");
      p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      PointSetRandomization lms = new LMScramble(stream);
      // simulRepsRQMC(model, p, lms, m, k, met++);
      // System.out.println("p is a DigitalNet after LMS alone");
      // System.out.println(p.formatPoints());
      
      // Sob-LMS-RDS
      //System.out.println("*****  Doing DigitalNet with LMS + RDS");
      p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      PointSetRandomization randlms = new LMScrambleShift(stream);
      simulRepsRQMC(model, p, randlms, m, k, met++);
      // if (p instanceof DigitalNet) System.out.println("p is a DigitalNet after LMS");

      // Sob-LMS-RDST
      pBaker = new BakerTransformedPointSet(p);
      simulRepsRQMC(model, pBaker, randlms, m, k, met++);

      // Sob-NUS
      //System.out.println("*****  Doing DigitalNet with NUS");
      RandomStream streamNUS = new LFSR113();
      p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      CachedPointSet cp = new CachedPointSet(p);
      PointSetRandomization randNUS = new NestedUniformScrambling(streamNUS, 30);
      simulRepsRQMC(model, cp, randNUS, m, k, met++);
      // System.out.println("CachedPointSet after NUS:");
      // System.out.println(cp.formatPoints());

      // Sob-NUST
      //System.out.println("*****  Doing DigitalNet with NUS + baker");
      BakerTransformedPointSet cpBaker = new BakerTransformedPointSet(cp);
      simulRepsRQMC(model, cpBaker, randNUS, m, k, met++);
      // if (cpBaker instanceof BakerTransformedPointSet) System.out.println("cpBaker is a BakerTransformedPointSet after NUSB");

      // Sob-Int2    Sob-interlaced-order2
      DigitalNetBase2 p2 = new SobolSequence(k, 30, 2*s); // n = 2^{k} points in 2s dim.
      p2.setInterlacing(2);
      p = p2.matrixInterlace();
      pBaker = new BakerTransformedPointSet(p);

      simulRepsRQMC(model, p, rands, m, k, met++);
      simulRepsRQMC(model, pBaker, rands, m, k, met++);    

      System.out.println("k = " + k + ", CPU time: " + timer.format());
   }

   /**
    * For given model, perform m RQMC runs for all point set sizes k, and puts the results in arrays.
    */
   public static void simulAllSizes(MonteCarloModelDouble model, int s, int mink, int maxk, int m)
         throws IOException {
      System.out.println("RQMC replicates with model: " + model.toString() + ", s = " + s + "\n");
      String modelTag = model.getTag();
      Chrono timer = new Chrono();
      for (int k = mink; k <= maxk; k++) { // For each point set size
         simulRepsAllTypes(model, s, k, m);
      }
      FileWriter file = new FileWriter(directory + modelTag + "-" + s + "-average.res");
      file.write(tableToString(maxk-3, maxk, statAverage));
      file.close();
      file = new FileWriter(directory + modelTag + "-" + s + "-logvariance.res");
      file.write(tableToString(mink, maxk, statLogVariance));
      file.close();
      System.out.println("Total time for simulAllSizes: " + timer.format() 
            + "\n=========================================== \n");
   }
   
   /**
    * For given model and given k, perform m RQMC runs for given model with n=2^k points,
    * for different types of RQMC points, with and without random bits after k.
    */
   public static void simulRepsLMS(MonteCarloModelDouble model, int s, int k, int m) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      System.out.println("\n========================");
      System.out.println("RQMC replicates with model: " + model.toString() + ", s = " + s + ", k = " + k + "\n");
      // String modelTag = model.getTag();
      RandomStream stream = new LFSR113();
      RandomStream streamIRB = new LFSR113();
      DigitalNetBase2 p = new SobolSequence(k, 31, s); // n = 2^{k} points in s dim.
      Chrono timer = new Chrono();
      int met = 0;

      // Sob-LMS
      System.out.println("\n*****  DigitalNet with LMS+RDS");
      timer.init();
      PointSetRandomization randlms = new LMScrambleShift(stream);
      simulRepsRQMC(model, p, randlms, m, k, met++);
      System.out.println(statReps.report());
      System.out.println("average = " + statReps.average());
      System.out.println("variance = " + statReps.variance());
      System.out.println("log variance = " + Math.log10(statReps.variance()));
      System.out.println("log variance2 = " + Math.log10(statReps.variance2()));
      System.out.println("kurtosis = " + statReps.kurtosis());
      System.out.println("k = " + k + ", CPU time: " + timer.format());
   }

}
