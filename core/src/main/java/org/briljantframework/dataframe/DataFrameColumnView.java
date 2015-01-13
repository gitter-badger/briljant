package org.briljantframework.dataframe;

import org.briljantframework.matrix.AnyMatrix;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * View into a DataFrame.
 * 
 * @author Isak Karlsson
 */
public class DataFrameColumnView implements Vector {

  private final DataFrame parent;
  private final int column;

  public DataFrameColumnView(DataFrame parent, int column) {
    this.parent = parent;
    this.column = column;
  }

  @Override
  public Value getAsValue(int index) {
    return parent.getAsValue(index, column);
  }

  @Override
  public String toString(int index) {
    return parent.toString(index, column);
  }

  @Override
  public boolean isNA(int index) {
    return parent.isNA(index, column);
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(index, column);
  }

  @Override
  public int getAsInt(int index) {
    return parent.getAsInt(index, column);
  }

  @Override
  public Bit getAsBit(int index) {
    return parent.getAsBinary(index, column);
  }

  @Override
  public String getAsString(int index) {
    return parent.getAsString(index, column);
  }

  @Override
  public int size() {
    return parent.rows();
  }

  @Override
  public VectorType getType() {
    return parent.getColumnType(column);
  }

  @Override
  public Builder newCopyBuilder() {
    return newBuilder().addAll(this);
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  @Override
  public AnyMatrix asMatrix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compare(int a, int b) {
    return getType().compare(a, this, b, this);
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return getType().compare(a, this, b, other);
  }
}
