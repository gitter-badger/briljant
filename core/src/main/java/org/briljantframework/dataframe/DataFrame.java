package org.briljantframework.dataframe;

import java.io.IOException;

import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.*;

/**
 * A DataFrame is a heterogeneous or homogeneous storage of data.
 * <p>
 * Created by Isak Karlsson on 21/11/14.
 */
public interface DataFrame extends Iterable<CompoundVector> {

  /**
   * Get value at {@code row} and {@code column} as string.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  String getAsString(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as double.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  double getAsDouble(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as int.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  int getAsInt(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as binary.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  Binary getAsBinary(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as complex.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  Complex getAsComplex(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as a value
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  Value getAsValue(int row, int column);

  /**
   * Returns true if value at {@code row, column} is NA.
   *
   * @param row the row
   * @param column the column
   * @return true or false
   */
  boolean isNA(int row, int column);

  /**
   * Get vector at {@code index}
   *
   * @param index the index
   * @return the vector
   */
  Vector getColumn(int index);

  /**
   * Get the type of vector at {@code index}
   *
   * @param index the index
   * @return the type
   */
  Type getColumnType(int index);

  /**
   * Get the name for the column vector at {@code index}.
   *
   * @param index the index
   * @return the name
   */
  String getColumnName(int index);

  /**
   * Get the row at {@code index}. Since a {@code DataFrame} can have columns of multiple types, the
   * returned type is a Sequence i.e. a heterogeneous vector of values.
   *
   * @param index the index
   * @return the row sequence
   */
  CompoundVector getRow(int index);

  /**
   * Returns the number of rows in this data frame
   *
   * @return the number of rows
   */
  int rows();

  /**
   * Returns the number of columns in this data frame
   *
   * @return the number of columns
   */
  int columns();

  /**
   * Creates a new builder with the same column types as this data frame
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Creates a new builder with the same column types as this data frame with {@code rows} rows, all
   * initialized to NA
   *
   * @param rows initial size
   * @return a new builder
   */
  Builder newBuilder(int rows);

  /**
   * Creates a new builder, initialized with a copy of this data frame, i.e.
   * {@code c.newCopyBuilder().create()} creates a new copy.
   *
   * @return a new builder
   */
  Builder newCopyBuilder();

  /**
   * Returns this as a matrix of double values.
   *
   * @return this data frame as a matrix
   */
  Matrix asMatrix();

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder {

    /**
     * Set value at {@code row} in {@code column} to NA.
     *
     * @param row the row
     * @param column the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#setNA(int)
     */
    Builder setNA(int row, int column);

    /**
     * Add a new NA value to {@code column}.
     *
     * @param column the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#addNA()
     */
    Builder addNA(int column);

    /**
     * Set value at {@code row, toCol} using the value at {@code fromRow, fromCol} in {@code from}.
     *
     * @param toRow the row
     * @param toCol the column
     * @param from the vector
     * @param fromRow the row
     * @param fromCol the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#set(int,
     *      org.briljantframework.vector.Vector, int)
     */
    Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol);

    /**
     * Add value to {@code toCol} using value {@code fromRow, fromCol} in {@code vector}.
     *
     * @param toCol the column
     * @param from the vector
     * @param fromRow the row
     * @param fromCol the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#add(org.briljantframework.vector.Vector,
     *      int)
     */
    Builder add(int toCol, DataFrame from, int fromRow, int fromCol);

    /**
     * Add the value {@code fromRow} from {@code from} to {@code toCol}.
     *
     * @param toCol the column
     * @param from the vector
     * @param fromRow the column
     * @return a modified builder
     */
    Builder add(int toCol, Vector from, int fromRow);

    /**
     * Set value at {@code row, column} to {@code value}.
     *
     * @param row the row
     * @param column the column
     * @param value the value
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#set(int, Object)
     */
    Builder set(int row, int column, Object value);

    /**
     * Add value to {@code column}
     *
     * @param col the column
     * @param value the value
     * @return a modified builder
     */
    Builder add(int col, Object value);

    /**
     * Add a new vector builder. If {@code builder.size() < rows()} the builder is padded with NA.
     *
     * @param builder builder to add
     * @return a modified builder
     */
    Builder addColumn(Vector.Builder builder);

    /**
     * Removes vector builder at {@code column}.
     *
     * @param column the index
     * @return a modified builder
     */
    Builder removeColumn(int column);

    /**
     * Swaps column vector {@code a} and {@code b}.
     *
     * @param a an index
     * @param b an index
     * @return a modified builder
     */
    Builder swapColumns(int a, int b);

    /**
     * Add all values in {@code vector} to column {@code toCol}.
     *
     * @param toCol the index
     * @param vector the vector
     * @return a modified builder
     */
    default Builder addAll(int toCol, Vector vector) {
      for (int i = 0; i < vector.size(); i++) {
        add(toCol, vector, i);
      }
      return this;
    }

    /**
     * Add all values from frame (from column 0 until column())
     *
     * @param frame the frame
     * @return a modified builder
     */
    default Builder addAll(DataFrame frame) {
      for (int i = 0; i < columns(); i++) {
        addAll(i, frame.getColumn(i));
      }
      return this;
    }

    public Builder read(DataFrameInputStream inputStream) throws IOException;

    /**
     * Returns the number of columns in the resulting data frame
     *
     * @return the number of columns
     */
    int columns();

    /**
     * Returns the number of rows in the resulting data frame
     *
     * @return the number of rows
     */
    int rows();

    /**
     * Create a new DataFrame.
     *
     * @return a new data frame
     */
    DataFrame create();
  }

}