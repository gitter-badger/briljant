package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class IntValue extends AbstractIntVector implements Value {
  private final int value;

  public IntValue(int value) {
    this.value = value;
  }

  @Override
  public int compareTo(Value o) {
    return getAsInt() - o.getAsInt();
  }

  @Override
  public int getAsInt(int index) {
    return value;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Builder newCopyBuilder() {
    return IntVector.TYPE.newBuilder().set(0, this, 0);
  }

  @Override
  public Builder newBuilder() {
    return IntVector.TYPE.newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return IntVector.TYPE.newBuilder(size);
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    IntValue integers = (IntValue) o;

    if (value != integers.value)
      return false;

    return true;
  }

  @Override
  public Iterator<Integer> iterator() {
    return Iterators.singletonIterator(value);
  }
}
