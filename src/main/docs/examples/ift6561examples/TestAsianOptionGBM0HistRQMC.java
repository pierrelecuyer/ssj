package ift6561examples;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import umontreal.ssj.stochprocess.*;
import umontreal.ssj.charts.HistogramChart;
import umontreal.ssj.hups.DigitalNet;
import umontreal.ssj.hups.PointSetIterator;
import umontreal.ssj.hups.SobolSequence;
import umontreal.ssj.rng.*;
import umontreal.ssj.randvar.NormalGen;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Chrono;

public class TestAsianOptionGBM0HistRQMC {

   // For testing with a GBM process, MC only.
   public static void main(String[] args) throws IOException {
      int numObsTimes = 12;
      double T1 = 1.0 / numObsTimes;
      double T = 1.0;
      double strike = 100.0;
      double s0 = 100.0;
      double r = 0.05;
      double sigma = 0.5;
//		int n = 65536, k = 16;
      int n = 4096, k = 12;

      AsianOption asian = new AsianOption(r, numObsTimes, T1, T, strike);
      RandomStream noise = new LFSR113();
      NormalGen gen = new NormalGen(noise);
      GeometricBrownianMotion gbmSeq = new GeometricBrownianMotion(s0, r, sigma, new BrownianMotion(0, 0, 1, gen));
      asian.setProcess(gbmSeq);

      TallyStore statValue = new TallyStore("Stats on discounted payoff", n);
      TallyStore statValuePos = new TallyStore("Stats on positive discounted payoffs only", n);

      System.out.println(asian.toString());
      Chrono timer = new Chrono();
      // asian.simulateRuns(n, noise, statValue, statValuePos);
      // System.out.println("Total CPU time: " + timer.format() + "\n");

      statValue.setConfidenceIntervalStudent();
      System.out.println(statValue.report(0.95, 5));
      System.out.printf("Variance per run: %9.4g%n", statValue.variance() * n);
      statValuePos.setConfidenceIntervalStudent();
      System.out.println(statValuePos.report(0.95, 5));
      System.out.printf("Variance per run: %9.4g%n", statValuePos.variance() * n);
      double fractionZero = 1.0 - (double) statValuePos.numberObs() / (double) n;
      System.out.printf("Proportion of zero payoffs: %12.6f%n", fractionZero);

      HistogramChart hist = new HistogramChart("Distribution of positive discounted payoff", "Payoff", "Frequency",
            statValuePos.getArray(), statValuePos.numberObs());
      double[] bounds = { 0, 150, 0, n / 20 };
      hist.setManualRange(bounds);
      (hist.getSeriesCollection()).setBins(0, 60, 0, 150);
      hist.view(800, 500);
      // hist.toLatexFile (10.0, 8.0, "asianchart.tex");
      String histLatex = hist.toLatex(10.0, 8.0);
      Writer file = new FileWriter("asianchartMC.tex");
      file.write(histLatex);
      file.close();

      // Now RQMC
      System.out.printf("\n\n" + "Simulation with Sobol points");
      TallyStore statValueRQMC = new TallyStore("Stats on discounted payoff RQMC", n);
      TallyStore statValuePosRQMC = new TallyStore("Stats on positive discounted payoffs only, RQMC", n);
      DigitalNet p = new SobolSequence(k, 31, numObsTimes); // 2^{k} points.
      n = p.getNumPoints();
      PointSetIterator stream = p.iterator();
      p.leftMatrixScramble(noise);
      p.addRandomShift(0, p.getDimension(), noise);
      asian.simulateRuns(n, stream, statValueRQMC, statValuePosRQMC);
      System.out.println("Total CPU time:      " + timer.format() + "\n");
      fractionZero = 1.0 - (double) statValuePos.numberObs() / (double) n;
      System.out.printf("Proportion of zero payoffs: %12.6f%n", fractionZero);

      hist = new HistogramChart("Distribution of positive discounted payoff RQMC", "Payoff", "Frequency",
            statValuePosRQMC.getArray(), statValuePosRQMC.numberObs());
      // double[] bounds = { 0, 150, 0, 35000 };
      hist.setManualRange(bounds);
      (hist.getSeriesCollection()).setBins(0, 60, 0, 150);
      hist.view(800, 500);
      histLatex = hist.toLatex(10.0, 8.0);
      file = new FileWriter("asianchartRQMC.tex");
      file.write(histLatex);
      file.close();

   }

}
