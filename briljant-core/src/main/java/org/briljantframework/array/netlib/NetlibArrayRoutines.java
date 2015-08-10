/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.array.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.base.BaseArrayRoutines;
import org.briljantframework.exceptions.NonConformantException;

/**
 * @author Isak Karlsson
 */
class NetlibArrayRoutines extends BaseArrayRoutines {

  private final static BLAS blas = BLAS.getInstance();

  @Override
  public double dot(DoubleArray a, DoubleArray b) {
    if (a.isContiguous() && b.isContiguous()) {
      Check.size(a, b);
      Check.argument(a.isVector() && b.isVector());
      int n = a.size();
      double[] aa = a.data();
      double[] ba = b.data();
      return blas.ddot(n, aa, a.getOffset(), b.stride(0), ba, b.getOffset(), b.stride(0));
    } else {
      return super.dot(a, b);
    }
  }

  @Override
  public double asum(DoubleArray a) {
    if (a.isContiguous()) {
      return blas.dasum(a.size(), a.data(), a.getOffset(), a.stride(0));
    } else {
      return super.asum(a);
    }
  }

  @Override
  public double norm2(DoubleArray a) {
    if (a.isContiguous()) {
      return blas.dnrm2(a.size(), a.data(), a.getOffset(), a.stride(0));
    } else {
      return super.norm2(a);
    }
  }

  @Override
  public int iamax(DoubleArray a) {
    if (a.isContiguous()) {
      return blas.idamax(a.size(), a.data(), a.getOffset(), a.stride(0));
    } else {
      return super.iamax(a);
    }
  }

  @Override
  public void scal(double alpha, DoubleArray x) {
    if (alpha != 1 && x.isContiguous()) {
      blas.dscal(x.size(), alpha, x.data(), x.getOffset(), x.stride(0));
    } else {
      super.scal(alpha, x);
    }
  }

  @Override
  public void axpy(double alpha, DoubleArray x, DoubleArray y) {
    if (alpha == 0) {
      return;
    }
    if (x.isContiguous() && y.isContiguous()) {
      Check.argument(x.isVector() && y.isVector());
      Check.size(x, y);
      blas.daxpy(x.size(), alpha, x.data(), x.getOffset(), x.stride(0),
                 y.data(), y.getOffset(), y.stride(0));
    } else {
      super.axpy(alpha, x, y);
    }
  }

  @Override
  public void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a) {
    if (!x.isView() && !y.isView() && !a.isView()) {
      Check.argument(x.isVector() && y.isVector());
      Check.size(x.size(), a.rows());
      Check.size(y.size(), a.columns());

      int m = a.rows();
      int n = a.columns();

      double[] ax = x.data();
      double[] ay = y.data();
      double[] aa = a.data();
      blas.dger(m, n, alpha, ax, 1, ay, 1, aa, Math.max(1, m));
    } else {
      super.ger(alpha, x, y, a);
    }
  }

  @Override
  public void gemv(Op transA, double alpha, DoubleArray a,
                   DoubleArray x, double beta, DoubleArray y) {
    Check.argument(a.isMatrix());
    Check.argument(x.isVector());
    Check.argument(y.isVector());

    int am = a.rows();
    int an = a.columns();
    String ta = "n";
    if (transA.isTrue()) {
      am = a.columns();
      an = a.rows();
    }
    if (!x.isVector() || x.size() != am) {
      throw new NonConformantException(am, an, x.rows(), x.columns());
    }
    if (!x.isVector() || !y.isVector() || y.size() != x.size()) {
      throw new IllegalArgumentException("...");
    }

    int lda = a.rows();
    double[] aa = a.data();
    double[] xa = x.data();
    double[] ya = y.data();
    blas.dgemv(ta, am, an, alpha, aa, lda, xa, 1, beta, ya, 1);
    if (y.isView()) {
      y.assign(ya);
    }
  }

  @Override
  public void gemm(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b,
                   double beta, DoubleArray c) {
    Check.argument(a.dims() == 2, "'a' has %s dims", a.dims());
    Check.argument(b.dims() == 2, "'b' has %s dims", a.dims());
    Check.argument(c.dims() == 2, "'c' has %s dims", a.dims());

    if (a.stride(0) == 1 && b.stride(0) == 1 && c.stride(0) == 1 &&
        a.stride(1) >= a.size(1) && b.stride(1) >= b.size(1) && c.stride(1) >= c.size(1)) {
      if (b.size(transB == Op.KEEP ? 0 : 1) != a.size(transA == Op.KEEP ? 1 : 0)) {
        boolean ta = transA == Op.KEEP;
        boolean tb = transB == Op.KEEP;
        throw new NonConformantException(String.format(
            "a has size (%d, %d), b has size(%d, %d)",
            a.size(ta ? 0 : 1), a.size(ta ? 1 : 0), b.size(tb ? 0 : 1), b.size(tb ? 1 : 0)
        ));
      }
      int m = a.size(transA == Op.KEEP ? 0 : 1);
      int n = b.size(transB == Op.KEEP ? 1 : 0);
      int k = a.size(transA == Op.KEEP ? 1 : 0);

      if (m != c.size(0) || n != c.size(1)) {
        throw new NonConformantException(String.format(
            "a has size (%d,%d), b has size (%d,%d), c has size (%d, %d)",
            m, k, k, n, c.size(0), c.size(1)));
      }

      double[] ca = c.data();
      blas.dgemm(
          transA.asString(),
          transB.asString(),
          m,
          n,
          k,
          alpha,
          a.data(),
          a.getOffset(),
          Math.max(1, a.stride(1)),
          b.data(),
          b.getOffset(),
          Math.max(1, b.stride(1)),
          beta,
          ca,
          c.getOffset(),
          Math.max(1, c.stride(1))
      );
//
      if (c.isView()) {
        c.assign(ca);
      }
    } else {
      super.gemm(transA, transB, alpha, a, b, beta, c);
    }


  }

  public boolean isMatrixTransposedView(DoubleArray x) {
    return x.stride(x.dims() - 1) > x.stride(0);
  }

}
