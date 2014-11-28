/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.natives.Blas.*;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

import org.briljantframework.matrix.math.Javablas;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;


/**
 * Common Basic Linear Algebra Subroutines.
 * <p>
 * Created by Isak Karlsson on 21/06/14.
 */
public class Matrices {

  /**
   * The constant RANDOM.
   */
  public static final Random RANDOM = new Random();
  /**
   * The constant LOG_2.
   */
  public static final double LOG_2 = Math.log(2);
  private static final Pattern ROW_SEPARATOR = Pattern.compile(";");
  private static final Pattern VALUE_SEPARATOR = Pattern.compile(",");

  /**
   * Parse matrix.
   *
   * @param str the str
   * @return the matrix
   */
  public static Matrix parseMatrix(String str) {
    return parseMatrix(DenseMatrix::new, str);
  }

  /**
   * Parse matrix.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param str the str
   * @return the out
   */
  public static <Out extends Matrix> Out parseMatrix(Matrix.New<Out> f, String str) {
    Preconditions.checkArgument(str != null && str.length() > 0);

    String[] rows = ROW_SEPARATOR.split(str);
    if (rows.length < 1) {
      throw new NumberFormatException("Illegally formatted Matrix");
    }

    Out matrix = null;
    for (int i = 0; i < rows.length; i++) {
      String[] values = VALUE_SEPARATOR.split(rows[i]);
      if (i == 0) {
        matrix = f.newMatrix(rows.length, values.length);
      }

      for (int j = 0; j < values.length; j++) {
        matrix.put(i, j, Double.parseDouble(values[j].trim()));
      }
    }

    return matrix;
  }

  /**
   * Zero dense matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the dense matrix
   */
  public static Matrix zero(int rows, int cols) {
    return zeros(DenseMatrix::new, rows, cols);
  }

  /**
   * Zeros out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param rows the rows
   * @param cols the cols
   * @return the out
   */
  public static <Out extends Matrix> Out zeros(Matrix.New<Out> f, int rows, int cols) {
    return f.newMatrix(rows, cols);
  }

  /**
   * Ones dense matrix.
   *
   * @param size the size
   * @return the dense matrix
   */
  public static Matrix ones(int size) {
    return ones(size, size);
  }

  /**
   * Ones matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static Matrix ones(int rows, int cols) {
    return ones(DenseMatrix::new, rows, cols);
  }

  /**
   * Ones out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param rows the rows
   * @param cols the cols
   * @return the out
   */
  public static <Out extends Matrix> Out ones(Matrix.New<Out> f, int rows, int cols) {
    Out out = f.newMatrix(rows, cols);
    map(out, ignore -> 1, out);
    return out;
  }

  /**
   * Apply <code>operation</code> to every element in the {@code ArrayBackend} output in
   * <p>
   * 
   * <pre>
   * Tensors.apply(vector, Math::sqrt, output);
   * </pre>
   *
   * @param in input tensorlike
   * @param operator operator to apply
   * @param out the out
   */
  public static void map(Matrix in, DoubleUnaryOperator operator, Matrix out) {
    for (int i = 0; i < in.size(); i++) {
      out.put(i, operator.applyAsDouble(in.get(i)));
    }
  }

  /**
   * N dense matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @param n the n
   * @return the dense matrix
   */
  public static Matrix n(int rows, int cols, double n) {
    double[] values = new double[rows * cols];
    Arrays.fill(values, n);
    return DenseMatrix.fromColumnOrder(rows, cols, values);
  }

  /**
   * Eye diagonal.
   *
   * @param size the size
   * @return the diagonal
   */
  public static Diagonal eye(int size) {
    double[] diagonal = new double[size];
    for (int i = 0; i < size; i++) {
      diagonal[i] = 1;
    }
    return Diagonal.of(size, size, diagonal);
  }


  /**
   * Eye diagonal.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the diagonal
   */
  public static Diagonal eye(int rows, int cols) {
    double[] diagonal = new double[rows * cols];
    for (int i = 0; i < diagonal.length; i++) {
      diagonal[i] = 1;
    }
    return Diagonal.of(rows, cols, diagonal);
  }

  /**
   * Sqrt matrix.
   *
   * @param matrix the matrix
   * @return the matrix
   */
  public static Matrix sqrt(Matrix matrix) {
    return sqrt(DenseMatrix::new, matrix);
  }

  /**
   * Sqrt out.
   *
   * @param <Out> the type parameter
   * @param out the out
   * @param in the in
   * @return out out
   */
  public static <Out extends Matrix> Out sqrt(Matrix.New<Out> out, Matrix in) {
    return map(out, in, Math::sqrt);
  }

  /**
   * <pre>
   * Tensors.apply(vector, Math::sqrt, DenseVector::new)
   * </pre>
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param in the in
   * @param operator the operator
   * @return out out
   */
  public static <Out extends Matrix> Out map(Matrix.New<Out> f, Matrix in,
      DoubleUnaryOperator operator) {
    Out out = f.newMatrix(in.getShape());
    map(in, operator, out);
    return out;
  }

