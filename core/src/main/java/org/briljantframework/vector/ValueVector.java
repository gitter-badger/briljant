package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.Matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @
 */
public class ValueVector extends AbstractVector implements VariableVector {

  private final List<? extends Value> values;

  /**
   * Constructs a {@code VariableVector}
   *
   * @param values the values
   */
  public ValueVector(List<? extends Value> values) {
    this.values = values;
  }

  @Override
  public Value get(int index) {
    return values.get(index);
  }

  @Override
  public <T> T getAs(Class<T> cls, int index) {
    return get(index).getAs(cls, 0);
  }

  @Override
  public String toString(int index) {
    return values.get(index).toString(0);
  }

  @Override
  public boolean isNA(int index) {
    return values.get(index).isNA(0);
  }

  @Override
  public double getAsDouble(int index) {
    return values.get(index).getAsDouble(0);
  }

  @Override
  public int getAsInt(int index) {
    return values.get(index).getAsInt(0);
  }

  @Override
  public Bit getAsBit(int index) {
    return values.get(index).getAsBit(0);
  }

  @Override
  public Complex getAsComplex(int index) {
    return values.get(index).getAsComplex(0);
  }

  @Override
  public String getAsString(int index) {
    return values.get(index).getAsString(0);
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public Matrix asMatrix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compare(int a, int b) {
    return get(a).compareTo(get(b));
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return get(a).compareTo(other.get(b));
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size);
  }

  @Override
  public VectorType getType(int index) {
    return values.get(index).getType();
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(new ArrayList<>(values));
  }

  // @Override
  // public Iterator<Value> iterator() {
  // return new UnmodifiableIterator<Value>() {
  // public int current = 0;
  //
  // @Override
  // public boolean hasNext() {
  // return current < size();
  // }
  //
  // @Override
  // public Value next() {
  // return get(current++);
  // }
  // };
  // }

  public static class Builder implements Vector.Builder {
    private List<Value> buffer;


    private Builder(List<Value> buffer) {
      this.buffer = buffer;
    }

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(size, size);
    }

    public Builder(int size, int capacity) {
      buffer = new ArrayList<>(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(Undefined.INSTANCE);
      }
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.set(index, Undefined.INSTANCE);
      return this;
    }

    @Override
    public Builder addNA() {
      return setNA(size());
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      set(size(), from, fromIndex);
      return this;
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.set(atIndex, from.get(fromIndex));
      return this;
    }

    @Override
    public Builder set(int index, Object obj) {
      Value value;
      if (obj instanceof Value) {
        value = (Value) obj;
      } else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short) {
        value = new IntValue(((Number) obj).intValue());
      } else if (obj instanceof Float || obj instanceof Double) {
        value = new DoubleValue(((Number) obj).doubleValue());
      } else if (obj instanceof Complex) {
        value = new ComplexValue((Complex) obj);
      } else if (obj instanceof Bit) {
        value = new BitValue((Bit) obj);
      } else if (obj != null) {
        value = new StringValue(obj.toString());
      } else {
        value = Undefined.INSTANCE;
      }
      ensureCapacity(index);
      buffer.set(index, value);
      return this;
    }

    @Override
    public Builder add(Object value) {
      set(size(), value);
      return this;
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        buffer.add(from.get(i));
      }
      return this;
    }

    @Override
    public Builder remove(int index) {
      buffer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      return TYPE.compare(buffer.get(a), buffer.get(b));
    }

    @Override
    public void swap(int a, int b) {
      Preconditions.checkArgument(a >= 0 && a < size() && b >= 0 && b < size());
      Collections.swap(buffer, a, b);
    }

    @Override
    public Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    @Override
    public Builder read(int index, DataEntry entry) throws IOException {
      set(index, entry.nextString());
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new ValueVector(this.buffer);
    }

    @Override
    public ValueVector build() {
      return new ValueVector(buffer);
    }

    private void ensureCapacity(int index) {
      while (buffer.size() <= index) {
        buffer.add(Undefined.INSTANCE);
      }
    }
  }
}
