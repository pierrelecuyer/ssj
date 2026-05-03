/*
 * Class:        NestedUniformScrambling2
 * Description:  performs Owen's nested uniform scrambling
 * Environment:  Java
 * Software:     SSJ
 * Copyright (C) 2016  David Munger, Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @author
 * @since

 * SSJ is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License (GPL) as published by the
 * Free Software Foundation, either version 3 of the License, or
 * any later version.

 * SSJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * A copy of the GNU General Public License is available at
   <a href="http://www.gnu.org/licenses">GPL licence site</a>.
 */
package rqmcexperiments;

import umontreal.ssj.hups.*;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.util.Num;

import java.lang.IllegalArgumentException;

/**
 * This @ref PointSetRandomization class provides the nested uniform scrambling
 * (NUS) randomization proposed by Owen (\cite vOWE95a, \cite vOWE03a) for
 * digital nets. Since the scrambled points are all stored explicitly, it can
 * only be applied to a @ref CachedPointSet that contains a @ref
 * DigitalNetBase2. The proper way to use it is to construct a @ref
 * CachedPointSet `p` that contains the digital net, and call @ref
 * NestedUniformScrambling.randomize(p) to randomize. The actual implementation
 * is in @ref DigitalNetBase2.nestedUniformScramble().
 *
 * Note that calling CachedPointSet.randomize() with an instance of
 * NestedUniformScrambling as its arguments will not work, because
 * CachedPointSet.randomize() calls randomize() on its reference point set (the
 * digital net) whereas NUS should modify the cached values instead.
 */
public class NestedUniformScrambling2 implements PointSetRandomization {

   private RandomStream stream;
   private int numBits;
   double normFactor = 1.0 / ((double) (1L << (31)));
   double EpsilonHalf = 1.0 / Num.TWOEXP[55]; // 2^{-55},  defined in PointSet

   public NestedUniformScrambling2(RandomStream stream, int numBits) {
      this.stream = stream;
      this.numBits = numBits;
      normFactor = 1.0 / ((double) (1L << (numBits)));
   }

   public RandomStream getStream() {
      return stream;
   }

   public void setStream(RandomStream stream) {
      this.stream = stream;
   }

   public void randomize(PointSet p) {
      CachedPointSet cp = (CachedPointSet) p;
      int[][] int_output = new int[cp.getNumPoints()][cp.getDimension()];
      ((DigitalNetBase2) cp.getParentPointSet()).nestedUniformScramble(stream, int_output, numBits); // Apply NUS with integers
      double[][] output = cp.getArray();         // Then transform to real numbers
      for (int j = 0; j < cp.getDimension(); ++j)
         for (int i = 0; i < cp.getNumPoints(); i++)
            output[i][j] = int_output[i][j] * normFactor + EpsilonHalf;
   }
   
}