  /**
   * Apply matrix.
   *
   * @param in the in
   * @param operator the operator
   * @return the matrix
   */
  public static Matrix map(Matrix in, DoubleUnaryOperator operator) {
    return map(DenseMatrix::new, in, operator);
  }

  /**
   * Log matrix.
   *
   * @param matrix the matrix
   * @return the matrix
   */
  public static Matrix log(Matrix matrix) {
    return log(DenseMatrix::new, matrix);
  }

  /**
   * Log out.
   *
   * @param <Out> the type parameter
   * @param out the out
   * @param in the in
   * @return out out
   */
  public static <Out extends Matrix> Out log(Matrix.New<Out> out, Matrix in) {
    return map(out, in, Math::log);
  }

  /**
   * Log 2.
   *
   * @param matrix the matrix
   * @return the matrix
   */
  public static Matrix log2(Matrix matrix) {
    return log2(DenseMatrix::new, matrix);
  }

  /**
   * Log 2.
   *
   * @param <Out> the type parameter
   * @param out the out
   * @param in the in
   * @return out out
   */
  public static <Out extends Matrix> Out log2(Matrix.New<Out> out, Matrix in) {
    return map(out, in, x -> Math.log(x) / LOG_2);
  }

  /**
   * Mdmul matrix.
   *
   * @param m the m
   * @param diagonal the diagonal
   * @return the matrix
   */
  public static Matrix mdmul(Matrix m, Diagonal diagonal) {
    return mdmul(DenseMatrix::new, m, diagonal);
  }

  /**
   * Return a new array with the result
   *
   * @param <T> the type parameter
   * @param f the f
   * @param m a square matrix with x.rows = d.size
   * @param d a diagonal matrix
   * @return a new array with the same dimensions as x
   */
  public static <T extends Matrix> T mdmul(Matrix.New<T> f, Matrix m, Diagonal d) {
    Shape shape = Shape.of(m.rows(), d.columns());
    double[] empty = shape.getArrayOfShape();
    mdmuli(m, d, empty);
    return f.newMatrix(shape, empty);
  }

  /**
   * Multiplying a square matrix X with a symmetric diagonal matrix (i.e. a vector of diagonal
   * entries) d storing the result in Y.
   * <p>
   * Since the result is a new square matrix, inplace multiplication can be performed
   * <p>
   * 
   * <pre>
   * Matrix x = Matrix.of(2, 2, 1, 1, 1, 1);
   * Vector d = Vector.row(2, 2);
   * Blas.multiplyByDiagonal(x, d, x);
   * </pre>
   * <p>
   * 
   * <pre>
   * Y &lt; -Xd
   * </pre>
   *
   * @param x a square matrix with x.headers = d.size
   * @param d a diagonal matrix
   * @param y a square matrix with x.shape = out.shape
   */
  public static void mdmuli(Matrix x, Diagonal d, double[] y) {
    if (x.columns() != d.rows()) {
      throw new NonConformantException(x, d);
    }
    int rows = x.rows(), columns = d.columns();
    for (int column = 0; column < columns; column++) {
      if (column < x.columns()) {
        for (int row = 0; row < rows; row++) {
          double xv = x.get(row, column);
          double dv = d.get(column);
          y[column * rows + row] = xv * dv;
        }
      } else {
        break;
      }
    }
  }

  /**
   * Dmmul matrix.
   *
   * @param d the d
   * @param m the m
   * @return the matrix
   */
  public static Matrix dmmul(Diagonal d, Matrix m) {
    return dmmul(DenseMatrix::new, d, m);
  }

  /**
   * Multiply by diagonal.
   *
   * @param <T> the type parameter
   * @param f the f
   * @param d a diagonal matrix
   * @param m a square matrix with x.headers = d.size
   * @return the result
   */
  public static <T extends Matrix> T dmmul(Matrix.New<T> f, Diagonal d, Matrix m) {
    Shape shape = Shape.of(d.rows(), m.columns());
    double[] array = shape.getArrayOfShape();
    dmmuli(d, m, array);
    return f.newMatrix(shape, array);
  }

  /**
   * Multiplying a square symmetric diagonal matrix (i.e. a vector of diagonal entries) d and X,
   * storing the result in Y
   * <p>
   * 
   * <pre>
   * Y &lt; -dX
   * </pre>
   *
   * @param d a diagonal matrix
   * @param x a square matrix with x.rows = d.size
   * @param y a square matrix with x.shape = y.shape
   */
  public static void dmmuli(Diagonal d, Matrix x, double[] y) {
    if (d.columns() != x.rows()) {
      throw new NonConformantException(d, x);
    }
    int rows = d.rows(), columns = x.columns();
    for (int row = 0; row < rows; row++) {
      if (row < x.rows()) {
        for (int column = 0; column < columns; column++) {
          y[column * rows + row] = x.get(row, column) * d.get(row);
        }
      } else {
        break;
      }
    }
  }

