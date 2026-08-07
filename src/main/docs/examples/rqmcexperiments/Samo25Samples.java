package rqmcexperiments;

import java.io.*;
import umontreal.ssj.hups64.*;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.mcqmctools.RQMCExperiment64;
import umontreal.ssj.rng.LFSR258;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Num;

/**
 * Tools to generate and store RQMC replicates for WSC 2023 paper. This class is
 * used by the main program in `Samo25SamplesMain.java`. It uses the 64-bit version
 * of `hups`.
 */
public class Samo25Samples extends RQMCExperiment64 {

   static String directory; // Must be set in main program `Samo25SamplesMain`.

   // Lattice generating vector for n=2^{14} found with gamma_j = 2/(2+j), used for
   // the WSC23 paper.
   // static int a14[] = { 1, 6229, 2691, 3349, 5893, 7643, 7921, 7055, 4829, 5177,
   // 5459, 4863, 4901, 2833, 2385, 3729,
   // 981, 957, 4047, 1013, 1635, 2327, 7879, 2805, 2353, 1081, 3999, 879, 5337,
   // 7725, 4889, 5103 };
   // The following one is for n=2^{18}, found by CBC with same gamma_j.
   static int a18[] = { 1, 103259, 73357, 46713, 58781, 112041, 32459, 50551, 40125, 128245, 18285, 124265, 98539,
         130087, 113373, 22191, 120679, 98411, 94845, 33103, 47891, 15941, 30147, 43921, 81129, 3289, 50935, 63965,
         55749, 38101, 70631, 116243 };
   // The trivial vector.
   // static int a1[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        

   /**
    * Redirect the output to a .res file with the given name, in `directory`.
    */
   public static void redirectToFile(String modelName) throws IOException {
      File file = new File(Samo25Samples.directory + modelName + ".res");
      PrintStream printStreamToFile = new PrintStream(file);
      System.setOut(printStreamToFile);
   }

   /**
    * Writes a summary report with mean, variance, etc., to a .sum file with the
    * given name, in given directory.
    */
   public static void reportToFile(TallyStore tally) throws IOException {
      FileWriter file = new FileWriter(directory + tally.getName() + ".sum");
      file.write(tally.shortReport());
      file.close();
   }

   /**
    * Returns the observations stored in this object as a `String`, with a line
    * feed after each observation.
    */
   public static String dataToString(TallyStore tally) {
      StringBuilder sb = new StringBuilder();
      double[] array = tally.getArray();
      for (int i = 0; i < tally.numberObs(); i++)
         sb.append(array[i] + "\n");
      return sb.toString();
   }

   /**
    * Writes the sorted observations in `tally` as a `String` and prints them in a
    * file with the given name, in given directory. Also calls `reportToFile`.
    */
   public static void dataToFile(TallyStore tally) throws IOException {
      // reportToFile (tally, fileName);
      FileWriter file = new FileWriter(directory + tally.getName() + ".dat");
      file.write(dataToString(tally));
      file.close();
   }

   /**
    * Performs m independent RQMC replications and save the sorted output in the
    * `statReps` collector. We assume that the randomization may change the number
    * of points, as it sometimes happens when using `RandomLatticeParams` for instance.
    * If `crn` is `true`, the same substream is used for all calls to this methods.
    */
   public static void simulRepsRQMCSort(MonteCarloModelDouble model, PointSet p, PointSetRandomization rand, int m,
         TallyStore statReps, boolean crn) throws IOException {
      statReps.init();
      Tally statValue = new Tally();
      if (crn) rand.getStream().resetStartStream();  // To use the same substream for all calls to this function.
      PointSetIterator streampts = p.iterator();     // Iterator over the RQMC points.
      Chrono timer = new Chrono();
      for (int rep = 0; rep < m; rep++) {
         statValue.init();
         if (crn) rand.getStream().resetNextSubstream();  // New substream for each rep.
         rand.randomize(p);
         streampts.resetStartStream(); // This stream iterates over the points.
         simulateRuns(model, p.getNumPoints(), streampts, statValue);
         statReps.add(statValue.average()); // For the estimator of the mean.
         // System.out.println("average = " + statReps.average());
      }
      System.out.println("Output file: " + statReps.getName());
      System.out.println("Number obs:  " + statReps.numberObs());
      System.out.println("average = " + statReps.average());
      System.out.println("variance = " + statReps.variance());
      System.out.println("skewness, bias corrected = " + statReps.skewness(true));
      System.out.println("skewness, not corrected  = " + statReps.skewness(false));
      System.out.println("excess kurtosis, bias corrected = " + statReps.kurtosis(true, true));
      System.out.println("excess kurtosis, not corrected  = " + statReps.kurtosis(false, true));
      System.out.println("CPU time: " + timer.format() + "\n");
      statReps.quickSort();
      dataToFile(statReps);
   }

