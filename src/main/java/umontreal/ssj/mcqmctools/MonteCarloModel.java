package umontreal.ssj.mcqmctools;

import umontreal.ssj.rng.*;

/**
 * An interface for a simple simulation model for which Monte Carlo (MC) or RQMC
 * experiments are to be performed. It generalizes @ref MonteCarloModelDouble.
 * This interface allows the output (performance) from the model to be of
 * arbitrary type `E`. It could be a scalar, an array, etc.
 */

public interface MonteCarloModel<E> {

   /**
    * Simulates the model for one run, using the given `stream`.
    */
   public void simulate(RandomStream stream);

   /**
    * Recovers and returns the realization of the performance measure, of type E.
    */
   public E getPerformance();

   /**
    * Returns a description of the model and its parameters.
    * This method has a default implementation that returns an empty string.
    */
   public String toString();
   
   /** 
    * Returns a short model name (usually a single word) to be used in reports.
    * This method has a default implementation that returns an empty string.
    */
   default public String getTag() {
      return "";
   }
   
}