  /**
   * Fill void.
   *
   * @param matrix the matrix
   * @param value the value
   */
  public static void fill(Matrix matrix, double value) {
    for (int i = 0; i < matrix.columns(); i++) {
      for (int j = 0; j < matrix.rows(); j++) {
        matrix.put(j, i, value);
      }
    }
  }

  /**
   * Find elements in {@code matrix} for which the corresponding value in {@code booleanMatrix} is
   * true. Return a new Vector.
   *
   * @param <Out> the type parameter
   * @param factory the factory
   * @param matrix the matrix
   * @param booleanMatrix the boolean matrix
   * @return out out
   */
  public static <Out extends Matrix> Out find(Matrix.New<Out> factory, Matrix matrix,
      BooleanMatrix booleanMatrix) {
    checkArgument(matrix.hasCompatibleShape(booleanMatrix.getShape()));
    DoubleArrayList list = new DoubleArrayList();
    for (int i = 0; i < matrix.rows(); i++) {
      for (int j = 0; j < matrix.columns(); j++) {
        if (booleanMatrix.has(i, j)) {
          list.add(matrix.get(i, j));
        }
      }
    }
    return factory.newVector(list.size(), list.toArray());
  }

  /**
   * Linspace matrix.
   *
   * @param limit the limit
   * @param n the n
   * @param base the base
   * @return the matrix
   */
  public static Matrix linspace(int limit, int n, int base) {
    return linspace(DenseMatrix::new, limit, n, base);
  }

  /**
   * Linspace out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param limit the limit
   * @param n the n
   * @param base the base
   * @return the out
   */
  public static <Out extends Matrix> Out linspace(Matrix.New<Out> f, int limit, int n, int base) {
    double[] tensor = new double[n];
    double step = ((double) limit - base) / (n - 1);

    double value = base;
    for (int index = 0; index < n; index++) {
      tensor[index] = value;
      value += step;
    }

    return f.newVector(n, tensor);
  }

  /**
   * Zeros matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static Matrix zeros(int rows, int cols) {
    return zeros(DenseMatrix::new, rows, cols);
  }

  /**
   * Reshape out.
   *
   * @param in the in
   * @param rows the rows
   * @param cols the cols
   * @return the out
   */
  public static Matrix reshape(Matrix in, int rows, int cols) {
    return reshape(DenseMatrix::new, in, rows, cols);
  }

  /**
   * Reshape out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param in the in
   * @param rows the rows
   * @param cols the cols
   * @return the out
   */
  public static <Out extends Matrix> Out reshape(Matrix.Copy<Out> f, Matrix in, int rows, int cols) {
    if (!in.hasCompatibleShape(rows, cols)) {
      throw new MismatchException("reshape", String.format(
          "can't reshape %s tensor into %s tensor", in.getShape(), Shape.of(rows, cols)));
    }
    return f.copyMatrix(Shape.of(rows, cols), in);
  }

  /**
   * Std matrix.
   *
   * @param matrix the matrix
   * @param axis the axis
   * @return the matrix
   */
  public static Matrix std(Matrix matrix, Axis axis) {
    return std(DenseMatrix::new, matrix, axis);
  }

  /**
   * Std out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static <Out extends Matrix> Out std(Matrix.New<Out> f, Matrix matrix, Axis axis) {
    Matrix mean = mean(matrix, axis);
    int columns = matrix.columns();
    double[] sigmas = new double[columns];

    for (int j = 0; j < columns; j++) {
      double std = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        double residual = matrix.get(i, j) - mean.get(j);
        std += residual * residual;
      }
      sigmas[j] = Math.sqrt(std / (matrix.rows() - 1));
    }
    return f.newMatrix(1, columns, sigmas);
  }

  /**
   * Mean matrix.
   *
   * @param matrixLike the matrix like
   * @param axis the axis
   * @return the matrix
   */
  public static Matrix mean(Matrix matrixLike, Axis axis) {
    return mean(DenseMatrix::new, matrixLike, axis);
  }

  /**
   * Mean out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param matrix the matrix
   * @param axis the axis
   * @return the out
   */
  public static <Out extends Matrix> Out mean(Matrix.New<Out> f, Matrix matrix, Axis axis) {
    int columns = matrix.columns();
    double[] means = new double[matrix.columns()];
    for (int j = 0; j < matrix.columns(); j++) {
      double mean = 0.0;
      for (int i = 0; i < matrix.rows(); i++) {
        mean += matrix.get(i, j);
      }
      means[j] = mean / matrix.rows();
    }

    return f.newMatrix(1, columns, means);
  }

  /**
   * Randn matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static Matrix randn(int rows, int cols) {
    return randn(DenseMatrix::new, rows, cols);
  }

  /**
   * Randn out.
   *
   * @param <Out> the type parameter
   * @param factory the factory
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static <Out extends Matrix> Out randn(Matrix.New<Out> factory, int rows, int cols) {
    Shape shape = Shape.of(rows, cols);
    double[] array = shape.getArrayOfShape();
    randn(array);
    return factory.newMatrix(shape, array);
  }

  /**
   * Randn void.
   *
   * @param array a array to fill
   */
  public static void randn(double[] array) {
    for (int i = 0; i < array.length; i++) {
      array[i] = RANDOM.nextGaussian();
    }
  }

