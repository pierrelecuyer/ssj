package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements the two-dimensional function
 * @f[
 *   f(u_1,u_2) = u_1 u_2 - 1/4.
 * @f]
 * for @f$\bm u = (u_1,u_2) \in [0,1]^2@f$.
 */
public class XY implements MonteCarloModelDouble {

   double prod;

   // Constructor.
   public XY() {}

   // Computes the product and center its mean to zero.
   public void simulate(RandomStream stream) {
      prod = stream.nextDouble() * stream.nextDouble() - 0.25;
   }

   // Returns X.
   public double getPerformance() {
      return prod;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "Product test function, u_1 u_2 - 1/4";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "Product";
   }
}