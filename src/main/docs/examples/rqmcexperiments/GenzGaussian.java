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

   private int s;
   private double[] c;
   private double[] w;

   private double sum;
   private double exactMean = Double.NaN;

   /**
    * Constructs a Genz Gaussian peak function in dimension {@code s}.
    *
    * The default parameter choice is @f$c_j = j/s@f$ for
    * @f$j = 1,\dots,s@f$ and @f$w_j = 1/2@f$.
    *
    * @param s dimension of the function
    */
   public GenzGaussian(int s) {
      this(defaultC(s), defaultW(s));
   }

   /**
    * Constructs a Genz Gaussian peak function with user-specified parameters.
    *
    * @param c scale parameters, all strictly positive
    * @param w location parameters, all in the open interval @f$(0,1)@f$
    */
   public GenzGaussian(double[] c, double[] w) {
      setParameters(c, w);
   }

   /**
    * Sets the parameters and resets the cached exact mean.
    *
    * @param c scale parameters, all strictly positive
    * @param w location parameters, all in the open interval @f$(0,1)@f$
    */
   private void setParameters(double[] c, double[] w) {
      if (c == null || w == null || c.length == 0 || c.length != w.length)
         throw new IllegalArgumentException("c and w must be nonempty arrays of the same length");

      this.s = c.length;
      this.c = c.clone();
      this.w = w.clone();

      for (int j = 0; j < s; j++) {
         if (this.c[j] <= 0.0)
            throw new IllegalArgumentException("c[" + j + "] must be positive");
         if (this.w[j] <= 0.0 || this.w[j] >= 1.0)
            throw new IllegalArgumentException("w[" + j + "] must be in (0, 1)");
      }

      exactMean = Double.NaN;
   }

   /**
    * Returns the dimension of this Genz Gaussian peak function.
    *
    * @return dimension of the function
    */
   public int getDimension() {
      return s;
   }

   /**
    * Returns a copy of the scale parameters.
    *
    * @return copy of the scale parameter array
    */
   public double[] getC() {
      return c.clone();
   }

   /**
    * Returns a copy of the location parameters.
    *
    * @return copy of the location parameter array
    */
   public double[] getW() {
      return w.clone();
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
    * @return raw function value minus the exact mean
    */
   @Override
   public double getPerformance() {
      return getRawPerformance() - getExactMean();
   }

   /**
    * Returns the raw integrand value from the last simulation.
    *
    * @return value of the Genz Gaussian peak function
    */
   public double getRawPerformance() {
      return Math.exp(-sum);
   }

   /**
    * Evaluates the raw Genz Gaussian peak function at a given point.
    *
    * @param u point in @f$[0,1]^s@f$
    * @return function value at {@code u}
    */
   public double evaluate(double[] u) {
      if (u == null || u.length != s)
         throw new IllegalArgumentException("u must have length " + s);

      double value = 0.0;
      for (int j = 0; j < s; j++) {
         double diff = u[j] - w[j];
         value += c[j] * c[j] * diff * diff;
      }

      return Math.exp(-value);
   }

   /**
    * Returns the exact mean, computing it only once.
    *
    * @return exact mean over @f$[0,1]^s@f$
    */
   private double getExactMean() {
      if (Double.isNaN(exactMean))
         exactMean = computeExactMean();

      return exactMean;
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