package rqmcexperiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.ScaledHistogram;
import umontreal.ssj.stat.TallyHistogram;
import umontreal.ssj.stat.TallyStore;

/**
 * This class contains facilities to make bootstrap samples of size @f$r@f$ and
 * produce standalone LaTeX histograms that compare the distributions of the
 * average @f$A_r@f$ and median @f$M_r@f$ for SAMO 2025 experiments.
 * Each plot contains two superposed histograms.
 * For eaach histogram, we also add 
 * 
 */
public class HistSamo25ArMr {

   /**
    * Draws @f$m@f$ realizations of @f$A_r@f$ and @f$M_r@f$ by using the data from `inputFile`
    * and generates the LaTeX code for the two overlaid histograms of these values.
    */
   public static String makeArMrHistogramLatex(File inputFile, String title, int numObs,
         int m, int r, int numBins, int[] marks, RandomStream stream, boolean crnboot) throws IOException {

      TallyStore tallyInput = new TallyStore();
      tallyInput.fillFromFile(inputFile.getAbsolutePath());
      TallyStore statAver = new TallyStore();
      TallyStore statMed = new TallyStore();

      if (crnboot) stream.resetStartStream();   // Reset to start of stream for each case.
      MeanMedianMSE.bootstrapArMrValues(tallyInput, m, r, stream, statAver, statMed);
      return makeDoubleHistogramLatex(statAver, statMed, title, numObs, r, numBins, marks);
   }
   
   /**
    * This function assumes that @f$m@f$ realizations of @f$A_r@f$ and @f$M_r@f$ are already
    * in {@code data1} and {@code data2}, respectively, 
    * and it generates the LaTeX code for the two overlaid histograms of these values.
    */
   public static String makeDoubleHistogramLatex(TallyStore data1, TallyStore data2, String titleName, 
         int numObs, int r, int numBins, int[] marks) throws IOException {
      data1.quickSort();
      data2.quickSort();
      // int n1 = data1.numberObs();
      // int n2 = data2.numberObs();
      double a = Math.min(data1.min(), data2.min());
      double b = Math.max(data1.max(), data2.max());
      double range = b - a;
      // System.out.println("makeDoubleHistogramLatex: a = " + a + ", b = " + b);

      TallyHistogram hist1 = new TallyHistogram(a, b + range * 1.0e-12, numBins);
      hist1.fillFromTallyStore(data1);     
      ScaledHistogram scHist1 = new ScaledHistogram(hist1);
      TallyHistogram hist2 = new TallyHistogram(a, b + range * 1.0e-12, numBins);
      hist2.fillFromTallyStore(data2);     
      ScaledHistogram scHist2 = new ScaledHistogram(hist2);
      // ScaledHistogram scHist = new ScaledHistogram();
      
      // System.out.println(hist.toString());
      scHist1.setAxisOptions("title={" + titleName + "}, width=4.4cm, height=3.0cm, scale only axis, \n" +
             "  ymin=0.0, xmin = " + (a - 0.01 * range) + ", xmax = " + (b + 0.01 * range) + 
             ",\n  ylabel={}, yticklabels={}, \n" +
             "  scaled x ticks=true, minor x tick num=0, scaled y ticks=false, \n" +
             "  every x tick label/.append style={scale=0.6, transform shape}, \n" +
             "  every x tick scale label/.style={at={(axis description cs:1, 0)}, \n" +
                "  anchor=north east, xshift=2pt, yshift=-6.2pt, inner sep=0pt}, \n");
      scHist1.setAddPlotOptions("mark=none,very thin,fill=green!25,opacity=0.6,fill opacity=0.6");
      scHist2.setAddPlotOptions("mark=none,very thin,fill=red!25,opacity=0.6,fill opacity=0.6");
      // Make the latex file.
      String latexCode = scHist1.toLatexTwoHist(scHist2);
      // Add the marks.
      StringBuilder coords = new StringBuilder();
      for(int i : marks)
         coords.append("(").append(String.format(Locale.US, "%.17g", data1.getArray()[i])).append(",0) ");
      String adds = "\\addplot+[only marks, mark=|, mark size=2.5pt, "
            + "mark options={green,thick}, forget plot] coordinates {" + coords + "};";
      latexCode = latexCode.replace("\\end{axis}", adds + "\n\\end{axis}");
      coords = new StringBuilder();
      for(int i : marks)
         coords.append("(").append(String.format(Locale.US, "%.17g", data2.getArray()[i])).append(",0) ");
      adds = "\\addplot+[only marks, mark=|, mark size=2.5pt, "
            + "mark options={red,thick}, forget plot] coordinates {" + coords + "};";
      latexCode = latexCode.replace("\\end{axis}", adds + "\n\\end{axis}");
      return latexCode;
   }

