package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.util.Num;

/**
 * Implements the general Genz Gaussian peak function.
 *
 * The function is defined by
 * @f[
 *   f(u_1,\dots,u_s) =
 *   \exp\left(-\sum_{j=1}^s c_j^2 (u_j - w_j)^2\right),
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$.
 */
public class GenzGaussian implements MonteCarloModelDouble {

   private final int s;
   private final double[] c;
   private final double[] w;
   private final double exactMean;

   private double sum;

   /**
    * Constructs a Genz Gaussian peak function in dimension {@code s}.
    *
    * The default parameter choice is @f$c_j = j/s@f$ for
    * @f$j = 1,\dots,s@f$ and @f$w_j = 1/2@f$ for all @f$j@f$.
    *
    * @param s dimension of the function
    */
   public GenzGaussian(int s) {
      this(s, defaultC(s), defaultW(s));
   }

   /**
    * Constructs a Genz Gaussian peak function with user-specified parameters.
    *
    * @param s dimension of the function
    * @param c scale parameters, all strictly positive
    * @param w location parameters, all in the open interval @f$(0,1)@f$
    */
   public GenzGaussian(int s, double[] c, double[] w) {
      if (s <= 0)
         throw new IllegalArgumentException("s must be positive");
      if (c == null || w == null || c.length != s || w.length != s)
         throw new IllegalArgumentException("c and w must have length s");

      for (int j = 0; j < s; j++) {
         if (c[j] <= 0.0)
            throw new IllegalArgumentException("c[" + j + "] must be positive");
         if (w[j] <= 0.0 || w[j] >= 1.0)
            throw new IllegalArgumentException("w[" + j + "] must be in (0, 1)");
      }

      this.s = s;
      this.c = c.clone();
      this.w = w.clone();
      this.exactMean = computeExactMean();
   }

   /**
    * Simulates one observation of the model.
    *
    * @param stream random stream used to generate the coordinates
    */
   @Override
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         double diff = stream.nextDouble() - w[j];
         sum += c[j] * c[j] * diff * diff;
      }
   }

   /**
    * Returns the centered performance from the last simulation.
    *
    * @return function value minus the exact mean
    */
   @Override
   public double getPerformance() {
      return Math.exp(-sum) - exactMean;
   }

   /**
    * Computes the exact mean of the Genz Gaussian peak function.
    *
    * @return exact integral over @f$[0,1]^s@f$
    */
   private double computeExactMean() {
      double integral = 1.0;
      double sqrtPiOver2 = Math.sqrt(Math.PI) / 2.0;

      for (int j = 0; j < s; j++) {
         integral *= sqrtPiOver2
               * (Num.erf(c[j] * w[j]) + Num.erf(c[j] * (1.0 - w[j])))
               / c[j];
      }

      return integral;
   }

   @Override
   public String toString() {
      return "Genz Gaussian peak function";
   }

   @Override
   public String getTag() {
      return "GenzGaussian";
   }

   /**
    * Creates the default scale parameter vector.
    *
    * @param s dimension of the function
    * @return default scale parameters
    */
   private static double[] defaultC(int s) {
      if (s <= 0)
         throw new IllegalArgumentException("s must be positive");

      double[] c = new double[s];
      for (int j = 0; j < s; j++)
         c[j] = (double) (j + 1) / (double) s;

      return c;
   }

   /**
    * Creates the default location parameter vector.
    *
    * @param s dimension of the function
    * @return default location parameters
    */
   private static double[] defaultW(int s) {
      if (s <= 0)
         throw new IllegalArgumentException("s must be positive");

      double[] w = new double[s];
      for (int j = 0; j < s; j++)
         w[j] = 0.5;

      return w;
   }
}