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

import org.briljantframework.complex.Complex;
import org.briljantframework.function.Aggregator;
import org.briljantframework.function.DoubleBiPredicate;

import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;

/**
 * A matrix is a 2-dimensional array.
 *
 * <p>
 * Every implementation have to ensure that {@link #set(int, double)}, {@link #get(int)} and
 * work in <b>column-major</b> order as fortran and not in
 * <b>row-major</b> order as in e.g., c.
 *
 * More specifically this means that given the matrix {@code m}
 *
 * <pre>
 *     1  2  3
 *     4  5  6
 *     7  8  9
 * </pre>
 *
 * The following must hold:
 * <ul>
 * <li>{@code for(int i = 0; i < m.size(); i++) System.out.print(m.get(i))} produces
 * {@code 147258369}</li>
 * <li>{@code for(int i = 0; i < m.size(); i++) m.put(i, m.get(i) * 2)} changes {@code m} to
 *
 * <pre>
 *     1   4   6
 *     8   10  12
 *     14  16  18
 * </pre>
 *
 * </li>
 * </ul>
 * <p>
 * Due to the order in which values are stored and implications such as cache-locality, different
 * implementations might have varying performance characteristics. For example, for element wise
 * operations one should prefer {@link #get(int)} and {@link #set(int, double)} to
 * {@link #get(int, int)} and {@link #set(int, int, double)}.
 *
 * <pre>
 * // Option 1:
 * for (int i = 0; i < m.rows(); i++) {
 *   for (int j = 0; j < m.columns() ; j++) {
 *     m.put(i, j, m.get(i, j) * 2);
 *   }
 * }
 *
 * // Option 2
 * for (int i = 0; i < m.size(); i++) {
 *   m.put(i, m.get(i) * 2)
 * }
 *
 * // Option 3
 * for (int j = 0; j < m.columns(); j++) {
 *   for (int i = 0; i < m.rows() ; i++) {
 *     m.put(i, j, m.get(i, j) * 2);
 *   }
 * }
 * </pre>
 *
 * In the example above, prefer <b>Option 2</b> (or simply {@code m.addi(2)}). <b>Option 3</b> can
 * also be an alternative option, that for many implementations preserves cache locality and might
 * be more readable in some cases.
 *
 * @author Isak Karlsson
 */
public interface DoubleMatrix extends Matrix<DoubleMatrix> {

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  DoubleMatrix assign(double value);

  DoubleMatrix assign(double[] array);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.DoubleSupplier#getAsDouble()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  DoubleMatrix assign(DoubleSupplier supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix   the matrix
   * @param operator the operator
   * @return receiver modified
   */
  DoubleMatrix assign(DoubleMatrix matrix, DoubleUnaryOperator operator);

  DoubleMatrix assign(DoubleMatrix matrix, DoubleBinaryOperator combine);

  DoubleMatrix assign(IntMatrix matrix, IntToDoubleFunction function);

  DoubleMatrix assign(LongMatrix matrix, LongToDoubleFunction function);

  DoubleMatrix assign(ComplexMatrix matrix, ToDoubleFunction<? super Complex> function);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  DoubleMatrix update(DoubleUnaryOperator operator);

  <R, C> R aggregate(Aggregator<? super Double, R, C> aggregator);

  <E> E collect(Supplier<E> supplier, ObjDoubleConsumer<E> consumer);

  // Transform

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Math::sqrt)} is equal to
   *
   * <pre>
   *     Matrix n = m.copy();
   *     for(int i = 0; i < n.size(); i++)
   *        n.put(i, Math.sqrt(n.get(i));
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Math::sqrt)} or
   * more verbosely
   *
   * <pre>
   *     for(int i = 0; i < m.size(); i++)
   *       m.put(i, Math.sqrt(m.get(i));
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  DoubleMatrix map(DoubleUnaryOperator operator);

  IntMatrix mapToInt(DoubleToIntFunction function);

  LongMatrix mapToLong(DoubleToLongFunction function);

  ComplexMatrix mapToComplex(DoubleFunction<Complex> function);

  // Filter

  DoubleMatrix filter(DoublePredicate predicate);

  BitMatrix satisfies(DoublePredicate predicate);

  BitMatrix satisfies(DoubleMatrix matrix, DoubleBiPredicate predicate);

  void forEach(DoubleConsumer consumer);

  // Reduce

  double reduce(double identity, DoubleBinaryOperator reduce);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a, b) -> a + b, x -> x)}
   *
   * @param identity the initial value
   * @param reduce   takes two values and reduces them to one
   * @param map      takes a value and possibly transforms it
   * @return the result
   */
  double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map);

  DoubleMatrix reduceAlongVector(Dim dim, ToDoubleFunction<? super DoubleMatrix> reduce);

  /**
   * Reduces each column. Column wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceColumns(col -&gt; col.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code Matrix} and returns {@code double}
   * @return a new column vector with the reduced value
   */
  DoubleMatrix reduceColumns(ToDoubleFunction<? super DoubleMatrix> reduce);

  /**
   * Reduces each rows. Row wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceRows(row -&gt; row.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code Matrix} and returns {@code double}
   * @return a new column vector with the reduced value
   */
  DoubleMatrix reduceRows(ToDoubleFunction<? super DoubleMatrix> reduce);

  void update(int i, DoubleUnaryOperator update);

  void update(int i, int j, DoubleUnaryOperator update);

  // GET SET

  void addTo(int i, double value);

  void addTo(int i, int j, double value);

  void set(int i, int j, double value);

  void set(int index, double value);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value double
   */
  double get(int i, int j);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
   * <p>
   *
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * <p>
   * prints
   * <p>
   * <p>
   *
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the value index
   */
  double get(int index);

  DoubleStream stream();

  List<Double> flat();

  // Arithmetical operations ///////////

  /**
   * <u>m</u>atrix diagonal multiplication
   *
   * @param diagonal the diagonal
   * @return matrix matrix
   */
  DoubleMatrix mmul(Diagonal diagonal);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha}.
   *
   * @param alpha scaling for {@code this*other}
   * @param other the other matrix
   * @return a new matrix
   */
  DoubleMatrix mmul(double alpha, DoubleMatrix other);

  DoubleMatrix mmul(T a, DoubleMatrix other, T b);

  DoubleMatrix mmul(double alpha, T a, DoubleMatrix other, T b);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  DoubleMatrix mul(double alpha, DoubleMatrix other, double beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleMatrix mul(double scalar);

  default DoubleMatrix addi(DoubleMatrix other) {
    return assign(other, (a, b) -> a + b);
  }

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleMatrix add(double scalar);

  default DoubleMatrix addi(double scalar) {
    return update(v -> v + scalar);
  }

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  DoubleMatrix add(double alpha, DoubleMatrix other, double beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  DoubleMatrix sub(double scalar);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  DoubleMatrix sub(double alpha, DoubleMatrix other, double beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  DoubleMatrix rsub(double scalar);

  default DoubleMatrix divi(DoubleMatrix other) {
    return assign(other, (x, y) -> x / y);
  }

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  DoubleMatrix div(double other);

  default DoubleMatrix divi(double value) {
    return update(v -> v / value);
  }

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  DoubleMatrix rdiv(double other);

  default DoubleMatrix rdivi(double other) {
    return update(v -> other / v);
  }

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  DoubleMatrix negate();

  /**
   * Returns a double array representation of this matrix. If {@linkplain #isView()} is {@code
   * true}, a copy is returned.
   *
   * @return a double array
   */
  double[] data();
}