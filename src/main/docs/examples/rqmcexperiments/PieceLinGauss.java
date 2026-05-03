package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements an piecewise-linear and continuous function in $s$ Gaussian inputs:
 * @f[
 *   f(u_1,\dots,u_s) = \max\left(s^{-1/2}\sum_{j=1}^s \Phi^{-1}(u_j)-\tau,0\right) - \varphi(\tau)+\tau\Phi(-\tau)
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with @f$\tau = 1@f$.
 * This function has infinite variation in the sense of Hardy and Krause.
 * It was used in @cite vLEC23a. 
 */
public class PieceLinGauss implements MonteCarloModelDouble {

   int s;
   double invsqrts, a, sum;

   // Constructor.
   public PieceLinGauss(int s) {
      this.s = s;
      invsqrts = 1.0 / Math.sqrt(s);
      a = NormalDist.cdf01(-1.0) - NormalDist.density01(1.0);
   }

   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += NormalDist.inverseF01(stream.nextDouble());
      }
   }

   public double getPerformance() {
      double prod = sum * invsqrts - 1.0;
      if (prod < 0.0)
         return a;
      else
         return (a + prod);
   }

   // Descriptor.
   @Override
   public String toString() {
      return "PieceLinGauss: Piecewise linear function of s Gaussians";
   }

   // Short descriptor (tag).
   public String getTag() {
      return "PieceLinGauss";
   }
}