  /**
   * Rand matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the matrix
   */
  public static Matrix rand(int rows, int cols) {
    return rand(DenseMatrix::new, rows, cols);
  }

  /**
   * Rand out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param rows the rows
   * @param cols the cols
   * @return out out
   */
  public static <Out extends Matrix> Out rand(Matrix.New<Out> f, int rows, int cols) {
    Shape shape = Shape.of(rows, cols);
    double[] array = shape.getArrayOfShape();
    rand(array);
    return f.newMatrix(shape, array);
  }

  /**
   * Fill with uniformly random numbers
   *
   * @param array the array
   */
  public static void rand(double[] array) {
    for (int i = 0; i < array.length; i++) {
      array[i] = RANDOM.nextGaussian();
    }
  }

  /**
   * Pow out.
   *
   * @param in the in
   * @param power the power
   * @return the out
   */
  public static Matrix pow(Matrix in, double power) {
    return pow(DenseMatrix::new, in, power);
  }

  /**
   * Pow out.
   *
   * @param <Out> the type parameter
   * @param factory the factory
   * @param in the in
   * @param power the power
   * @return out out
   */
  public static <Out extends Matrix> Out pow(Matrix.New<Out> factory, Matrix in, double power) {
    switch ((int) power) {
      case 2:
        return map(factory, in, x -> x * x);
      case 3:
        return map(factory, in, x -> x * x * x);
      case 4:
        return map(factory, in, x -> x * x * x * x);
      default:
        return map(factory, in, x -> Math.pow(x, power));
    }
  }

  /**
   * Log 10.
   *
   * @param <Out> the type parameter
   * @param out the out
   * @param in the in
   * @return out out
   */
  public static <Out extends Matrix> Out log10(Matrix.New<Out> out, Matrix in) {
    return map(out, in, Math::log10);
  }

  /**
   * Sign out.
   *
   * @param <Out> the type parameter
   * @param out the out
   * @param in the in
   * @return out out
   */
  public static <Out extends Matrix> Out sign(Matrix.New<Out> out, Matrix in) {
    return map(out, in, Math::signum);
  }

  /**
   * Matrix multiplication between matrix a and b scaling with alpha and beta, i.e. (aA)(bB)
   *
   * @param a a rectangular array
   * @param transA transpose a
   * @param b a rectangular array
   * @param transB transpose b
   * @param out output array
   */
  public static void mmuli(Matrix a, Transpose transA, Matrix b, Transpose transB, double[] out) {
    mmuli(a, transA, 1, b, transB, 1, out);
  }

  /**
   * This function multiplies A * B and multiplies the resulting matrix by alpha. It then multiplies
   * matrix C by beta. It stores the sum of these two products in matrix C.
   * <p>
   * Thus, it calculates either
   * <p>
   * C <- alpha*A*B + beta*C
   * <p>
   * or
   * <p>
   * C <- alpha*B*A + beta*C
   * <p>
   * with optional use of transposed forms of A, B, or both.
   * <p>
   * For example, reusing an existing matrix for storing the intermediate value of a computation
   * 
   * <pre>
   *      Matrix a = Matrix.of(2, 3,
   *          1, 2, 3,
   *          3, 2, 1
   *      );
   * 
   *      Matrix b = Matrix.of(3, 2,
   *          1, 2,
   *          3, 4,
   *          5, 6
   *      );
   * 
   *      Matrix out = new Matrix(2, 2);
   * 
   *      for(...) {
   *          Matrices.multiplyInplace(a, Transpose.No, 1, b, Transpose.No, 0, out);
   *      }
   * </pre>
   *
   * @param a a rectangular array
   * @param transA transpose a
   * @param alpha a multiplication factor
   * @param b a rectangular array
   * @param transB transpose b
   * @param beta a scaling factor for out
   * @param out a rectangular array
   */
  public static void mmuli(Matrix a, Transpose transA, double alpha, Matrix b, Transpose transB,
      double beta, double[] out) {
    int am = a.rows(), an = a.columns(), bm = b.rows(), bn = b.columns();
    if (transA == Transpose.YES) {
      int tmp = am;
      am = an;
      an = tmp;
    }

    if (transB == Transpose.YES) {
      int tmp = bm;
      bm = bn;
      bn = tmp;
    }

    if (an != bm) {
      throw new NonConformantException("a", am, an, "b", bm, bn);
    }

    cblas_dgemm(CblasColMajor, transA.getCblasTranspose(), transB.getCblasTranspose(),
    // M N K
        am, bn, bm, alpha, a.asDoubleArray(),
        // LDA LDB LDC
        a.rows(), b.asDoubleArray(), b.rows(), beta, out, am);
  }

  /**
   * Mmul matrix.
   *
   * @param a the a
   * @param transA the trans a
   * @param b the b
   * @param transB the trans b
   * @return the matrix
   */
  public static Matrix mmul(Matrix a, Transpose transA, Matrix b, Transpose transB) {
    return mmul(DenseMatrix::new, a, transA, b, transB);
  }

