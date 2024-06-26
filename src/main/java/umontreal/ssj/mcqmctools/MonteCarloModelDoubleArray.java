package umontreal.ssj.mcqmctools;

import umontreal.ssj.rng.*;

/**
 * Similar to @ref MonteCarloModelDouble except that the returned performance is
 * an array of real numbers. The dimension of that array must be returned by
 * `getPerformanceDim()`.
 */

public interface MonteCarloModelDoubleArray {

   // Optional
   // public void simulate ();

   /**
    * Simulates the model for one run.
    */
   public void simulate(RandomStream stream);

   /**
    * Recovers and returns the realization of the vector of performance measures.
    */
   public double[] getPerformance();

   /**
    * Returns the dimension of the array of performance measures.
    */
   public int getPerformanceDim();

   /**
    * Returns a short description of the model and its parameters.
    */
   public String toString();

   /** 
    * Returns a short model name (tag) to be used in reports.
    */
   public String getTag();
   
}
