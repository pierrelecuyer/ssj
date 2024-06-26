/*
 * Class:        MultivariateBrownianMotionBridge
 * Description:  
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @authors      Jean-Sébastien Parent & Clément Teule
 * @since        2008

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
package umontreal.ssj.stochprocess;

import umontreal.ssj.rng.*;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.*;

/**
 * A multivariate Brownian motion process @f$\{\mathbf{X}(t) : t \geq0 \}@f$
 * sampled via *bridge sampling*. We use a Cholesky decomposition of the
 * relevant covariance matrix to generate the next @f$c@f$-dimensional vector at
 * each step of the bridge sampling algorithm. For this, we construct the same
 * matrix @f$\boldsymbol{\Sigma}@f$ as in
 * 
 * @ref MultivariateBrownianMotion and we compute its Cholesky decomposition
 * @f$\boldsymbol{\Sigma}= B B^{\mathsf{t}}@f$.
 *
 *                         <div class="SSJ-bigskip"></div><div class=
 *                         "SSJ-bigskip"></div>
 */
public class MultivariateBrownianMotionBridge extends MultivariateBrownianMotion {

   protected double[] z, covZCholDecompz; // vector of c*d standard normals.
   protected int bridgeCounter = -1; // Before 1st observ

   // For precomputations for B Bridge
   protected double[] wMuDt, wSqrtDt;
   protected int[] wIndexList, ptIndex;

   /**
    * Constructs a new `MultivariateBrownianMotionBridge` with
    * parameters @f$\boldsymbol{\mu}= @f$<tt>mu</tt>,
    * 
    * @f$\boldsymbol{\sigma}= @f$<tt>sigma</tt>, correlation matrix
    * @f$\mathbf{R}_z = @f$<tt>corrZ</tt>, and initial value
    * @f$\mathbf{X}(t_0) = @f$<tt>x0</tt>. The normal variates @f$Z_j@f$ in are
    *                    generated by inversion using the
    * @ref umontreal.ssj.rng.RandomStream `stream`.
    */
   public MultivariateBrownianMotionBridge(int c, double[] x0, double[] mu, double[] sigma, double[][] corrZ,
         RandomStream stream) {
      setParams(c, x0, mu, sigma, corrZ);
      this.gen = new NormalGen(stream, new NormalDist());
      z = new double[c];
      covZCholDecompz = new double[c];
   }

   /**
    * Constructs a new `MultivariateBrownianMotionBridge` with
    * parameters @f$\boldsymbol{\mu}= @f$<tt>mu</tt>,
    * 
    * @f$\boldsymbol{\sigma}= @f$<tt>sigma</tt>, correlation matrix
    * @f$\mathbf{R}_z = @f$<tt>corrZ</tt>, and initial value
    * @f$\mathbf{X}(t_0) = @f$<tt>x0</tt>. The normal variates @f$Z_j@f$ in are
    *                    generated by `gen`.
    */
   public MultivariateBrownianMotionBridge(int c, double[] x0, double[] mu, double[] sigma, double[][] corrZ,
         NormalGen gen) {
      setParams(c, x0, mu, sigma, corrZ);
      this.gen = gen;
      z = new double[c];
      covZCholDecompz = new double[c];
   }

   public double[] generatePath() {
      // Generation of Brownian bridge process
      if (!covZiSCholDecomp) { // the cholesky factorisation must be done to use the matrix covZCholDecomp
         initCovZCholDecomp();
      }
      int oldIndexL, oldIndexR, newIndex, i, j;

      for (i = 0; i < c; i++)
         z[i] = gen.nextDouble();

      computeAZ(covZCholDecomp, z, covZCholDecompz);
      for (i = 0; i < c; i++) {
         path[c * d + i] = x0[i] + mu[i] * (t[d] - t[0]) + wSqrtDt[0] * covZCholDecompz[i];

      }
      for (j = 0; j < 3 * (d - 1); j += 3) {

         for (i = 0; i < c; i++)
            z[i] = gen.nextDouble();

         computeAZ(covZCholDecomp, z, covZCholDecompz);
         oldIndexL = wIndexList[j];
         newIndex = wIndexList[j + 1];
         oldIndexR = wIndexList[j + 2];
         for (i = 0; i < c; i++) {
            path[c * newIndex + i] = path[c * oldIndexL + i]
                  + (path[c * oldIndexR + i] - path[c * oldIndexL + i]) * wMuDt[newIndex]
                  + wSqrtDt[newIndex] * covZCholDecompz[i];
//             System.out.println("path[0] : " + path[0]);
//             System.out.println("path[" + i + "] : " + path[c * newIndex + i]);
         }
      }
      // resetStartProcess();

      observationIndex = observationCounter = c * d;
      return path;
   }

