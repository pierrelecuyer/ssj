package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements the corner peak function from Genz @cite iGEN87a, defined as 
 * @f[
 *   f(u_1,\dots,u_s) = \left(1 + \sum_{j=1}^s a_j u_j\right)^{-(s+1)}
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with @f$a_j = j/s@f$.
 * See also @cite iSUR13a.
 * This function has a sharp peak in one corner of the unit hypercube,
 * so it can be difficult to integrate by Monte Carlo methods.
 */
public class CornerPeak implements MonteCarloModelDouble {

   int s;
   double sum;
   double[] a;

   // Constructor.
   public CornerPeak(int s) {
      this.s = s;
      a = new double[s];
      for (int j = 0; j < s; j++)
         a[j] = (double) (j + 1) / (double) s;
   }

   // Generates and returns X, without IS.
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += a[j] * stream.nextDouble();
      }
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      return Math.pow((1.0 + sum), -(s + 1));
   }

   // Descriptor of this model.
   @Override
   public String toString() {
      return "Corner Peak function from Genz";
   }

   // Short descriptor (tag) for this model.
   @Override
   public String getTag() {
      return "CornerPeak";
   }
}