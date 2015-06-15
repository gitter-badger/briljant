package org.briljantframework.dataframe;

import com.google.common.collect.ImmutableTable;

import org.briljantframework.Bj;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.AbstractVector;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * @author Isak Karlsson
 */
class RowView extends AbstractVector {

  private final DataFrame parent;
  private final int row;
  private final VectorType type;

  public RowView(DataFrame parent, int row, VectorType type) {
    super(parent.getColumnIndex());
    this.parent = parent;
    this.type = type;
    this.row = row;
  }

  public RowView(DataFrame parent, int row) {
    this(parent, row, Vec.VARIABLE);
  }

  @Override
  public VectorType getType() {
    return type;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return parent.get(cls, row, index);
  }

  @Override
  public String toString(int index) {
    return parent.get(index).toString(row);
  }

  @Override
  public boolean isNA(int index) {
    return parent.isNA(row, index);
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(row, index);
  }

  @Override
  public int getAsInt(int index) {
    return parent.getAsInt(row, index);
  }

  @Override
  public Bit getAsBit(int index) {
    return parent.getAsBit(row, index);
  }

  @Override
  public Complex getAsComplex(int index) {
    return parent.getAsComplex(row, index);
  }

  @Override
  public int size() {
    return parent.columns();
  }

  @Override
  public VectorType getType(int index) {
    return parent.getType(index);
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
  public Matrix toMatrix() {
    DoubleMatrix matrix = Bj.doubleMatrix(1, size());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, getAsDouble(i));
    }
    return matrix;
  }

  @Override
  public int compare(int a, int b) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public int compare(int a, Vector other, int b) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> b = new ImmutableTable.Builder<>();
    b.put(0, 0, "");
    b.put(1, 0, "[" + row + ",]");
    for (int i = 0; i < size(); i++) {
      b.put(0, i + 1, getIndex().get(i).toString());
      b.put(1, i + 1, toString(i));
    }
    return Utils.prettyPrintTable(b.build(), 1, 2, false, false);
  }
}