   /**
    * Perform m RQMC runs for the given model with n=2^k points, for different
    * types of RQMC points. For each type, the sorted values are saved in a file,
    * and a report is printed to standard output, which can be redirected to a file
    * via `redirectToFile`.
    * 
    */
   public static void simulRepsAllTypes(MonteCarloModelDouble model, int s, int k, int m, boolean crn) throws IOException {
      String modelTag = model.getTag();
      // String ident; // Identifies the case, used in file names.
      int n = (int) Num.TWOEXP[k];
      RandomStream stream = new LFSR258();   // Used to randomize the points.
      Chrono timer = new Chrono();
      System.out.println("Samo25Samples program, RQMC replicates with model: " + model.toString() + "\n");
      TallyStore statReps = new TallyStore(m);

      // --------------------------
      // Objects for lattice points
      System.out.println("***  Lattice points ");
      Rank1Lattice pLat = new Rank1Lattice(n, a18, s);
      RandomShift randShift = new RandomShift(stream);
      BakerTransformedPointSet ptent = new BakerTransformedPointSet(pLat);
      RandomLatticeParams randLatPar = new RandomLatticeParams(true, stream); // Randomizes a for n fixed.
      RandomLatticeParams randLatPar2 = new RandomLatticeParams(n / 2, n, stream); // This one also randomizes n.

      // Lat-RS
      System.out.println("*   Lattice with RS");
      statReps.setName(modelTag + "-" + s + "-Lat-RS-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randShift, m, statReps, crn);
      
      // Lat-RSB
      System.out.println("*   Lattice with RS + tent transform");
      statReps.setName(modelTag + "-" + s + "-Lat-RSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, randShift, m, statReps, crn);

      // Lat-Rv, random a
      System.out.println("*   Lattice with random gen vector a, no shift");
      pLat.clearRandomShift();   // This is essential.
      // pLat = new Rank1Lattice(n, a18, s);
      randLatPar.setRandShift(false);
      statReps.setName(modelTag + "-" + s + "-Lat-Rv-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar, m, statReps, crn);

      // Lat-RvRS, random a and RS
      System.out.println("*   Lattice with random gen vector a and RS");
      randLatPar.setRandShift(true);
      statReps.setName(modelTag + "-" + s + "-Lat-RvRS-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar, m, statReps, crn);

      // Lat-RvRSB, random a and RS + tent
      System.out.println("*   Lattice with random gen vector a and RS + tent");
      statReps.setName(modelTag + "-" + s + "-Lat-RvRSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, randLatPar, m, statReps, crn);

      // Lat-Rpv, random n and a, no shift
      System.out.println("*   Lattice with random n and random gen vector a, no shift");
      pLat.clearRandomShift();
      randLatPar2.setRandShift(false);
      statReps.setName(modelTag + "-" + s + "-Lat-Rpv-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar2, m, statReps, crn );

      // Lat-RpvRS, random n and a and RS
      System.out.println("*   Lattice with random n and random gen vector a, and RS");
      randLatPar2.setRandShift(true);
      statReps.setName(modelTag + "-" + s + "-Lat-RpvRS-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar2, m, statReps, crn);

      // Lat-RpvRSB, random n and a and RS + tent
      System.out.println("*   Lattice with random n, random gen vector a, and RS + tent");
      statReps.setName(modelTag + "-" + s + "-Lat-RpvRSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, randLatPar2, m, statReps, crn);

      // -------------------------
      // Objects for Sobol' points
      System.out.println("*** Sobol points ");
      DigitalNetBase2 p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      ptent = new BakerTransformedPointSet(p);
      // PointSetRandomization norand = new EmptyRandomization(); // No randomization
      PointSetRandomization rds = new RandomShift(stream); // Digital shift
      PointSetRandomization lms = new LMScramble(stream);
      PointSetRandomization lmsrds = new LMScrambleShift(stream);
      PointSetRandomization nus = new NestedUniformScrambling(stream, 53);

      // Sob-RDS System.out.println("* Sobol with RDS alone");
      statReps.setName(modelTag + "-" + s + "-Sob-RDS-" + k + "-" + m);
      simulRepsRQMCSort(model, p, rds, m, statReps, crn);

      // Sob-RDSB System.out.println("* Sobol with RDS + baker transform");
      statReps.setName(modelTag + "-" + s + "-Sob-RDSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, rds, m, statReps, crn);

      // Sob-LMS System.out.println("* Sobol with LMS alone, no shift");
      p.clearRandomShift();     // This is essential to remove the digital shift.
      statReps.setName(modelTag + "-" + s + "-Sob-LMS-" + k + "-" + m);
      simulRepsRQMCSort(model, p, lms, m, statReps, crn);

      // Sob-LMS-RDS System.out.println("* Sobol with LMS+RDS");
      statReps.setName(modelTag + "-" + s + "-Sob-LMS-RDS-" + k + "-" + m);
      simulRepsRQMCSort(model, p, lmsrds, m, statReps, crn);

      // Sob-LMS-RDS-IRB after k
      System.out.println("* Sobol with LMS+RDS+IRB (indep random bits after k)");
      statReps.setName(modelTag + "-" + s + "-Sob-LMS-RDS-IRB-" + k + "-" + m);
      p.addIndepRandomBits(stream);
      simulRepsRQMCSort(model, p, lmsrds, m, statReps, crn);
      p.clearIndepRandomBits();

      // Sob-NUS
      System.out.println("* Sobol with NUS");
      statReps.setName(modelTag + "-" + s + "-Sob-NUS-" + k + "-" + m);
      p.clearRandomShift();  
      CachedPointSet cp = new CachedPointSet(p);
      simulRepsRQMCSort(model, cp, nus, m, statReps, crn);
      
      /*
       * // Sob-Int2 Sob-interlaced-order2
       * System.out.println("* Interlaced Sobol points with LMS+RDS"); DigitalNetBase2
       * p2 = new SobolSequence(k, 60, 2*s); // n = 2^{k} points in 2s dim.
       * DigitalNetBase2 pitl = p2.matrixInterlace(2, s); ptent = new
       * BakerTransformedPointSet(p); // // System.out.println(p.formatPoints()); //
       * simulRepsRQMCSort(model, pitl, nus, m, statReps, crn); simulRepsRQMCSort(model,
       * ptent, nus, m, statReps, crn);
       */

      System.out.println(
            "Total time for simulRepsAllTypes: " + timer.format() + "\n=========================================== \n");
   }

   /**
    * Same thing, but for just a few selected types of RQMC method.
    */
   public static void simulRepsSelectedTypes(MonteCarloModelDouble model, int s, int k, int m, boolean crn) throws IOException {
      String modelTag = model.getTag();
      // String ident; // Identifies the case, used in file names.
      int n = (int) Num.TWOEXP[k];
      RandomStream stream = new LFSR258();
      Chrono timer = new Chrono();
      System.out.println("Samo25Samples program, RQMC replicates with model: " + model.toString() + "\n");
      TallyStore statReps = new TallyStore(m);

      // --------------------------
      // Objects for lattice points
      System.out.println("***  Lattice points ");
      Rank1Lattice pLat = new Rank1Lattice(n, a18, s);
      RandomShift randShift = new RandomShift(stream);
      BakerTransformedPointSet ptent = new BakerTransformedPointSet(pLat);
      RandomLatticeParams randLatPar = new RandomLatticeParams(true, stream); // Randomizes a for n fixed.
      RandomLatticeParams randLatPar2 = new RandomLatticeParams(n / 2, n, stream); // This one also randomizes n.

      // Lat-RvRS, random a and RS
      System.out.println("*   Lattice with random gen vector a and RS");
      randLatPar.setRandShift(true);
      statReps.setName(modelTag + "-" + s + "-Lat-RvRS-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar, m, statReps, crn);

      // Lat-RvRSB, random a and RS + tent
      System.out.println("*   Lattice with random gen vector a and RS + tent");
      statReps.setName(modelTag + "-" + s + "-Lat-RvRSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, randLatPar, m, statReps, crn);

      System.out.println(
            "Total time for simulRepsSelectedTypes: " + timer.format() + "\n=========================================== \n");
   }

   /**
    * Specific models, s, k, method, usually for very large m.
    */
   public static void simulRepsSpecificCases (int m, boolean crn) throws IOException {
      RandomStream stream = new LFSR258();
      Chrono timer = new Chrono();
      System.out.println("Samo25Samples program, Specific cases\n");
      TallyStore statReps = new TallyStore(m);
      MonteCarloModelDouble model;
      int s = 4;
      int k = 10;
      int n = (int) Num.TWOEXP[k];
      
      Rank1Lattice pLat = new Rank1Lattice(n, a18, s);
      pLat.clearRandomShift();
      RandomLatticeParams randLatPar = new RandomLatticeParams(true, stream); // Randomizes a for n fixed.
      RandomLatticeParams randLatPar2 = new RandomLatticeParams(n / 2, n, stream); // This one also randomizes n.
      
      // Lat-Rv, random a, random shift
      model = new MC2(s);
      System.out.println("SmoothPerB4, Lat-RvRS, s=4, k=10 ");
      System.out.println("*   Lattice with random gen vector a, random shift");
      // pLat.clearRandomShift();
      randLatPar.setRandShift(true);
      statReps.setName(model.getTag() + "-" + s + "-Lat-RvRS-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar, m, statReps, crn);

      /*
      
      // Lat-Rpv, random n and a, no shift
      model = new SmoothPerB4(s, 1.0);
      System.out.println("SmoothPerB4, Lat-Rpv, s=8, k=16 ");
      System.out.println("*   Lattice with random n and random gen vector a, no shift");
      pLat.clearRandomShift();
      randLatPar2.setRandShift(false);
      statReps.setName(model.getTag() + "-" + s + "-Lat-Rpv-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar2, m, statReps, crn);

      model = new MC2(s);
      System.out.println("MC2, Lat-Rpv, s=8, k=16 ");
      pLat.clearRandomShift();
      randLatPar2.setRandShift(false);
      statReps.setName(model.getTag() + "-" + s + "-Lat-Rpv-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar2, m, statReps, crn);  
      
      model = new MC2(s);
      System.out.println("MC2, Lat-Rpv, s=8, k=16 ");
      pLat.clearRandomShift();
      //RandomLatticeParams randLatPar = new RandomLatticeParams(true, stream); // Randomizes a for n fixed.
      RandomLatticeParams randLatPar2 = new RandomLatticeParams(n / 2, n, stream); // This one also randomizes n.
      randLatPar2.setRandShift(false);
      statReps.setName(model.getTag() + "-" + s + "-Lat-Rpv-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar2, m, statReps, crn);
  
      model = new MC2(s);
      System.out.println("MC2, Sob-LMS, s=8, k=16 ");
      DigitalNetBase2 p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      PointSetRandomization lms = new LMScramble(stream);
      // PointSetRandomization lmsrds = new LMScrambleShift(stream);
      p.clearRandomShift(); 
      statReps.setName(model.getTag() + "-" + s + "-Sob-LMS-" + k + "-" + m);
      simulRepsRQMCSort(model, p, lms, m, statReps, crn);

      System.out.println("MC2, Sob-RDSB, s=8, k=16 ");
      PointSetRandomization rds = new RandomShift(stream); // Digital shift
      BakerTransformedPointSet ptent = new BakerTransformedPointSet(p);
      statReps.setName(model.getTag() + "-" + s + "-Sob-RDSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, rds, m, statReps, crn);
      */
      System.out.println(
            "Total time for simulRepsLatRv: " + timer.format() + "\n=========================================== \n");
   }


   /**
    * Specific models, s, k, method, usually for very large m.
    */
   public static void simulRepsSpecificCases2 (int m, boolean crn) throws IOException {
      RandomStream stream = new LFSR258();
      Chrono timer = new Chrono();
      System.out.println("Samo25Samples program, Specific cases\n");
      TallyStore statReps = new TallyStore(m);
      MonteCarloModelDouble model;
      int s = 8;
      int k = 16;
      int n = (int) Num.TWOEXP[k];
      
      model = new MC2(s);
      System.out.println("MC2, Lat-Rv, s=8, k=16 ");
      Rank1Lattice pLat = new Rank1Lattice(n, a18, s);
      pLat.clearRandomShift();
      RandomLatticeParams randLatPar = new RandomLatticeParams(true, stream); // Randomizes a for n fixed.
      randLatPar.setRandShift(false);
      statReps.setName(model.getTag() + "-" + s + "-Lat-Rv-" + k + "-" + m);
      // simulRepsRQMCSort(model, pLat, randLatPar, m, statReps, crn);
 
      System.out.println("MC2, Sob-LMS, s=8, k=16 ");
      DigitalNetBase2 p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      PointSetRandomization lms = new LMScramble(stream);
      PointSetRandomization lmsrds = new LMScrambleShift(stream);
      p.clearRandomShift(); 
      statReps.setName(model.getTag() + "-" + s + "-Sob-LMS-" + k + "-" + m);
      // simulRepsRQMCSort(model, p, lms, m, statReps, crn);

      System.out.println("MC2, Sob-LMS-RDS, s=8, k=16 ");
      p.clearRandomShift(); 
      statReps.setName(model.getTag() + "-" + s + "-Sob-LMS-RDS-" + k + "-" + m);
      // simulRepsRQMCSort(model, p, lmsrds, m, statReps, crn);
      
      System.out.println("MC2, Sob-RDSB, s=8, k=16 ");
      PointSetRandomization rds = new RandomShift(stream); // Digital shift
      BakerTransformedPointSet ptent = new BakerTransformedPointSet(p);
      statReps.setName(model.getTag() + "-" + s + "-Sob-RDSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, rds, m, statReps, crn);
      
      s = 4;  k = 14;  n = (int) Num.TWOEXP[k];
      model = new MC2(s);
      System.out.println("MC2, Sob-RDSB, s=4, k=14 ");
      p = new SobolSequence(k, 53, s); // n = 2^{k} points in s dim.
      ptent = new BakerTransformedPointSet(p);
      statReps.setName(model.getTag() + "-" + s + "-Sob-RDSB-" + k + "-" + m);
      simulRepsRQMCSort(model, ptent, rds, m, statReps, crn);
      
      System.out.println(
            "Total time for simulRepsLatRv: " + timer.format() + "\n=========================================== \n");
   }


   /**
    * To make simple tests and trace for small s, k, and m.
    */
   public static void simulTrace (int m, boolean crn) throws IOException {
      // RandomStream stream = new LFSR258();
      RandomStream stream = new LFSR113();
      System.out.println("Running SimulRepsSmallTest, with trace\n");
      TallyStore statReps = new TallyStore(m);
      MonteCarloModelDouble model;
      int s = 4;
      int k = 4;
      int n = (int) Num.TWOEXP[k];
      
      Rank1Lattice pLat = new Rank1Lattice(n, a18, s);
      pLat.clearRandomShift();
      RandomLatticeParams randLatPar = new RandomLatticeParams(true, stream); // Randomizes a for n fixed.
      
      // Lat-Rv, random a, random shift
      model = new SumUeU(s);
      System.out.println("SumUeU, Lat-RvRS");
      System.out.println("*   Lattice with random gen vector a, random shift");
      // pLat.clearRandomShift();
      randLatPar.setRandShift(true);
      statReps.setName(model.getTag() + "-" + s + "-Lat-RvRS-" + k + "-" + m);
      simulRepsRQMCSort(model, pLat, randLatPar, m, statReps, crn);
   }
   
   /**
    * For one model, perform m RQMC runs for all point set sizes k from mink to
    * maxk, by steps of 2, and puts the results in arrays. After that, the arrays
    * are used to output data sets in files.
    */
   public static void simulRepsAllSizes(MonteCarloModelDouble model, int s, int mink, int maxk, int m, boolean crn)
         throws IOException {
      // redirectToFile(model.getTag() + "-" + s + "-" + m);
      System.out.println("RQMC replicates with model: " + model.toString() + ", s = " + s + "\n");
      Chrono timer = new Chrono();
      for (int k = mink; k <= maxk; k += 2) { // For each point set size
         simulRepsAllTypes(model, s, k, m, crn);
         //  simulRepsSelectedTypes(model, s, k, m, crn);
      }
      System.out.println(
            "\nTotal time for simulAllSizes: " + timer.format() + "\n=========================================== \n");
   }

}
