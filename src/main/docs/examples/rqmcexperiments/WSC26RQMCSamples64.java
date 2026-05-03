package rqmcexperiments;

import java.io.*;
import umontreal.ssj.hups64.*;
import umontreal.ssj.mcqmctools.*;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Num;

// Tools to generate and store RQMC replicates for WSC 2026 paper

public class WSC26RQMCSamples64 extends RQMCExperiment64 {

   static String directory;   // Must be set in main program.

   // Lattice generating vector for n=2^{14} found with gamma_j = 2/(2+j), used for the WSC23 paper.
   static int a14[] = { 1, 6229, 2691, 3349, 5893, 7643, 7921, 7055, 4829, 5177, 5459, 4863, 4901, 2833, 2385, 3729,
         981, 957, 4047, 1013, 1635, 2327, 7879, 2805, 2353, 1081, 3999, 879, 5337, 7725, 4889, 5103 };
   // This one is for n=2^{18}, found by CBC with same gamma_j.
   static int a18[] = { 1, 103259, 73357, 46713, 58781, 112041, 32459, 50551, 40125, 128245, 
         18285, 124265, 98539, 130087, 113373, 22191, 120679, 98411, 94845, 33103, 47891, 15941, 
         30147, 43921, 81129, 3289, 50935, 63965, 55749, 38101, 70631, 116243 };
   
   static final String strPointSets = " k "
//         + " Strat "        // Stratification
         + " Lat-RS "       // Lattice + RS
         + " Lat-RST "      // Lattice + RS + tent
//         + " Sob "          // Sobol, deterministic
//         + " Sob-T "        // Sobol + tent
         + " Sob-RDS "      // Sobol + RDS
         + " Sob-RDST "
         + " Sob-LMS "      // Sobol + LMS
         + " Sob-LMS-RDS "  // Sobol + LMS + RDS
         + " Sob-LMS-RDS-RB " // Sobol + LMS + RDS + random bits after k
         + " Sob-LMS-RDST " 
         + " Sob-NUS "      // Sobol + NUS
         + " Sob-NUST "
//         + " SobInt2-RDS "  // Sobol Interlaced + RDS
//         + " SobInt2-RDST "
         + "\n";
   static int numTypesPts = 10;  // Number of types in `strPointSets`. 
   static int MAXK = 20;         // Upper bound on k.
   static double[][] statAverage = new double[MAXK+1][numTypesPts]; // To store the averages and variances
   static double[][] statVariance = new double[MAXK+1][numTypesPts]; // for different point sets and k.
   static double[][] statLogVariance = new double[MAXK+1][numTypesPts]; 
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
   public static void simulRepsRQMC(MonteCarloModelDouble model, PointSet p, PointSetIterator iter, 
         PointSetRandomization rand, int m, int k, int typePts) {
      System.out.println("typePts = " + typePts);
      // System.out.println("\n***  SimulRepsRQMC: The point set p before simulReplicates:");
      // System.out.println(p.formatPoints());
      RQMCExperiment64.simulReplicatesRQMC(model, p, iter, rand, m, statReps);
      statAverage[k][typePts] = statReps.average();
      statVariance[k][typePts] = statReps.variance();
      statLogVariance[k][typePts] = Math.log10(statReps.variance());   // We store log_10 of the variance.
      statLogVariance2[k][typePts] = Math.log10(statReps.variance2());
      statKurtosis[k][typePts] = statReps.kurtosis();
      // System.out.println("The cached points cp after NUS:");
      // System.out.println(p.formatPoints());
      }
   
   public static void simulRepsRQMC(MonteCarloModelDouble model, PointSet p,
         PointSetRandomization rand, int m, int k, int typePts) {
      PointSetIterator iter = p.iterator();
      simulRepsRQMC(model, p, iter, rand, m, k, typePts);
      }

