/*
 * Class:        RandomLatticeParams
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

/**
 * This type of randomization applies to a lattice rule of rank 1.
 * It permit one to randomize the generating vector and perhaps also the 
 * number of points of the lattice rule. 
 * The randomization itself is always performed by `randomize(PointSet)`,
 * where `PointSet` must be a `Rank1Lattice`.
 * 
 * ...  to be continued and implemented.  
 *
 * <div class="SSJ-bigskip"></div><div class="SSJ-bigskip"></div>
 */
public class RandomLatticeParams implements PointSetRandomization {

   protected RandomStream stream;

   /**
    * Empty constructor: No stream is passed here for the randomization; one must
    * be passed later by #setStream. **Pierre:** Not sure if we should keep this;
    * we always need a stream!
    */
   public RandomLatticeParams() {
   }

   /**
    * Constructor that sets the internal @ref umontreal.ssj.rng.RandomStream to
    * `stream`.
    * 
    * @param stream stream to use in the randomization
    */
   public RandomLatticeParams(RandomStream stream) {
      this.stream = stream;
   }

   /**
    * This method calls
    * {@link umontreal.ssj.hups.PointSet.addRandomShift(RandomStream)
    * addRandomShift(stream)}.
    * 
    * @param p Point set to randomize
    */
   public void randomize(PointSet p) {
      p.addRandomShift(stream);
   }

   /**
    * Sets the internal @ref umontreal.ssj.rng.RandomStream to `stream`.
    * 
    * @param stream stream to use in the randomization
    */
   public void setStream(RandomStream stream) {
      this.stream = stream;
   }

   /**
    * Returns the internal @ref umontreal.ssj.rng.RandomStream.
    * 
    * @return stream used in the randomization
    */
   public RandomStream getStream() {
      return stream;
   }

   /**
    * Returns a descriptor of this object.
    */
   public String toString() {
      return "Random shift (digital if applied to a digital net)";
   }

}