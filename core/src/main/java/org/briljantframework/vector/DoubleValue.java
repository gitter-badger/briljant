package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class DoubleValue extends AbstractDoubleVector implements Value {

  private final double value;

  public DoubleValue(double value) {
    this.value = value;
  }

  @Override
  public int compareTo(Value o) {
    return isNA() && o.isNA() ? 0 : Double.compare(getAsDouble(), o.getAsDouble());
  }

  @Override
  public double getAsDouble(int index) {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    DoubleValue doubles = (DoubleValue) o;

    if (Double.compare(doubles.value, value) != 0)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(value);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public String toString() {
    return toString(0);
  }

  @Override
  public Iterator<Double> iterator() {
    return Iterators.singletonIterator(getAsDouble());
  }

  public static Value valueOf(double d) {
    return Is.NA(d) ? Undefined.INSTANCE : new DoubleValue(d);
  }
}
