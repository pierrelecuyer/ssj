package rqmcexperiments;

import java.io.*;
import umontreal.ssj.stat.*;

/**
 * This is the main program used to make experiments for the WSC 2026 paper on RQMC.
 * It makes RQMC replicates for selected functions, a fixed list of RQMC methods
 * specified in `WSC26RQMCSamples64`, for selected numbers of dimensions s
 * and numbers of points @f$2^k@f$. The output is a set of files, two for each function 
 * and dimension, that contain data sets with the log-variance and the mean for each RQMC estimator
 * (each column) and each value of `k` (each row). 
 */

public class HistLatex {

   public static void main(String[] args) throws IOException {

      // String directory = "C:/Users/Lecuyer/Dropbox/samo25/paperdat/";  // For testing
      String directory = "/home/otman/Documents/GitHub/Data/wsc23/test/";      // For testing
      
      // double xmin = -5.2E-12;
      // double xmax =  5.2E-12;
      double xmin = -1.4E-4;
      double xmax =  1.4E-4;
      int numBins = 40;
      // int numObs  = 1000000; 
     
      xmax = 1.1E-4;
      xmin = -xmax;
      TallyHistogram hist = new TallyHistogram(xmin, xmax, numBins);
      // hist.fillFromFile(directory + "SmoothPerB4-8-Lat-RS-16.dat");
      hist.fillFromFile(directory + "SmoothPerB4-8-Sob-LMS-RDS-16-1000000.dat");
      // ScaledHistogram histScaled = new ScaledHistogram(hist, 1.0);
      // String histStr = histScaled.toLatex(false, true);
      ScaledHistogram sc_hist = new ScaledHistogram(hist, 1.0);
      sc_hist.setAxisOptions("title={SmoothPerB4-8-Sob-LMS-RDS-16}, xlabel={$x$}, ylabel={Density}, width=8cm, height=5cm");
      sc_hist.setAddPlotOptions("fill=blue!30, draw=black");
      System.out.println(sc_hist.toLatex(true, false));
      
      xmax = 1.25E-6;
      xmin = -xmax;
      hist = new TallyHistogram(xmin, xmax, numBins);
      hist.fillFromFile(directory + "MC2-8-Sob-LMS-RDS-16-1000000.dat");
      // histScaled = new ScaledHistogram(hist, 1.0);
      // histStr = histScaled.toLatex(false, true);
      sc_hist = new ScaledHistogram(hist, 1.0);

      sc_hist.setAxisOptions(
    		   "title={MC2-8-Sob-LMS-RDS-16}, xlabel={$x$}, ylabel={Density}, " +
    		   "width=8cm, height=5cm, " +
    		   "scaled y ticks=false, " +
    		   "every x tick scale label/.append style={font=\\scriptsize}"
    		);
      sc_hist.setAddPlotOptions("fill=blue!30, draw=black");
      System.out.println(sc_hist.toLatex(false, true));
   }
}
