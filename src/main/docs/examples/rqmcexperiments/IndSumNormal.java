package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.probdist.NormalDist;

/**
 * This function is an indicator that a sum of standard normal random variables 
 * exceeds a given threshold, re-centered so that the mean is zero:
 * @f[
 *   f(u_1,\dots,u_s) = - \Phi(-\tau) + \II\left[s^{-1/2}\sum_{j=1}^s \Phi^{-1}(u_j)\ge\tau\right]
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with @f$\tau=1@f$.
 * It was used in @cite vLEC23a,  @cite vLEC24a, and @cite vLEC26a.
 * This function is discontinuous and has infinite variation in the sense of Hardy and Krause 
 * for $\dimu\ge2$.  The discontinuities are not axis-parallel so they are not RQMC-friendly. 
 *
 */
public class IndSumNormal implements MonteCarloModelDouble {

   int s;
   double invsqrts, a, sum;

   // Constructor.
   public IndSumNormal(int s) {
      this.s = s;
      invsqrts = 1.0 / Math.sqrt(s);
      a = -NormalDist.cdf01(-1.0);
   }

   // Generates and returns X, without IS.
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += NormalDist.inverseF01(stream.nextDouble());
      }
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      if (sum * invsqrts < 1)
         return a;
      else
         return (a + 1.0);
   }

   // Descriptor of this model.
   @Override
   public String toString() {
      return "IndSumNormal: Indicator of a sum of normals larger than a constant";
   }

   // Short descriptor (tag) for this model.
   @Override
   public String getTag() {
      return "IndSumNormal";
   }
}
