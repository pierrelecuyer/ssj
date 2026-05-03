package rqmcexperiments;

import umontreal.ssj.mcqmctools.MonteCarloModelDouble;
import umontreal.ssj.rng.RandomStream;

/**
 * Implements the smooth one-periodic function
 * @f[
 *   f(u_1,\dots,u_s) = -1 + \prod_{j=1}^s [1 + 30 \omega_j B_4(u_j)] 
 *                    = -1 + \prod_{j=1}^s 30 \omega_j u_j^2 (1-u_j)^2
 * @f]
 * for @f$\bm u = (u_1,\dots,u_s) \in [0,1]^s@f$, where 
 * @f$B_4(x) = x^{2}(1-x)^{2} -1/30@f$ is the fourth Bernoulli polynomial
 * and @f$\omega_j = j^{-w}@f$, and @f$w@f$ is the `expw` given in the constructor.
 * It is the Worst-case function for @f$\cP_{2\alpha}@f$ with @f$\alpha=4@f$. 
 * This function was used in @cite vLEC26a.
 */
public class SmoothPerB4 implements MonteCarloModelDouble {

   int s;
   double u, prod;
   double[] omega;

   // Constructor.
   public SmoothPerB4(int s, double expw) {
      this.s = s;
      this.omega = new double[s];
      for (int j = 0; j < s; j++)
         this.omega[j] = Math.pow((double)(j+1), -expw);
   }

   public void simulate(RandomStream stream) {
      prod = 1.0;
      for (int j = 0; j < s; j++) {
         u = stream.nextDouble();
         prod *= (1.0 + omega[j] * (30.0 * u * u * (1.0-u) * (1.0-u) - 1.0));        
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
      return "SmoothPerB4: Worst-case function for P_{2\\alpha} with alpha=4";
   }

   // Short descriptor (tag).
   @Override
   public String getTag() {
      return "SmoothPerB4";
   }
}