   /**
    * For the given model and given `k`, perform m RQMC replications with n=2^k points,
    * for different types of RQMC points, with and without the tent transformation.
    */
   public static void simulRepsAllTypes(MonteCarloModelDouble model, int s, int k, int m) throws IOException {
      int n = (int) Num.TWOEXP[k]; // Number of points.
      // String modelTag = model.getTag();
      RandomStream stream = new LFSR258();
      Chrono timer = new Chrono();
      int met = 0;
      
      // Stratif
      // System.out.println("*****  Stratification");
      // StratifiedUnitCube str = new StratifiedUnitCube (n, s);  //  Must be n^{1/s}
      // simulRepsRQMC(model, str, new RandomShift(stream), m, k, met++);
      
      // Lattice points  
      Rank1Lattice pLat = new Rank1Lattice(n, a14, s);
      RandomShift randShift = new RandomShift(stream);
      BakerTransformedPointSet pLatBaker = new BakerTransformedPointSet(pLat);

      // Lat-RS
      System.out.println("*****  Lat-RS");
      simulRepsRQMC(model, pLat, randShift, m, k, met++);

      // Lat-RSB
      simulRepsRQMC(model, pLatBaker, randShift, m, k, met++);

      // Sobol' points
      DigitalNetBase2 p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      PointSetRandomization norand = new EmptyRandomization(); // No randomization
      PointSetRandomization rands = new RandomShift(stream); // Digital shift
      BakerTransformedPointSet pBaker = new BakerTransformedPointSet(p);

      // Sob
      //simulRepsRQMC(model, p, norand, 2, k, met++);
      // if (p instanceof DigitalNet) System.out.println("p is a DigitalNet, Sobol");

      // Sob-B
      //simulRepsRQMC(model, pBaker, norand, 2, k, met++);
      // System.out.println("after Sobol + baker");

      // Sob-RDS
      System.out.println("***** DigitalNet with RDS alone");
      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      simulRepsRQMC(model, p, rands, m, k, met++);
      // if (p instanceof DigitalNet) System.out.println("p is a DigitalNet, Sobol, after rands");

      // Sob-RDSB
      pBaker = new BakerTransformedPointSet(p);
      simulRepsRQMC(model, pBaker, rands, m, k, met++);
      
      // Sob-LMS
      System.out.println("*****  DigitalNet with LMS alone");
      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      PointSetRandomization lms = new LMScramble(stream);
      simulRepsRQMC(model, p, lms, m, k, met++);
      // System.out.println("p is a DigitalNet after LMS alone");
      // System.out.println(p.formatPoints());
      
      // Sob-LMS-RDS
      System.out.println("*****  DigitalNet with LMS + RDS");
      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      PointSetRandomization randlms = new LMScrambleShift(stream);
      simulRepsRQMC(model, p, randlms, m, k, met++);
      // if (p instanceof DigitalNet) System.out.println("p is a DigitalNet after LMS");

      // Sob-LMS-RDS + independent random bits after k
      System.out.println("*****  DigitalNet with LMS+RDS + indep random bits after k");
      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      p.addIndepRandomBits(new LFSR258());
      randlms = new LMScrambleShift(stream);
      simulRepsRQMC(model, p, randlms, m, k, met++);
      p.clearIndepRandomBits();
      
      // Sob-LMS-RDSB
      pBaker = new BakerTransformedPointSet(p);
      simulRepsRQMC(model, pBaker, randlms, m, k, met++);

      // Sob-NUS
      System.out.println("*****  DigitalNet with NUS");
      // RandomStream streamNUS = new LFSR113();
      RandomStream streamNUS = new LFSR258();
      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.     
      CachedPointSet cp = new CachedPointSet(p);
      PointSetRandomization randNUS = new NestedUniformScrambling (streamNUS, 53);
      simulRepsRQMC(model, cp, randNUS, m, k, met++);
      // System.out.println("CachedPointSet after NUS:");
      // System.out.println(cp.formatPoints());
      
      // Sob-NUSB
      // System.out.println("*****  Doing DigitalNet with NUS + baker");
      BakerTransformedPointSet cpBaker = new BakerTransformedPointSet(cp);
      simulRepsRQMC(model, cpBaker, randNUS, m, k, met++);
      // if (cpBaker instanceof BakerTransformedPointSet) System.out.println("cpBaker is a BakerTransformedPointSet after NUSB");
      
      // Sob-Int2    Sob-interlaced-order2
      //System.out.println("*****  Doing DigitalNet with interlacing");
      DigitalNetBase2 p2 = new SobolSequence(k, 32, 2*s); // n = 2^{k} points in 2s dim.
      // p2.setInterlacing(2);
      p = p2.matrixInterlace(2, s);
      pBaker = new BakerTransformedPointSet(p);
      // System.out.println(p.formatPoints());

      // simulRepsRQMC(model, p, rands, m, k, met++);
      // simulRepsRQMC(model, pBaker, rands, m, k, met++);    

      System.out.println("k = " + k + ", CPU time: " + timer.format());
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
      //RandomStream stream = new MRG32k3a();
      //RandomStream streamIRB = new MRG32k3a();
      //((MRG32k3a)stream).increasedPrecision(true);
      //((MRG32k3a)streamIRB).increasedPrecision(true);
      RandomStream stream = new LFSR258();
      RandomStream streamIRB = new LFSR258();
      DigitalNetBase2 p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
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
      
      // Sob-LMS-RDS + indep random bits after k
      System.out.println("\n*****  DigitalNet with LMS+RDS + indep random bits after k");
      timer.init();
      p.addIndepRandomBits(streamIRB);
      stream.resetStartStream();
      simulRepsRQMC(model, p, randlms, m, k, met++);
      System.out.println(statReps.report());
      System.out.println("average = " + statReps.average());
      System.out.println("variance = " + statReps.variance());
      System.out.println("log variance = " + Math.log10(statReps.variance()));
      System.out.println("log variance2 = " + Math.log10(statReps.variance2()));
      System.out.println("kurtosis = " + statReps.kurtosis());
      System.out.println("k = " + k + ", CPU time: " + timer.format());
   }
   
