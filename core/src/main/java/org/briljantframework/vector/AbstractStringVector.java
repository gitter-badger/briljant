package org.briljantframework.vector;

import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.AnyMatrix;

import com.google.common.primitives.Doubles;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractStringVector implements Vector, Iterable<String> {
  public static final String NA = null;
  public static final VectorType TYPE = new VectorType() {
    @Override
    public StringVector.Builder newBuilder() {
      return new StringVector.Builder();
    }

    @Override
    public StringVector.Builder newBuilder(int size) {
      return new StringVector.Builder(size);
    }


    @Override
    public Class<?> getDataClass() {
      return String.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == NA; // i.e. null checking
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      return !va.isNA(a) && !ba.isNA(b) ? va.getAsString(a).compareTo(ba.getAsString(b)) : 0;
    }

    @Override
    public Scale getScale() {
      return Scale.CATEGORICAL;
    }

    @Override
    public boolean equals(int a, Vector va, int b, Vector ba) {
      return !(!va.isNA(a) && !ba.isNA(b)) || va.getAsString(a).equals(ba.getAsString(b));
    }

    @Override
    public String toString() {
      return "string";
    }
  };

  @Override
  public Value getAsValue(int index) {
    String value = getAsString(index);
    return Is.NA(value) ? Undefined.INSTANCE : new StringValue(value);
  }

  @Override
  public String toString(int index) {
    String value = getAsString(index);
    return value == AbstractStringVector.NA ? "NA" : value;
  }

  @Override
  public boolean isNA(int index) {
    return getAsString(index) == NA;
  }

  @Override
  public double getAsDouble(int index) {
    return tryParseDouble(getAsString(index));
  }

  @Override
  public int getAsInt(int index) {
    return tryParseInteger(getAsString(index));
  }

  @Override
  public Bit getAsBit(int index) {
    String str = getAsString(index);
    if (str == null) {
      return Bit.NA;
    } else if (str.equalsIgnoreCase("true")) {
      return Bit.TRUE;
    } else if (str.equalsIgnoreCase("false")) {
      return Bit.FALSE;
    } else {
      return Bit.NA;
    }
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public AnyMatrix asMatrix() throws TypeConversionException {
    throw new TypeConversionException("Unable to convert StringVector to AnyMatrix");
  }

  @Override
  public int compare(int a, int b) {
    return !isNA(a) && !isNA(b) ? getAsString(a).compareTo(getAsString(b)) : 0;
  }

  @Override
  public int compare(int a, int b, Vector other) {
    String va = getAsString(a);
    String vb = other.getAsString(b);
    return !Is.NA(va) && !Is.NA(vb) ? va.compareTo(vb) : 0;
  }

  protected double tryParseDouble(String str) {
    if (str == AbstractStringVector.NA) {
      return DoubleVector.NA;
    }
    Double d = Doubles.tryParse(str);
    if (d != null) {
      return d;
    } else {
      return DoubleVector.NA;
    }
  }

  protected int tryParseInteger(String str) {
    if (str == AbstractStringVector.NA) {
      return IntVector.NA;
    }
    Double i = Doubles.tryParse(str);
    if (i != null) {
      return i.intValue();
    } else {
      return IntVector.NA;
    }
  }
}
