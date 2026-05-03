package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements an exponential function from Genz @cite iGEN87a, defined as 
 * @f[
 *   f(u_1,\dots,u_s) = \exp\left(a \sum_{j=1}^s u_j\right)
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with @f$a = 2/3@f$.
 */
public class Exponential implements MonteCarloModelDouble {

   int s;
   double sum;
   double a = 2.0 / 3.0;

   // Constructor.
   public Exponential(int s) {
      this.s = s;
   }

   // Generates and returns X, without IS.
   public void simulate(RandomStream stream) {
      sum = 0.0;
      for (int j = 0; j < s; j++) {
         sum += stream.nextDouble();
      }
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      return Math.exp(a * sum);
   }

   // Descriptor of this model.
   @Override
   public String toString() {
      return "Exponential: exponential of a sum of uniforms over [0,a]";
   }

   // Short descriptor (tag) for this model.
   @Override
   public String getTag() {
      return "Exponential";
   }
}