  /**
   * Multiply out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param a the a
   * @param transA the trans a
   * @param b the b
   * @param transB the trans b
   * @return the out
   */
  public static <Out extends Matrix> Out mmul(Matrix.New<Out> f, Matrix a, Transpose transA,
      Matrix b, Transpose transB) {
    return mmul(f, a, transA, 1.0, b, transB, 1.0);
  }

  /**
   * Multiply out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param a the a
   * @param transA the trans a
   * @param alpha the alpha
   * @param b the b
   * @param transB the trans b
   * @param beta the beta
   * @return the out
   */
  public static <Out extends Matrix> Out mmul(Matrix.New<Out> f, Matrix a, Transpose transA,
      double alpha, Matrix b, Transpose transB, double beta) {
    int am = a.rows(), an = a.columns(), bm = b.rows(), bn = b.columns();
    if (transA == Transpose.YES) {
      int tmp = am;
      am = an;
      an = tmp;
    }

    if (transB == Transpose.YES) {
      int tmp = bm;
      bm = bn;
      bn = tmp;
    }

    if (an != bm) {
      throw new NonConformantException("a", am, an, "b", bm, bn);
    }
    Shape shape = Shape.of(am, bn);
    double[] out = shape.getArrayOfShape();
    mmuli(a, transA, alpha, b, transB, beta, out);
    return f.newMatrix(shape, out);
  }

  /**
   * Mmul matrix.
   *
   * @param a the a
   * @param transA the trans a
   * @param alpha the alpha
   * @param b the b
   * @param transB the trans b
   * @param beta the beta
   * @return the matrix
   */
  public static Matrix mmul(Matrix a, Transpose transA, double alpha, Matrix b, Transpose transB,
      double beta) {
    return mmul(DenseMatrix::new, a, transA, alpha, b, transB, beta);
  }

  /**
   * Mmul matrix.
   *
   * @param a the a
   * @param alpha the alpha
   * @param b the b
   * @param beta the beta
   * @return the matrix
   */
  public static Matrix mmul(Matrix a, double alpha, Matrix b, double beta) {
    return mmul(DenseMatrix::new, a, alpha, b, beta);
  }

  /**
   * Multiply out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param a the a
   * @param alpha the alpha
   * @param b the b
   * @param beta the beta
   * @return the out
   */
  public static <Out extends Matrix> Out mmul(Matrix.New<Out> f, Matrix a, double alpha, Matrix b,
      double beta) {
    Shape shape = Shape.of(a.rows(), b.columns());
    double[] out = shape.getArrayOfShape();
    mmuli(a, alpha, b, beta, out);
    return f.newMatrix(shape, out);
  }

  /**
   * Matrix multiplication between matrix a and b scaling with alpha and beta, i.e. (aA)(bB)
   *
   * @param a a rectangular array
   * @param alpha the alpha
   * @param b a rectangular array
   * @param beta the beta
   * @param out store the result in out
   */
  public static void mmuli(Matrix a, double alpha, Matrix b, double beta, double[] out) {
    if (Shape.of(a.rows(), b.columns()).size() != out.length) {
      throw new MismatchException("multiply", "output array size does not match");
    }

    cblas_dgemm(CblasColMajor, CblasNoTrans, CblasNoTrans,
    // M N K
        a.rows(), b.columns(), b.rows(), alpha, a.asDoubleArray(),
        // LDA LDB LDC
        a.rows(), b.asDoubleArray(), b.rows(), beta, out, a.rows());
  }

  /**
   * Abs void.
   *
   * @param in in
   * @param out out
   */
  public static void abs(Matrix in, Matrix out) {
    Javablas.abs(in.asDoubleArray(), out.asDoubleArray());
  }

  /**
   * Mmul matrix.
   *
   * @param a the a
   * @param b the b
   * @return the matrix
   */
  public static Matrix mmul(Matrix a, Matrix b) {
    return mmul(DenseMatrix::new, a, b);
  }

  /**
   * This is equivalent to a*b
   *
   * @param <T> the type parameter
   * @param f the f
   * @param a a matrix like
   * @param b a matrix like
   * @return a new matrix (a*b)
   * @throws NonConformantException if a.columns() != b.rows()
   */
  public static <T extends Matrix> T mmul(Matrix.New<T> f, Matrix a, Matrix b) {
    if (a.columns() != b.rows()) {
      throw new NonConformantException(a, b);
    }
    Shape shape = Shape.of(a.rows(), b.columns());
    double[] outArray = shape.getArrayOfShape();
    mmuli(a, b, outArray);
    return f.newMatrix(shape, outArray);
  }

  /**
   * A*B, with alpha=1, and beta=1 (i.e. regular matrix multiplication)
   *
   * @param a a rectangular array
   * @param b a rectangular array
   * @param out the out
   */
  public static void mmuli(Matrix a, Matrix b, double[] out) {
    mmuli(a, 1, b, 1, out);
  }

