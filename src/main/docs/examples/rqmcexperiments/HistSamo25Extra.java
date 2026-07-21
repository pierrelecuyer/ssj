package rqmcexperiments;

import java.io.IOException;

/**
 * Similar to `HistSamo25`, but this one produces only selected histograms,
 * usually for larger sample sizes.  
 */
public class HistSamo25Extra {


   public static void main(String[] args) throws IOException {

      String inputFolder = "C:/Users/Lecuyer/Dropbox/samo25/datapl/";
      String outputFolder = "C:/Users/Lecuyer/Dropbox/samo25/paperdat/";
     
      String[] fileNames = new String[] {
            "SmoothPerB4-8-Lat-Rv-16-1000000", 
            "SmoothPerB4-8-Lat-Rpv-16-1000000", 
            "SmoothPerB4-8-Lat-RvRS-16-1000000", 
            "SmoothPerB4-8-Sob-LMS-RDS-16-1000000", 
            "MC2-8-Lat-Rv-16-1000000", 
            "MC2-8-Lat-Rpv-16-1000000", 
            "MC2-8-Sob-LMS-16-1000000", 
            "MC2-8-Sob-LMS-RDS-16-1000000", 
            "MC2-8-Sob-RDSB-16-1000000", 
            "MC2-4-Sob-RDSB-14-1000000", 
         };
      String[] titleNames = new String[] {
            "SmoothPerB4-8-Lat-Rv-16-M", 
            "SmoothPerB4-8-Lat-Rpv-16-M", 
            "SmoothPerB4-8-Lat-RvRS-16-M", 
            "SmoothPerB4-8-Sob-LMS-RDS-16-M", 
            "MC2-8-Lat-Rv-16-M", 
            "MC2-8-Lat-Rpv-16-M", 
            "MC2-8-Sob-LMS-16-M", 
            "MC2-8-Sob-LMS-RDS-16-M", 
            "MC2-8-Sob-RDSB-16-M", 
            "MC2-4-Sob-RDSB-14-M",
         };
      String[] legendAnchor = new String[] {
            "pos=north east", "pos=north east", "pos=north west", "pos=north west", 
            "pos=north east", "pos=north east", "pos=north east", "pos=north east", 
            "style={at={(0.5, 0.97)}, anchor=north}", 
            "style={at={(0.5, 0.97)}, anchor=north}", 
         };

      for (int i = 0; i < fileNames.length; i++)
         HistSamo25Paper.makeSimpleHistogramLatex(fileNames[i], titleNames[i], legendAnchor[i], 200);
      System.out.println("ALL DONE ");
   }
}
