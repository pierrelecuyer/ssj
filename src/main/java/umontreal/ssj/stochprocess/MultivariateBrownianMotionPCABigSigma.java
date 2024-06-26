/*
 * Class:        MultivariateBrownianMotionPCABigSigma
 * Description:
 * Environment:  Java
 * Software:     SSJ
 * Copyright (C) 2001  Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @author       Jean-Sébastien Parent
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
import cern.colt.matrix.impl.*;
import cern.colt.matrix.linalg.*;
import cern.colt.matrix.doublealgo.*;

/**
 * A multivariate Brownian motion process @f$\{\mathbf{X}(t) : t \geq0 \}@f$
 * sampled entirely using the *principal component* decomposition (PCA). In this
 * class, a matrix which equals the Kronecker products of two matrices C
 * and @f$\Sigma@f$ must be computed. `C` is the usual one dimensional Brownian
 * motion covariance matrix and @f$\Sigma@f$ is the matrix that defined the
 * covariance between the one dimensionnal Brownian motion. This Kronecker
 * products is time and memory consuming as it might creates an enormous matrix,
 * matrix that is called BigSigma here. The class
 * 
 * @ref MultivariateBrownianMotionPCA provides faster results.
 *
 *      <div class="SSJ-bigskip"></div><div class="SSJ-bigskip"></div>
 */
public class MultivariateBrownianMotionPCABigSigma extends MultivariateBrownianMotion {

   protected DoubleMatrix2D BigSigma; // Matrice de covariance du vecteur des observ.
   // BigSigma [i*c+k][j*c+l] = Cov[X_k(t_{i+1}),X_l(t_{j+1})].
   protected DoubleMatrix2D decompPCABigSigma;
   protected DoubleMatrix2D C; // C[i,j] = \min(t_{i+1},t_{j+1}).
   protected DoubleMatrix2D A; // C = AA' (PCA decomposition).
   protected double[] z, zz; // vector of c*d standard normals.
   protected boolean decompPCA;

   /**
    * Constructs a new `MultivariateBrownianMotionPCABigSigma` with
    * parameters @f$\boldsymbol{\mu}= \mathtt{mu}@f$,
    * 
    * @f$\boldsymbol{\sigma}= \mathtt{sigma}@f$, correlation matrix
    * @f$\mathbf{R}_z = \mathtt{corrZ}@f$, and initial value
    * @f$\mathbf{X}(t_0) = \mathtt{x0}@f$. The normal variates @f$Z_j@f$ in are
    *                    generated by inversion using the
    * @ref umontreal.ssj.rng.RandomStream `stream`.
    */
   public MultivariateBrownianMotionPCABigSigma(int c, double[] x0, double[] mu, double[] sigma, double[][] corrZ,
         RandomStream stream) {
// we cannot call the constructor of MBM class because it sets B to the cholesky decomposition of of CorrZ.
      this.gen = new NormalGen(stream, new NormalDist());
      setParams(c, x0, mu, sigma, corrZ);
   }

   /**
    * Constructs a new `MultivariateBrownianMotionPCABigSigma` with
    * parameters @f$\boldsymbol{\mu}= \mathtt{mu}@f$,
    * 
    * @f$\boldsymbol{\sigma}= \mathtt{sigma}@f$, correlation matrix
    * @f$\mathbf{R}_z = \mathtt{corrZ}@f$, and initial value
    * @f$\mathbf{X}(t_0) = \mathtt{x0}@f$. The normal variates @f$Z_j@f$ in are
    *                    generated by `gen`.
    */
   public MultivariateBrownianMotionPCABigSigma(int c, double[] x0, double[] mu, double[] sigma, double[][] corrZ,
         NormalGen gen) {
// we cannot call the constructor of MBM class because it sets B to the cholesky decomposition of CorrZ.
      this.gen = gen;
      setParams(c, x0, mu, sigma, corrZ);
   }

   public void setParams(int c, double[] x0, double[] mu, double[] sigma, double[][] corrZ) {
      decompPCA = false;
      super.setParams(c, x0, mu, sigma, corrZ);
   }

   protected DoubleMatrix2D decompPCA(DoubleMatrix2D BigSigma) {
      // L'objet SingularValueDecomposition permet de recuperer la matrice
      // des valeurs propres en ordre decroissant et celle des vecteurs propres de
      // sigma (pour une matrice symetrique et definie-positive seulement).
      SingularValueDecomposition sv = new SingularValueDecomposition(BigSigma);
      DoubleMatrix2D D = sv.getS();
      // Calculer la racine carree des valeurs propres
      for (int i = 0; i < D.rows(); ++i)
         D.setQuick(i, i, Math.sqrt(D.getQuick(i, i)));
      DoubleMatrix2D P = sv.getV();
      return P.zMult(D, null);
//         return D;
   }

   protected void init() {
      super.init();
      int g;
      BigSigma = new DenseDoubleMatrix2D(c * d, c * d);
      for (int i = 0; i < d; ++i) {
         for (int j = 0; j < d; ++j) {
            for (int k = 0; k < c; ++k) {
               for (int l = 0; l < c; ++l) {
                  g = (i <= j ? i + 1 : j + 1);
                  if (k == l)
                     BigSigma.setQuick(i * c + k, j * c + l, g * sigma[k] * sigma[l] * (t[i + 1] - t[i]));
                  else
                     BigSigma.setQuick(i * c + k, j * c + l, g * covZ.getQuick(k, l) * (t[i + 1] - t[i]));
               }
            }
         }
      }
      decompPCABigSigma = decompPCA(BigSigma);
      decompPCA = true;
   }

   public double[] generatePath() {
      double sum = 0.0;
      int i, j, k;
      double[] z = new double[c * d];
      if (!decompPCA) {
         init();
      } // if the PCA decomposition has not been changed since the initialisation of the
        // parameters, it must be done before the path is generated
      for (i = 0; i < c * d; i++)
         z[i] = gen.nextDouble();

      for (j = 0; j < d; j++)
         for (i = 0; i < c; i++) {
            sum = 0.0;
            for (k = 0; k < c * d; k++) {
               sum += decompPCABigSigma.getQuick(c * j + i, k) * z[k];
            }
            path[c * (j + 1) + i] = sum + mu[i] * (t[j + 1] - t[0]) + x0[i];
         }

      observationIndex = d;
      return path;
   }

   public double[] generatePath(double[] uniform01) {
      double sum = 0.0;
      int i, j, k;
      if (!decompPCA) {
         init();
      } // if the PCA decomposition has not been changed since the initialisation of the
        // parameters, it must be done before the path is generated
      for (j = 0; j < d; j++)
         for (i = 0; i < c; i++) {
            sum = 0.0;
            for (k = 0; k < c * d; k++) {
               sum += decompPCABigSigma.getQuick(c * j + i, k) * uniform01[k];
            }
            path[c * (j + 1) + i] = sum + mu[i] * (t[j + 1] - t[0]) + x0[i];
         }

      observationIndex = d;
      return path;
   }

}