  /**
   * Mul matrix.
   *
   * @param in the in
   * @param scalar the scalar
   * @return matrix matrix
   */
  public static Matrix mul(Matrix in, double scalar) {
    return mul(DenseMatrix::new, in, scalar);
  }

  /**
   * Multiply t.
   *
   * @param <T> the type parameter
   * @param f the f
   * @param in the in
   * @param scalar the scalar
   * @return the t
   */
  public static <T extends Matrix> T mul(Matrix.New<T> f, Matrix in, double scalar) {
    double[] outArray = in.getShape().getArrayOfShape();
    Javablas.mul(in.asDoubleArray(), scalar, outArray);
    return f.newMatrix(in.getShape(), outArray);
  }

  /**
   * Mul matrix.
   *
   * @param a the a
   * @param b the b
   * @return the matrix
   */
  public static Matrix mul(Matrix a, Matrix b) {
    return mul(DenseMatrix::new, a, b);
  }

  /**
   * Elementwise multiply.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param a the a
   * @param b the b
   * @return t t
   */
  public static <T extends Matrix> T mul(Matrix.New<T> factory, Matrix a, Matrix b) {
    if (!a.hasEqualShape(b)) {
      throw new NonConformantException(a, b);
    }
    Shape shape = Shape.of(a.rows(), b.columns());
    double[] outArray = shape.getArrayOfShape();
    Javablas.mul(a.asDoubleArray(), 1.0, b.asDoubleArray(), 1.0, outArray);
    return factory.newMatrix(shape, outArray);
  }

  /**
   * Add matrix.
   *
   * @param a the a
   * @param b the b
   * @return the matrix
   */
  public static Matrix add(Matrix a, Matrix b) {
    return add(DenseMatrix::new, a, b);
  }

  /**
   * Add t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param a the a
   * @param b the b
   * @return t t
   */
  public static <T extends Matrix> T add(Matrix.New<T> factory, Matrix a, Matrix b) {
    if (!a.hasEqualShape(b)) {
      throw new NonConformantException(a, b);
    }
    Shape shape = Shape.of(a.rows(), b.columns());
    double[] outArray = shape.getArrayOfShape();
    Javablas.add(a.asDoubleArray(), 1.0, b.asDoubleArray(), 1.0, outArray);
    return factory.newMatrix(shape, outArray);
  }

  /**
   * Add matrix.
   *
   * @param a the a
   * @param b the b
   * @return the matrix
   */
  public static Matrix add(Matrix a, double b) {
    return add(DenseMatrix::new, a, b);
  }

  /**
   * Add t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param a the a
   * @param b the b
   * @return t t
   */
  public static <T extends Matrix> T add(Matrix.New<T> factory, Matrix a, double b) {
    double[] out = a.getShape().getArrayOfShape();
    Javablas.add(a.asDoubleArray(), b, out);
    return factory.newMatrix(a.getShape(), out);
  }

  /**
   * Sub matrix.
   *
   * @param a the a
   * @param b the b
   * @return the matrix
   */
  public static Matrix sub(Matrix a, double b) {
    return sub(DenseMatrix::new, a, b);
  }

  /**
   * Subtract t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param a the a
   * @param b the b
   * @return t t
   */
  public static <T extends Matrix> T sub(Matrix.New<T> factory, Matrix a, double b) {
    double[] out = a.getShape().getArrayOfShape();
    Javablas.sub(a.asDoubleArray(), b, out);
    return factory.newMatrix(a.getShape(), out);
  }

  /**
   * Sub matrix.
   *
   * @param b the b
   * @param a the a
   * @return the matrix
   */
  public static Matrix sub(double b, Matrix a) {
    return sub(DenseMatrix::new, b, a);
  }

  /**
   * Subtract t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param b the b
   * @param a the a
   * @return t t
   */
  public static <T extends Matrix> T sub(Matrix.New<T> factory, double b, Matrix a) {
    double[] out = a.getShape().getArrayOfShape();
    Javablas.sub(b, a.asDoubleArray(), out);
    return factory.newMatrix(a.getShape(), out);
  }

  /**
   * Div matrix.
   *
   * @param num the num
   * @param denom the denom
   * @return the matrix
   */
  public static Matrix div(Matrix num, Matrix denom) {
    return div(DenseMatrix::new, num, denom);
  }

  /**
   * Divide t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param numerator the numerator
   * @param denominator the denominator
   * @return t t
   */
  public static <T extends Matrix> T div(Matrix.New<T> factory, Matrix numerator, Matrix denominator) {
    if (!denominator.hasEqualShape(numerator)) {
      throw new NonConformantException(denominator, numerator);
    }
    double[] out = numerator.getShape().getArrayOfShape(); // denominator.rows(),
                                                           // numerator.columns());
    Javablas.div(denominator.asDoubleArray(), 1.0, numerator.asDoubleArray(), 1.0, out);
    return factory.newMatrix(numerator.getShape(), out);
  }

