package rqmcexperiments;

import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;

/**
 * Implements the function 
 * @f[
 *   f(u_1,\dots,u_s) = -1 + (2/s) \sum_{j=1}^s u_j
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$.
 * This is just a sum of uniform random variables rescaled and centered to have mean zero.
 */
public class SumUniforms implements MonteCarloModelDouble {

   int s;
   double sum;

   // Constructor.
   public SumUniforms(int s) {
      this.s = s;
   }

   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += stream.nextDouble();
      }
   }

   public double getPerformance() {
      return 2.0 * sum / s - 1.0;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "SumUniforms: Twice the average of s U(0,1)";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "SumUniforms";
   }
}
