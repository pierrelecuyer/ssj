package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements an oscillatory function from Genz @cite iGEN87a, defined as 
 * @f[
 *   f(u_1,\dots,u_s) = \cos\left(\sum_{j=1}^s a_j u_j \right)
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with @f$a_j = j/s@f$.
 * This function is smooth, but not one-periodic.
 */
public class Oscillatory implements MonteCarloModelDouble {

   int s;
   double sum;
   double[] a;
   double mean = 1.0;

   // Constructor.
   public Oscillatory(int s) {
      this.s = s;
      a = new double[s];
      for (int j = 0; j < s; j++) {
         a[j] = (double) (j + 1) / (double) s;
         mean *= 2.0 * Math.sin(a[j] / 2.0) / a[j];
      } 
      mean *= Math.cos((s + 1.0) / 4.0);
   }

   // Generates the values and compute the sum.   
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += a[j] * stream.nextDouble();
      }
   }

   // Return the value X of the function. Here, E[X] is not zero. 
   public double getPerformance() {
      return Math.cos(sum) - mean;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "Oscillatory function from Genz";
   }

   // Short descriptor (tag) for this model.
   @Override
   public String getTag() {
      return "Oscillatory";
   }
}