  /**
   * Div matrix.
   *
   * @param num the num
   * @param denom the denom
   * @return the matrix
   */
  public static Matrix div(double num, Matrix denom) {
    return div(DenseMatrix::new, num, denom);
  }

  /**
   * Divide t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param numerator the numerator
   * @param denominator the denominator
   * @return t t
   */
  public static <T extends Matrix> T div(Matrix.New<T> factory, double numerator, Matrix denominator) {
    double[] out = denominator.getShape().getArrayOfShape(); // denominator.rows(),
                                                             // numerator.columns());
    Javablas.div(numerator, denominator.asDoubleArray(), out);
    return factory.newMatrix(denominator.getShape(), out);
  }

  /**
   * Div matrix.
   *
   * @param num the num
   * @param denom the denom
   * @return the matrix
   */
  public static Matrix div(Matrix num, double denom) {
    return div(DenseMatrix::new, num, denom);
  }

  /**
   * Divide t.
   *
   * @param <T> the type parameter
   * @param factory the factory
   * @param numerator the the numerator
   * @param denominator the denominator
   * @return t t
   */
  public static <T extends Matrix> T div(Matrix.New<T> factory, Matrix numerator, double denominator) {
    if (denominator == 0.0) {
      throw new ArithmeticException("division by zero");
    }
    double[] out = numerator.getShape().getArrayOfShape();
    Javablas.div(numerator.asDoubleArray(), denominator, out);
    return factory.newMatrix(numerator.getShape(), out);
  }

  /**
   * Std double.
   *
   * @param vector the vector
   * @return the double
   */
  public static double std(MatrixLike vector) {
    return std(vector, mean(vector));
  }

  /**
   * Std double.
   *
   * @param vector the vector
   * @param mean the mean
   * @return the double
   */
  public static double std(MatrixLike vector, double mean) {
    double var = var(vector, mean);
    return Math.sqrt(var / (vector.size() - 1));
  }

  /**
   * Mean double.
   *
   * @param vector the vector
   * @return the double
   */
  public static double mean(MatrixLike vector) {
    double mean = 0;
    for (int i = 0; i < vector.size(); i++) {
      mean += vector.get(i);
    }

    return mean / vector.size();
  }

  /**
   * Var double.
   *
   * @param vector the vector
   * @param mean the mean
   * @return the double
   */
  public static double var(MatrixLike vector, double mean) {
    double var = 0;
    for (int i = 0; i < vector.size(); i++) {
      double residual = vector.get(i) - mean;
      var += residual * residual;
    }
    return var;
  }

  /**
   * Var double.
   *
   * @param vector the vector
   * @return the double
   */
  public static double var(MatrixLike vector) {
    return var(vector, mean(vector));
  }

  /**
   * Sort out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param in the in
   * @param comparator the comparator
   * @return the out
   */
  public static <Out extends Matrix> Out sort(Matrix.Copy<Out> f, MatrixLike in,
      Comparator<Double> comparator) {
    Out newTensor = f.copyMatrix(in);
    List<Double> doubles = Doubles.asList(newTensor.asDoubleArray());
    Collections.sort(doubles, comparator);
    return newTensor;
  }

  /**
   * Sort out.
   *
   * @param <Out> the type parameter
   * @param f the f
   * @param in the in
   * @return the out
   */
  public static <Out extends Matrix> Out sort(Matrix.Copy<Out> f, MatrixLike in) {
    Out newTensor = f.copyMatrix(in);
    Arrays.sort(newTensor.asDoubleArray());
    return newTensor;
  }

  /**
   * Sort in.
   *
   * @param <In> the type parameter
   * @param in the in
   * @return the in
   */
  public static <In extends Matrix> In sort(In in) {
    Arrays.sort(in.asDoubleArray());
    return in;
  }

  /**
   * Sort index.
   *
   * @param vector the vector
   * @return the int [ ]
   */
  public static int[] sortIndex(MatrixLike vector) {
    return sortIndex(vector, (o1, o2) -> Double.compare(vector.get(o1), vector.get(o2)));
  }

  /**
   * Sort index.
   *
   * @param vector the vector
   * @param comparator the comparator
   * @return the int [ ]
   */
  public static int[] sortIndex(MatrixLike vector, Comparator<Integer> comparator) {
    int[] indicies = new int[vector.size()];
    for (int i = 0; i < indicies.length; i++) {
      indicies[i] = i;
    }
    List<Integer> tempList = Ints.asList(indicies);
    Collections.sort(tempList, comparator);
    return indicies;
  }

  /**
   * Sort in.
   *
   * @param in the in
   * @param comparator the comparator
   */
  public static void sort(Matrix in, Comparator<Double> comparator) {
    List<Double> doubles = Doubles.asList(in.asDoubleArray());
    Collections.sort(doubles, comparator);
  }

  /**
   * Inner product, i.e. the dot product x * y
   *
   * @param x a vector
   * @param y a vector
   * @return the dot product
   */
  public static double dot(MatrixLike x, MatrixLike y) {
    return dot(x, 1, y, 1);
  }