   public void resetStartProcess() {
      observationIndex = 0;
      observationCounter = 0;
      bridgeCounter = -1;
   }

   public double[] nextObservationVector() {
      throw new UnsupportedOperationException("nextObservationVector is not implemented ");
   }

   public void nextObservationVector(double[] obs) {
      throw new UnsupportedOperationException("nextObservationVector is not implemented ");
   }

   protected void init() {
      super.init();

      /* For Brownian Bridge */

      // Quantities for Brownian Bridge process
      wMuDt = new double[d + 1];
      wSqrtDt = new double[d + 1];
      wIndexList = new int[3 * (d)];
      ptIndex = new int[d + 1];

      int indexCounter = 0;
      int newIndex, oldLeft, oldRight;
      int i, j, k, powOfTwo;

      for (i = 0; i < c; i++)
         path[i] = x0[i];

      ptIndex[0] = 0;
      ptIndex[1] = d;

      wMuDt[0] = 0.0; // The end point of the Wiener process
                      // w/ Brownian bridge has expectation = 0
      wSqrtDt[0] = Math.sqrt(t[d] - t[0]);
      // = sigma*sqrt(Dt) of end point

      for (powOfTwo = 1; powOfTwo <= d / 2; powOfTwo *= 2) {
         /* Make room in the indexing array "ptIndex" */
         for (j = powOfTwo; j >= 1; j--) {
            ptIndex[2 * j] = ptIndex[j];
         }

         /* Insert new indices and Calculate constants */
         for (j = 1; j <= powOfTwo; j++) {
            oldLeft = 2 * j - 2;
            oldRight = 2 * j;
            newIndex = (int) (0.5 * (ptIndex[oldLeft] + ptIndex[oldRight]));

            wMuDt[newIndex] = (t[newIndex] - t[ptIndex[oldLeft]]) / (t[ptIndex[oldRight]] - t[ptIndex[oldLeft]]);

            wSqrtDt[newIndex] = Math.sqrt((t[newIndex] - t[ptIndex[oldLeft]]) * (t[ptIndex[oldRight]] - t[newIndex])
                  / (t[ptIndex[oldRight]] - t[ptIndex[oldLeft]]));

            ptIndex[oldLeft + 1] = newIndex;
            wIndexList[indexCounter] = ptIndex[oldLeft];
            wIndexList[indexCounter + 1] = newIndex;
            wIndexList[indexCounter + 2] = ptIndex[oldRight];

            indexCounter += 3;
         }
      }
      /* Check if there are holes remaining and fill them */
      for (k = 1; k < d; k++) {
         if (ptIndex[k - 1] + 1 < ptIndex[k]) {
            // there is a hole between (k-1) and k.
            wMuDt[ptIndex[k - 1] + 1] = (t[ptIndex[k - 1] + 1] - t[ptIndex[k - 1]])
                  / (t[ptIndex[k]] - t[ptIndex[k - 1]]);
            wSqrtDt[ptIndex[k - 1] + 1] = Math.sqrt((t[ptIndex[k - 1] + 1] - t[ptIndex[k - 1]])
                  * (t[ptIndex[k]] - t[ptIndex[k - 1] + 1]) / (t[ptIndex[k]] - t[ptIndex[k - 1]]));
            wIndexList[indexCounter] = ptIndex[k] - 2;
            wIndexList[indexCounter + 1] = ptIndex[k] - 1;
            wIndexList[indexCounter + 2] = ptIndex[k];
            indexCounter += 3;
         }
      }
   }

   // Computes A * z and returns it in vector Az.
   private void computeAZ(DoubleMatrix2D A, double z[], double Az[]) {
      for (int i = 0; i < c; i++) {
         double sum = 0.0;
         for (int k = 0; k < c; k++)
            sum += A.getQuick(i, k) * z[k];
         Az[i] = sum;
      }
   }

}