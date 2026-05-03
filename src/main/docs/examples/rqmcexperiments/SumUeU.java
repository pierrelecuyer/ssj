package rqmcexperiments;

import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.mcqmctools.MonteCarloModelDouble;


/**
 * Implements a function taken from Owen @cite vOWE23a, defined as 
 * @f[
 *   f(u_1,\dots,u_s) = -s + \sum_{j=1}^s u_j\exp(u_j)
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$.
 * This is a sum of one-dimensional functions, centered to have mean zero.
 * It was used in @cite vLEC23a,  @cite vLEC24a, and @cite vLEC26a.
 */
public class SumUeU implements MonteCarloModelDouble {

   int s;
   double sum, u;

   // Constructor.
   public SumUeU(int s) {
      this.s = s;
   }

   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         u = stream.nextDouble();
         sum += u * Math.exp(u);
      }
   }

   public double getPerformance() {
      return sum - s;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "SumUeU: Sum of U_j exp(U_j)";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "SumUeU";
   }
}
