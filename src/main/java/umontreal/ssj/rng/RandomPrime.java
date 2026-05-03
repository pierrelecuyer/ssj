/*
 * Class:        RandomPermutation
 * Description:  Provides methods to randomly shuffle arrays or lists
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Universite de Montreal
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
package umontreal.ssj.rng;

import java.util.RandomAccess;

/**
 * To generate random prime numbers in some range.
 * The function `randomPrime24` generates random integers until a prime number is found.  
 * Primality is checked by checking for prime factors taken from a list.
 * For larger numbers, it uses the Rabin-Miller probabilistic primality test
 * (not yet implemented).
 *
 * <div class="SSJ-bigskip"></div>
 */
public class RandomPrime {

   /**
    * Returns `true` iff `p` is a prime number less than @f$2^{24}@f$.
    * This function checks if @f$p < 2^{24}@f$ and if it has no prime factor 
    * less than @f$p < 2^{12}@f$.
    */
   public static boolean isPrime24 (int p) {
      if (p  >= (1 << 24)) return false;
      for (int i = 0; (i < 564); i++) {
         int pi = primes4096[i];
         if (pi * pi > p) return true;
         if (p % pi == 0) return false;
      }
      return true;
   }

   /**
    * Returns a random prime number uniformly distributed over the interval @f$[p_1, p_2]@f$.  
    * The interval boundaries must satisfy @f$1 <= p_1 <= p_2 <= 2^{24}@f$ and the interval
    * must contain at least one prime number, otherwise there will be an infinite loop!
    */
   public static int randomPrime24 (int p1, int p2, RandomStream stream) {
      if (p2  > (1 << 24)) 
         throw new IllegalArgumentException("p2 cannot exceed 2^{24}");
      // We could test if the interval contains at least one prime number, but this 
      // may take significant time...
      int p;
      do {
         p = stream.nextInt(p1, p2);
         if (isPrime24(p)) return p;
      }
      while (true);
   }
   
   
   protected static final int primes4096[] = 
      { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101 };

}