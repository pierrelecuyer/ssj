package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements the smooth gaussian function
 * @f[
 *   f(u_1,\dots,u_s) = =  -\Phi( 1/\sqrt{2}) + \Phi(1+ s^{-1/2}\sum_{j=1}^s \Phi^{-1}(u_j))
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$.
 * This function is smooth, bounded, and monotone in each @f$u_j@f$.
 * It was used in @cite vLEC23a, @cite vLEC24a, and @cite vLEC26a.
 */
public class SmoothGauss implements MonteCarloModelDouble {

   int s;
   double invsqrts, a, sum;

   // Constructor.
   public SmoothGauss(int s) {
      this.s = s;
      invsqrts = 1.0 / Math.sqrt(s);
      a = -NormalDist.cdf01(1.0 / Math.sqrt(2.0));
   }

   // Sum of s N(0.1) random variables.
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += NormalDist.inverseF01(stream.nextDouble());
      }
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      return (NormalDist.cdf01(1.0 + sum * invsqrts) + a);
   }

   // Descriptor.
   @Override
   public String toString() {
      return "SmoothGauss: Smooth and bounded function of s Gaussians";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "SmoothGauss";
   }
}
