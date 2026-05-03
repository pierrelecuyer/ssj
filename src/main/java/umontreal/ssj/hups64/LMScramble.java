/*
 * Class:        LMScramble
 * Description:  performs a left matrix scramble only
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001--2018  Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @author       
 * @since
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package umontreal.ssj.hups64;

import umontreal.ssj.rng.RandomStream;
import java.lang.IllegalArgumentException;

/**
 * This class implements a @ref umontreal.ssj.hups.PointSetRandomization that
 * performs a left matrix scrambling. The point set
 * must be a @ref umontreal.ssj.hups.DigitalNet.
 *
 * <div class="SSJ-bigskip"></div><div class="SSJ-bigskip"></div>
 */
public class LMScramble extends RandomShift {

   /**
    * Empty constructor.
    */
   public LMScramble() {
   }

   /**
    * Sets internal variable `stream` to the given `stream`.
    * 
    * @param stream stream to use in the randomization
    */
   public LMScramble(RandomStream stream) {
      super(stream);
   }

   /**
    * This method calls only
    * umontreal.ssj.hups.DigitalNet.leftMatrixScramble(RandomStream).  If `p` is not
    * a @ref umontreal.ssj.hups.DigitalNet, an IllegalArgumentException is thrown.
    * 
    * @param p Point set to randomize
    */
   public void randomize(PointSet p) {
      if (p instanceof DigitalNet) {
         ((DigitalNet) p).leftMatrixScramble(stream);
      }
      else if (p instanceof ContainerPointSet) {
         randomize(((ContainerPointSet) p).getOriginalPointSet()); 
      } else {
         throw new IllegalArgumentException("LMScramble" + " can only randomize a DigitalNet");
      }
   }

   /**
    * Returns a descriptor of this object.
    */
   public String toString() {
      return "Left matrix scramble";
   }

}