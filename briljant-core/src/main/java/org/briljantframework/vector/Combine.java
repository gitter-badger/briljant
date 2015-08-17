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

package org.briljantframework.vector;

import org.apache.commons.math3.complex.Complex;

import java.util.function.BiFunction;

/**
 * @author Isak Karlsson
 */
public final class Combine {

  private Combine() {
  }

  /**
   * Returns a {@code BiFunction} that ignores {@code NA} values by returning {@code NA} when
   * either
   * the {@code left} or {@code right} value is {@code NA}.
   *
   * @param combine the function to apply for non-{@code NA} values
   * @param <T>     the input type
   * @param <R>     the output type
   * @return a {@code NA} safe {@code BiFunction}
   */
  public static <T, R> BiFunction<T, T, R> ignoreNA(BiFunction<T, T, R> combine) {
    return (a, b) -> {
      boolean aNA = Is.NA(a);
      boolean bNA = Is.NA(b);
      if (aNA || bNA) {
        return null;
      } else {
        return combine.apply(a, b);
      }
    };
  }

  /**
   * Returns a {@code BiFunction} that ignores {@code NA} values by returning {@code NA} when both
   * the {@code left} or {@code right} value is {@code NA} and fill the {@code left} or {@code
   * right} value when either is {@code NA}.
   *
   * <p>For example:
   * <pre>{@code
   *  BiFunction<Integer, Integer, Integer> adder = ignoreNA((a, b) -> a + b), 10);
   *  adder(null, 1);    // 11
   *  adder(1, null);    // 11
   *  adder(null, null); // NA
   *  adder(1, 1);       // 2
   * }</pre>
   *
   * @param combine the function to apply for non-{@code NA} values
   * @param <T>     the input type
   * @param <R>     the output type
   * @return a {@code NA} safe {@code BiFunction}
   */
  public static <T, R> BiFunction<T, T, R> ignoreNA(BiFunction<T, T, R> combine, T fillValue) {
    return (a, b) -> {
      boolean aNA = Is.NA(a);
      boolean bNA = Is.NA(b);
      if (aNA && bNA) {
        return null;
      } else {
        if (aNA) {
          return combine.apply(fillValue, b);
        } else if (bNA) {
          return combine.apply(a, fillValue);
        } else {
          return combine.apply(a, b);
        }
      }
    };
  }

  /**
   * @return an adder that ignores {@code NA}
   */
  public static BiFunction<Object, Object, Object> add() {
    return ignoreNA(Combine::plusNumber);
  }

  /**
   * @return an adder that ignores {@code NA} and defaults to {@code fillValue}
   * @see #ignoreNA(java.util.function.BiFunction, Object)
   */
  public static BiFunction<Object, Object, Object> add(Number fillValue) {
    return ignoreNA(Combine::plusNumber, fillValue);
  }

  public static BiFunction<Object, Object, Object> mul() {
    return ignoreNA(Combine::multiplyNumber);
  }

  public static BiFunction<Object, Object, Object> multiply(Number fillValue) {
    return ignoreNA(Combine::multiplyNumber, fillValue);
  }

  public static BiFunction<Object, Object, Object> div() {
    return ignoreNA(Combine::divNumber);
  }

  public static BiFunction<Object, Object, Object> div(Object fillValue) {
    return ignoreNA(Combine::divNumber, fillValue);
  }

  public static BiFunction<Object, Object, Object> sub() {
    return ignoreNA(Combine::minusNumber);
  }

  public static BiFunction<Object, Object, Object> sub(Object fillValue) {
    return ignoreNA(Combine::minusNumber, fillValue);
  }

  private static Object plusNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() + ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).add((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() + ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() + ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }

  private static Object minusNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() - ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).subtract((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() - ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() - ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }

  private static Object multiplyNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() * ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).multiply((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() * ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() * ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }

  private static Object divNumber(Object a, Object b) {
    if (a instanceof Integer && b instanceof Integer || a instanceof Short && b instanceof Short) {
      return ((Number) a).intValue() / ((Number) a).intValue();
    } else if (a instanceof Complex && b instanceof Complex) {
      return ((Complex) a).divide((Complex) b);
    } else if (a instanceof Long && b instanceof Long) {
      return ((Number) a).longValue() / ((Number) b).longValue();
    } else if (a instanceof Number) {
      return ((Number) a).doubleValue() / ((Number) b).doubleValue();
    } else {
      return null; // NA
    }
  }
}