  /**
   * Take the inner product of two vectors (m x 1) and (1 x m) scaling them by alpha and beta
   * respectively
   *
   * @param x a row vector
   * @param alpha scaling factor for a
   * @param y a column vector
   * @param beta scaling factor for y
   * @return the inner product
   */
  public static double dot(MatrixLike x, double alpha, MatrixLike y, double beta) {
    if (x.size() != y.size()) {
      throw new NonConformantException(x, y);
    }
    int size = y.size();
    double dot = 0;
    for (int i = 0; i < size; i++) {
      dot += (alpha * x.get(i)) * (beta * y.get(i));
    }
    return dot;
  }

  /**
   * Compute the sigmoid between a and b, i.e. 1/(1+e^(a'-b))
   *
   * @param a a vector
   * @param b a vector
   * @return the sigmoid
   */
  public static double sigmoid(MatrixLike a, MatrixLike b) {
    return 1.0 / (1 + Math.exp(dot(a, 1, b, -1)));
  }

  /**
   * Sub matrix.
   *
   * @param a the a
   * @param b the b
   * @return the matrix
   */
  public static Matrix sub(Matrix a, Matrix b) {
    return sub(DenseMatrix::new, a, b);
  }

  /**
   * Subtract t.
   *
   * @param <T> the type parameter
   * @param f the f
   * @param a the a
   * @param b the b
   * @return t t
   */
  public static <T extends Matrix> T sub(Matrix.New<T> f, Matrix a, Matrix b) {
    if (a.size() != b.size()) {
      throw new NonConformantException(a, b);
    }

    double[] out = new double[a.size()];
    sub(a, b, out);
    return f.newMatrix(a.getShape(), out);
  }

  /**
   * Subtract b from a storing the result in c. I.e. c <- a - b
   *
   * @param a a vector
   * @param b a vector
   * @param c a mutable vector
   */
  public static void sub(Matrix a, Matrix b, double[] c) {
    add(a, 1, b, -1, c);
  }

  /**
   * Add vector a and b, scaling by alpha and beta storing the result in c. I.e. c <- alpha*a +
   * beta*b
   * <p>
   * 
   * <pre>
   * Matrix a = Matrix.of(3, 1, 1, 2, 3);
   * Matrix b = Matrix.of(3, 1, 1, 2, 3);
   * Vectors.add(a, 1, b, 2, b);
   * 
   * // b = 3; 6; 9
   * </pre>
   * <p>
   * Hence, subtraction can be implemented as:
   * 
   * <pre>
   * Matrix a = Matrix.of(3, 1, 1, 2, 3);
   * Matrix b = Matrix.of(3, 1, 1, 2, 3);
   * Vectors.add(a, 1, b, -1, b);
   * 
   * // b = 0; 0; 0
   * </pre>
   *
   * @param a a vector
   * @param alpha scaling factor for a
   * @param b a vector
   * @param beta scaling factor for y
   * @param c store the result
   */
  public static void add(MatrixLike a, double alpha, MatrixLike b, double beta, double[] c) {
    for (int j = 0; j < c.length; j++) {
      c[j] = (alpha * a.get(j)) + (beta * b.get(j));
    }
  }

  /**
   * Sum double.
   *
   * @param matrix the matrix
   * @return the double
   */
  public static double sum(MatrixLike matrix) {
    double sum = 0;
    for (int i = 0; i < matrix.size(); i++) {
      sum += matrix.get(i);
    }
    return sum;
  }

  /**
   * Sum t.
   *
   * @param <T> the type parameter
   * @param f the f
   * @param m the m
   * @param axis the axis
   * @return the t
   */
  public static <T extends Matrix> T sum(Matrix.New<T> f, MatrixLike m, Axis axis) {
    switch (axis) {
      case ROW:
        return rowSum(f, m);
      case COLUMN:
        return columnSum(f, m);
      default:
        throw new IllegalArgumentException();
    }
  }

  public static Matrix sum(MatrixLike in, Axis axis) {
    return sum(DenseMatrix::new, in, axis);
  }

  private static <T extends Matrix> T columnSum(Matrix.New<T> f, MatrixLike m) {
    double[] values = new double[m.rows()];
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values[i] += m.get(i, j);
      }
    }
    return f.newMatrix(m.rows(), 1, values);
  }

  private static <T extends Matrix> T rowSum(Matrix.New<T> f, MatrixLike m) {
    double[] values = new double[m.columns()];
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values[j] += m.get(i, j);
      }
    }

    return f.newMatrix(1, m.columns(), values);
  }

  /**
   * Return the euclidean norm of x
   *
   * @param x a vector
   * @return the squared norm || x ||_2
   */
  public static double norm2(Matrix x) {
    double[] array = x.asDoubleArray();
    return cblas_dnrm2(array.length, array, 1);
  }

  /**
   * Computes the sum of the absolute values of elements
   *
   * @param a with values
   * @return the absolute sum
   */
  public static double dasum(Matrix a) {
    double[] values = a.asDoubleArray();
    return cblas_dasum(values.length, values, 1);
  }

}