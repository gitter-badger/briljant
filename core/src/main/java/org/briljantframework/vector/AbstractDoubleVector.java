package org.briljantframework.vector;

import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.storage.VectorStorage;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractDoubleVector extends AbstractVector {

  public static final VectorType TYPE = new VectorType() {
    @Override
    public DoubleVector.Builder newBuilder() {
      return new DoubleVector.Builder();
    }

    @Override
    public DoubleVector.Builder newBuilder(int size) {
      return new DoubleVector.Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Double.TYPE;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Double && Is.NA(value));
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      double dva = va.getAsDouble(a);
      double dba = ba.getAsDouble(b);

      return !Is.NA(dva) && !Is.NA(dba) ? Double.compare(dva, dba) : 0;
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public String toString() {
      return "real";
    }
  };

  @Override
  public Value get(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? Undefined.INSTANCE : new DoubleValue(value);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (Double.class.isAssignableFrom(cls)) {
      return cls.cast(getAsDouble(index));
    } else {
      return Vectors.naValue(cls);
    }
  }

  @Override
  public String toString(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? "NA" : Double.toString(value);
  }

  @Override
  public boolean isNA(int index) {
    return Is.NA(getAsDouble(index));
  }

  @Override
  public int getAsInt(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? IntVector.NA : (int) value;
  }

  @Override
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
  }

  @Override
  public String getAsString(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? StringVector.NA : Double.toString(value);
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public DoubleMatrix asMatrix() {
    return new DefaultDoubleMatrix(new VectorStorage(this));
  }

  @Override
  public int compare(int a, int b) {
    double va = getAsDouble(a);
    double vb = getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int compare(int a, Vector other, int b) {
    double va = getAsDouble(a);
    double vb = other.getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int hashCode() {
    int code = 1;
    for (int i = 0; i < size(); i++) {
      long v = Double.doubleToLongBits(getAsDouble(i));
      code += 31 * (int) (v ^ v >>> 32);
    }
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Vector) {
      Vector ov = (Vector) o;
      if (size() == ov.size()) {
        for (int i = 0; i < size(); i++) {
          if (getAsDouble(i) != ov.getAsDouble(i)) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

}
