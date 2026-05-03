package rqmcexperiments;

import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;

/**
 * Implements a function taken from Genz @cite vMOR95a, defined as 
 * @f[
 *   f(u_1,\dots,u_s) = -1 + (s-1/2)^{-s} \prod_{j=1}^s (s-u_j)
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$.
 * It is centered to its mean is zero.
 * This function is smooth and almost additive.
 * It was used in @cite vLEC23a,  @cite vLEC24a, and @cite vLEC26a.
 */
public class MC2 implements MonteCarloModelDouble {

   int s;
   double ds, ds2, prod;

   // Constructor.
   public MC2(int s) {
      this.s = s;
      ds = (double) s;
      ds2 = 1.0 / (ds - 0.5);
   }

   // Generates and returns X, without IS.
   public void simulate(RandomStream stream) {
      prod = 1.0;
      for (int j = 0; j < s; j++) {
         double u = stream.nextDouble();
         prod *= (ds - u) * ds2;
      }
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      return prod - 1.0;
   }

   // Descriptor of this model.
   @Override
   public String toString() {
      return "Morokoff and Caflisch function";
   }

   // Short descriptor (tag) for this model.
   @Override
   public String getTag() {
      return "MC2";
   }
}
