package org.briljantframework.math.transform;

import static org.briljantframework.matrix.Doubles.zeros;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Complexes;
import org.briljantframework.matrix.DoubleMatrix;

/*
 * Free FFT and convolution (Java)
 * 
 * Copyright (c) 2014 Project Nayuki http://www.nayuki.io/page/free-small-fft-in-multiple-languages
 * 
 * (MIT License) Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the following conditions: - The above
 * copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. - The Software is provided "as is", without warranty of any kind,
 * express or implied, including but not limited to the warranties of merchantability, fitness for a
 * particular purpose and noninfringement. In no event shall the authors or copyright holders be
 * liable for any claim, damages or other liability, whether in an action of contract, tort or
 * otherwise, arising from, out of or in connection with the Software or the use or other dealings
 * in the Software.
 */

/**
 * @author Isak Karlsson
 */
public final class FastFourierTransform {
  private FastFourierTransform() {}

  public static ComplexMatrix fft(ComplexMatrix a) {
    int n = a.size();
    ComplexMatrix copy = a.copy();
    // n is a power of 2?
    if ((n & (n - 1)) == 0) {
      transformRadix2(copy);
    } else {
      transformBluestein(copy);
    }
    return copy;
  }

  private static void transformBluestein(ComplexMatrix a) {
    // Find a power-of-2 convolution length m such that m >= n * 2 + 1
    int n = a.size();
    if (n >= 0x20000000) { // n >= 536870912
      throw new IllegalArgumentException("");
    }
    int m = Integer.highestOneBit(n * 2 + 1) << 1;

    // Trigonometric tables
    DoubleMatrix cosTable = zeros(n);
    DoubleMatrix sinTable = zeros(n);
    for (int i = 0; i < n; i++) {
      int j = (int) ((long) i * i % (n * 2));
      cosTable.set(i, Math.cos(Math.PI * j / n));
      sinTable.set(i, Math.sin(Math.PI * j / n));
    }

    ComplexMatrix an = Complexes.zeros(m);
    ComplexMatrix bn = Complexes.zeros(m);

    bn.set(0, new Complex(cosTable.get(0), sinTable.get(0)));
    for (int i = 0; i < n; i++) {
      double cos = cosTable.get(i);
      double sin = sinTable.get(i);
      Complex av = a.get(i);
      double real = av.real() * cos + av.imag() * sin;
      double imag = -av.real() * sin + av.imag() * cos;
      an.set(i, new Complex(real, imag));

      int j = i + 1;
      if (j < n) {
        Complex bcVal = Complex.valueOf(cosTable.get(j), sinTable.get(j));
        bn.set(j, bcVal);
        bn.set(m - j, bcVal);
      }
    }

    // Convolution
    ComplexMatrix cc = convolve(an, bn);
    for (int i = 0; i < n; i++) {
      double cos = cosTable.get(i);
      double sin = sinTable.get(i);

      Complex cv = cc.get(i);
      double real = cv.real() * cos + cv.imag() * sin;
      double imag = -cv.real() * sin + cv.imag() * cos;
      a.set(i, new Complex(real, imag));
    }
  }

  /*
   * Computes the circular convolution of the given complex vectors. Each vector's length must be
   * the same.
   */
  private static ComplexMatrix convolve(ComplexMatrix x, ComplexMatrix y) {
    int n = x.size();
    ComplexMatrix xt = fft(x);
    ComplexMatrix yt = fft(y);

    for (int i = 0; i < n; i++) {
      xt.set(i, xt.get(i).multiply(yt.get(i)));
    }

    for (int i = 0; i < n; i++) {
      final Complex complex = xt.get(i);
      xt.set(i, new Complex(complex.imag(), complex.real()));
    }

    xt = fft(xt); // inverse transform
    // TODO: implement divi for complex matrices
    // reverse back
    for (int i = 0; i < n; i++) {
      Complex c = xt.get(i);
      xt.set(i, Complex.valueOf(c.imag() / n, c.real() / n));
    }
    return xt;
  }

  private static void transformRadix2(ComplexMatrix a) {
    final int n = a.size();
    int levels = (int) Math.floor(Math.log(n) / Math.log(2));
    if (1 << levels != n) {
      throw new IllegalArgumentException();
    }

    DoubleMatrix cosTable = zeros(n / 2);
    DoubleMatrix sinTable = zeros(n / 2);
    final double v = 2 * Math.PI;
    for (int i = 0; i < n / 2; i++) {
      cosTable.set(i, Math.cos(v * i / n));
      sinTable.set(i, Math.sin(v * i / n));
    }

    // Bit-reversed addressing permutation (i.e. even addresses in the first half and odd in the
    // second half)
    for (int i = 0; i < n; i++) {
      int j = Integer.reverse(i) >>> (32 - levels);
      if (j > i) {
        a.swap(j, i);
      }
    }

    // Cooley-Tukey decimation-in-time radix-2 FFT
    for (int size = 2; size <= n; size *= 2) {
      int halfSize = size / 2;
      int tableStep = n / size;
      for (int i = 0; i < n; i += size) {
        for (int j = i, k = 0; j < i + halfSize; j++, k += tableStep) {
          Complex hjVal = a.get(j + halfSize);
          Complex jVal = a.get(j);
          double cos = cosTable.get(k);
          double sin = sinTable.get(k);
          double tpre = hjVal.real() * cos + hjVal.imag() * sin;
          double tpim = -hjVal.real() * sin + hjVal.imag() * cos;
          a.set(j + halfSize, new Complex(jVal.real() - tpre, jVal.imag() - tpim));
          a.set(j, new Complex(jVal.real() + tpre, jVal.imag() + tpim));
        }
      }
      if (size == n) {
        break;
      }
    }
  }
}