   /**
    * Writes a LaTeX table comparing @f$A_r@f$ and @f$M_r@f$ for one model
    * and dimension.
    *
    * <p>For each RQMC method and sample size, it reads the experiment
    * observations, bootstraps samples of size @f$r@f$, and plots the resulting
    * averages and medians as overlaid histograms.
    */
   private static void writeHistogramPageBody(
         PrintWriter out, File inputFolder, String modelTag, String[] methods, int s,
         int[] ks, int m, int r, int numObs, int numBins, int[] marks, String pageTitle, RandomStream stream,
         boolean crnboot) throws IOException {

      out.println("\\sethistwidths{" + ks.length + "}");
      out.print(
            "\\begin{longtable}{@{}>{\\centering\\arraybackslash}p{\\histmethodwidth}");
      for (int i = 0; i < ks.length; i++)
         out.print("@{}>{\\centering\\arraybackslash}p{\\histcellwidth}");
      out.println("@{}}");
      String titleLatex = "\\scriptsize\\textbf{" + pageTitle + "}";
      String phantomTitleLatex = "\\phantom{" + titleLatex + "}";

      out.println("\\multicolumn{" + (ks.length + 1)
            + "}{c}{" + titleLatex + "} \\\\[2mm]");
      out.print("{}");
      for (int k : ks)
         out.print(" & \\makebox[\\histcellwidth][c]"
               + "{{\\scriptsize $n=2^{" + k + "}$}}");
      out.println(" \\\\[1.5mm]");

      out.println("\\endfirsthead");
      out.println("\\multicolumn{" + (ks.length + 1)
            + "}{c}{" + phantomTitleLatex + "} \\\\[2mm]");

      out.print("{}");
      for (int k : ks)
         out.print(" & \\makebox[\\histcellwidth][c]"
               + "{{\\scriptsize $n=2^{" + k + "}$}}");
      out.println(" \\\\[1.5mm]");
      out.println("\\endhead");

      for (String method : methods) {
         out.print("\\raisebox{0.7cm}{\\rotatebox{90}"
               + "{\\scriptsize " + method + "}}");
         for (int k : ks) {
            String fileName = modelTag + "-" + s + "-" + method + "-" + k + "-" + m + ".dat";
            File file = new File(inputFolder, fileName);
            if (!file.exists()) {
               System.out.println("Missing file: " + fileName);
               out.print(" & \\makebox[\\histcellwidth][c]"
                     + "{{\\tiny Missing}}");
               continue;
            }
            String title = method + ", $s=" + s + "$, $k=" + k + "$";
            String latexCode = makeArMrHistogramLatex(file, title, numObs, m, r, numBins, marks, stream, crnboot);
            out.print(" & \\makebox[\\histcellwidth][c]{");
            out.print(latexCode);
            out.println("}");
         }
         out.println("\\\\[1.5mm]");
      }
      out.println("\\end{longtable}");
   }

   /**
    * Creates a standalone LaTeX histogram document for one model.
    *
    * <p>The caller chooses and initializes the random stream. To reproduce the
    * same histograms, use the same stream implementation and initial seed. If
    * {@code resetBeforeEachHistogram} is {@code true}, every histogram starts
    * from that same initial stream state, independently of traversal order.
    *
    * @param inputFolder folder containing the experiment data files
    * @param outputFolder folder in which to write the LaTeX file
    * @param modelTag model identifier used in file names and titles
    * @param methods RQMC methods to include
    * @param sDims dimensions to include
    * @param ks exponents defining sample sizes @f$n=2^k@f$
    * @param m number of observations recorded in each input file
    * @param stream caller-owned random stream used for bootstrapping
    * @param resetBeforeEachHistogram if {@code true}, reset {@code stream} to
    *        the start of the stream before generating each histogram
    * @throws IOException if an input or output file cannot be accessed
    */
   public static void writeModelFile(
         String inputFolder, String outputFolder,
         String modelTag, String[] methods,
         int[] sDims, int[] ks, int m, int r, int numObs, int numBins, int[] marks,
         RandomStream stream, boolean crnboot) throws IOException {

      if (ks.length == 0 || sDims.length == 0)
         throw new IllegalArgumentException(
               "ks and sDims must not be empty.");
      if (stream == null)
         throw new IllegalArgumentException("stream must not be null.");

      File inputDir = new File(inputFolder);
      File outputDir = new File(outputFolder);
      if (!outputDir.exists() && !outputDir.mkdirs())
         throw new IOException(
               "Could not create output directory: " + outputDir);
      File outFile = new File(
            outputDir, modelTag + "-ArMr-hist.tex");
      try (PrintWriter out =
            new PrintWriter(new FileWriter(outFile))) {
         out.println("\\documentclass[letterpaper]{article}");
         out.println("\\usepackage[margin=0.2in]{geometry}");
         out.println("\\usepackage{amsmath}");
         out.println("\\usepackage{graphicx}");
         out.println("\\usepackage{array}");
         out.println("\\usepackage{longtable}");
         out.println("\\usepackage{pgfplots}");
         out.println("\\pgfplotsset{compat=1.18}");
         out.println("\\pagestyle{empty}");
         out.println();

         out.println("\\newlength{\\histmethodwidth}");
         out.println("\\newlength{\\histcellwidth}");
         out.println("\\setlength{\\histmethodwidth}{0.2cm}");
         out.println("\\newcommand{\\sethistwidths}[1]{%");
         out.println("  \\setlength{\\histcellwidth}"
               + "{\\dimexpr(\\textwidth-\\histmethodwidth)/#1\\relax}%");
         out.println("  \\ifdim\\histcellwidth>5.3cm");
         out.println("    \\setlength{\\histcellwidth}{5.3cm}%");
         out.println("  \\fi");
         out.println("}");
         out.println();

         out.println("\\begin{document}");
         out.println();

         for (int s : sDims) {
            String mStr = Integer.toString(m);
            String samples =
                  m > 0 && mStr.matches("10*")
                        ? "$10^{" + (mStr.length() - 1)
                              + "}$ samples"
                        : m + " samples";
            String pageTitle =
                  "RQMC comparison of $A_r$ and $M_r$: "
                        + modelTag.replace("_", "\\_")
                        + " s = " + s
                        + " (" + samples + ")";
            writeHistogramPageBody(out, inputDir, modelTag, methods,
                  s, ks, m, r, numObs, numBins, marks, pageTitle, stream, crnboot);
            out.println("\\clearpage");
            out.println();
         }
         out.println("\\end{document}");
      }
      System.out.println("LaTeX file created:");
      System.out.println(outFile.getAbsolutePath());
   }

}
