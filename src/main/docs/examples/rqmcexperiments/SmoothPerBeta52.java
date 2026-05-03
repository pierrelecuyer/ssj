package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements a product of smooth asymmetric Beta(4,1) density functions
 * @f[
 *   f(u_1,\dots,u_s) =  -1 + \prod_{j=1}^s (1 + \omega_j (30 u_j^4 (1-u_j) - 1))
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, with weights 
 * @f$\omega_j = j^{-w}@f$, where @f$w@f$ is the `expw` given in the constructor.
 */
public class SmoothPerBeta52 implements MonteCarloModelDouble {

   int s;
   double u, prod;
   double[] omega;

   // Constructor.
   public SmoothPerBeta52(int s, double expw) {
      this.s = s;
      omega = new double[s];
      //  omega[0] = 0.1;
      for (int j = 0; j < s; j++)
         omega[j] = Math.pow((double)(j+1), -expw);
   }

   public void simulate(RandomStream stream) {
      prod = 1.0;
      for (int j = 0; j < s; j++) {
         u = stream.nextDouble();
         // prod *= (1.0 + omega[j] * (20.0 * u * u * u * (1.0-u) - 1.0));        
         prod *= (1.0 + omega[j] * (30.0 * u * u * u * u * (1.0-u) - 1.0));        
      }
      prod -= 1.0;
   }

   // Generates and returns X, without IS.
   public double getPerformance() {
      return prod;
   }

   // Descriptor.
   @Override
   public String toString() {
      return "SmoothPerBeta(4,2)";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "SmoothPerBeta(4,2)";
   }
}
