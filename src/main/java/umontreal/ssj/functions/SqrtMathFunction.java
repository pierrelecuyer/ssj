/*
 * Class:        SqrtMathFunction
 * Description:  function computing the square root of another function
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @author       Éric Buist
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
package umontreal.ssj.functions;

/**
 * Represents a function computing the square root of another function
 * 
 * @f$f(x)@f$.
 *
 *             <div class="SSJ-bigskip"></div>
 */
public class SqrtMathFunction implements MathFunction {
   private MathFunction func;

   /**
    * Computes and returns the square root of the function `func`.
    * 
    * @param func the function to compute square root for.
    */
   public SqrtMathFunction(MathFunction func) {
      super();
      if (func == null)
         throw new NullPointerException();
      this.func = func;
   }

   /**
    * Returns the function associated with this object.
    * 
    * @return the associated function.
    */
   public MathFunction getFunction() {
      return func;
   }

   public double evaluate(double x) {
      return Math.sqrt(func.evaluate(x));
   }
}