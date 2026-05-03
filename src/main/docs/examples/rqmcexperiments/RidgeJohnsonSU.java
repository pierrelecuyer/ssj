package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.probdist.JohnsonSUDist;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.probdist.NormalDist;

/**
 * Implements the function
 * @f[
 *   f(u_1,\dots,u_s) = = -\eta + F^{-1}(\Phi(s^{-1/2}\sum_{j=1}^s \Phi^{-1}(u_j))
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, 
 * where @f$F@f$ is the CDF of the Johnson's SU distribution @cite tJOH49a,
 * with parameters @f$\gamma=\delta=\lambda=1@f$ and @f$\xi=0@f$, and $\eta$ is
 * the mean of that distribution, which has skewness @f$-5.66@f$ and excess
 * kurtosis @f$96.8@f$ (for any @f$s@f$) making it heavy tailed.
 * This function was used in @cite vLEC23a. 
 */
public class RidgeJohnsonSU implements MonteCarloModelDouble {

   int s;
   double xi = 0.0, lambda = 1.0, gamma = 1.0, delta = 1.0;
   double sum, val;
   double invsqrts, mean;

   public RidgeJohnsonSU(int s) {
      this.s = s;
      invsqrts = 1.0 / Math.sqrt((double) s);
      mean = xi - lambda * Math.exp(0.5 / (delta * delta)) * Math.sinh(gamma / delta);
   }

   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += NormalDist.inverseF01(stream.nextDouble());
      }
      sum *= invsqrts;
      val = JohnsonSUDist.inverseF(gamma, delta, xi, lambda, NormalDist.cdf01(sum)) - mean;
   }

   public double getPerformance() {
      return val;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "RidgeJohnsonSU: Ridge for the sum of s SumJohnsonSU";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "RidgeJohnsonSU";
   }
}
