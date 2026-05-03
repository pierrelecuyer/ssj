package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements a polynomial function taken from @cite vLEC22m:
 * @f[
 *   f(u_1,\dots,u_s) = \prod_{j=1}^s (1 + a_j \cdot (u_j-1/2))$.
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with increasing weights @f$a_j = j/s@f$.
 * This function is smooth but not one-periodic.
 * It was also used in @cite vLEC24a and @cite vLEC26a. 
 */
public class Polynomial implements MonteCarloModelDouble {

   int s;
   double prod;
   double[] a;

   // Constructor.
   public Polynomial(int s) {
      this.s = s;
      a = new double[s];
      for (int j = 0; j < s; j++)
         a[j] = (double) (j+1) / (double) s;
   }

   public void simulate(RandomStream stream) {
      prod = 1.0;
      for (int j = 0; j < s; j++) {
         prod *= 1.0 + a[j] * (stream.nextDouble() - 0.5);
      }
   }

   public double getPerformance() {
      return prod - 1.0;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "Polynomial test function";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "Polynomial";
   }
}