   /**
    * For given model and given k, perform m RQMC runs for given model with n=2^k points,
    * for different types of RQMC points, with and without tent transform.
    */
   public static void simulRepsNUS(MonteCarloModelDouble model, int s, int k, int m) throws IOException {
      // int n = (int) Num.TWOEXP[k]; // Number of points.
      // String modelTag = model.getTag();
      RandomStream stream = new LFSR258();
      DigitalNetBase2 p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      Chrono timer = new Chrono();
      int met = 0;

      // Sob-NUS
      System.out.println("\n*****  DigitalNet with NUS");
      timer.init();
      // p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      PointSetRandomization randNUS = new NestedUniformScrambling(stream, 53);
      CachedPointSet cp = new CachedPointSet(p);
      stream.resetStartStream();
      simulRepsRQMC(model, cp, randNUS, m, k, met++);    
      // System.out.println(statReps.report());
      System.out.println("log variance = " + Math.log10(statReps.variance()));
      System.out.println("k = " + k + ", CPU time: " + timer.format());
   }
   
   // For one model, perform m RQMC runs with LMS for all point set sizes k, and puts the
   // results in arrays.  This is for testing different variants of LMS. 
   public static void simulAllSizesLMS(MonteCarloModelDouble model, int s, int mink, int maxk, int m)
         throws IOException {
      System.out.println("RQMC replicates with model: " + model.toString() + ", s = " + s + "\n");
      String modelTag = model.getTag();
      Chrono timer = new Chrono();
      for (int k = mink; k <= maxk; k++) { // For each point set size
         simulRepsLMS(model, s, k, m);
      }
      // String strTab = tableToString(modelTag, mink, maxk, statAverage);
      FileWriter file = new FileWriter(directory + modelTag + "-" + s + "-averageLMS.res");
      file.write(tableToString(maxk-3, maxk, statAverage));
      file.close();
      file = new FileWriter(directory + modelTag + "-" + s + "-varianceLMS.res");
      file.write(tableToString(mink, maxk, statLogVariance));
      file.close();
      System.out.println("\nTotal time for simulAllSizes: " + timer.format() 
            + "\n=========================================== \n");
   }
     
   // For one model, perform m RQMC runs for all point set sizes k, and puts the
   // results in arrays.  This was for testing variants of NUS. 
   public static void simulAllSizesNUS(MonteCarloModelDouble model, int s, int mink, int maxk, int m)
         throws IOException {
      System.out.println("RQMC replicates with model: " + model.toString() + ", s = " + s + "\n");
      String modelTag = model.getTag();
      Chrono timer = new Chrono();
      for (int k = mink; k <= maxk; k++) { // For each point set size
         simulRepsNUS(model, s, k, m);
      }
      // String strTab = tableToString(modelTag, mink, maxk, statAverage);
      FileWriter file = new FileWriter(directory + modelTag + "-" + s + "-averageNUS.res");
      file.write(tableToString(maxk-3, maxk, statAverage));
      file.close();
      file = new FileWriter(directory + modelTag + "-" + s + "-varianceNUS.res");
      file.write(tableToString(mink, maxk, statLogVariance));
      file.close();
      System.out.println("\nTotal time for simulAllSizes: " + timer.format() 
            + "\n=========================================== \n");
   }
   
   /**
    * For one model, perform m RQMC runs for all point set sizes k, and puts the
    * results in arrays.  After that, the arrays are used to output data sets in files. 
    */
   public static void simulAllSizes(MonteCarloModelDouble model, int s, int mink, int maxk, int m)
         throws IOException {
      System.out.println("RQMC replicates with model: " + model.toString() + ", s = " + s + "\n");
      String modelTag = model.getTag();
      Chrono timer = new Chrono();
      for (int k = mink; k <= maxk; k++) { // For each point set size
         simulRepsAllTypes(model, s, k, m);
      }
      // String strTab = tableToString(modelTag, mink, maxk, statAverage);
      FileWriter file = new FileWriter(directory + modelTag + "-" + s + "-average.res");
      file.write(tableToString(maxk-3, maxk, statAverage));
      file.close();
      file = new FileWriter(directory + modelTag + "-" + s + "-variance.res");
      file.write(tableToString(mink, maxk, statLogVariance));
      file.close();
      file = new FileWriter(directory + modelTag + "-" + s + "-variance2.res");
      file.write(tableToString(mink, maxk, statLogVariance2));
      file.close();
      file = new FileWriter(directory + modelTag + "-" + s + "-kurtosis.res");
      file.write(tableToString(mink, maxk, statKurtosis));
      file.close();
      System.out.println("\nTotal time for simulAllSizes: " + timer.format() 
            + "\n=========================================== \n");
   }
}
