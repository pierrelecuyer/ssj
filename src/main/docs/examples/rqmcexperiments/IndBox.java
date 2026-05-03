package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements a function defined as a sum of indicators:
 * @f[
 *   f(u_1,\dots,u_s) =  -s/\pi + \sum_{j=1}^s \II[u_j < 1/\pi]$ 
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$.
 * If @f$\bm u@f$ has the uniform distribution, 
 * the output has a binomial distribution re-centered at zero.
 * This is a discontinuous function, but the discontinuities are
 * axis-parallel, which is favorable to RQMC.
 */
public class IndBox implements MonteCarloModelDouble {

   int s;
   double a, b, sum;

   // Constructor.
   public IndBox(int s) {
      this.s = s;
      b = 1.0 / Math.PI;
      a = s * b;
   }

   // Generates and returns X, without IS.
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         if (stream.nextDouble() < b)
            sum += 1.0;
      }
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      return sum - a;
   }

   // Descriptor of this model.
   @Override
   public String toString() {
      return "IndBox: Sum of indicators of (U_j < 1/pi)";
   }

   // Short descriptor (tag) for this model.
   @Override
   public String getTag() {
      return "IndBox";
   }
}
