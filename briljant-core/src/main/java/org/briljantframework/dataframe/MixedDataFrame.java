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

package org.briljantframework.dataframe;

import org.briljantframework.Check;
import org.briljantframework.index.Index;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.EntryReader;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 *
 * @author Isak Karlsson
 */
public class MixedDataFrame extends AbstractDataFrame {

  private final List<Vector> columns;
  private final int rows;

  /**
   * Constructs a new mixed data frame from balanced vectors
   *
   * @param columns the vectors
   */
  public MixedDataFrame(Vector... columns) {
    this(Arrays.asList(columns));
  }

  /**
   * Construct a new mixed data frame from a collection of balanced vectors
   *
   * @param vectors the collection of vectors
   */
  public MixedDataFrame(Collection<? extends Vector> vectors) {
    super(null, null); // TODO: fix me
    Check.argument(vectors.size() > 0);

    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    for (Vector vector : vectors) {
      if (rows == 0) {
        rows = vector.size();
      }
      Check.argument(
          vector.size() == rows,
          "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size()
      );
      this.columns.add(vector);
    }
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from a map of strings (column names) and balanced (i.e. of
   * same size) vectors.
   *
   * @param vectors the map of vectors and names
   */
  public <T> MixedDataFrame(Map<T, ? extends Vector> vectors) {
    super(null, null); // TODO: fix me
    Check.argument(vectors.size() > 0);
    this.columns = new ArrayList<>(vectors.size());
    int rows = 0;
    List<T> columnIndex = new ArrayList<>();
    for (Map.Entry<T, ? extends Vector> kv : vectors.entrySet()) {
      Vector vector = kv.getValue();
      T key = kv.getKey();
      columnIndex.add(key);
      if (rows == 0) {
        rows = vector.size();
      }
      Check.argument(
          vector.size() == rows,
          "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size()
      );
      this.columns.add(vector);
    }
    this.rows = rows;
    setColumnIndex(new HashIndex(columnIndex));
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used
   * for
   * performance by checked builder).
   *
   * @param vectors the vectors
   * @param rows    the expected size of the vectors (not checked but should be enforced)
   */
  protected MixedDataFrame(List<Vector> vectors, int rows) {
    super(null, null); // TODO: fix me
    this.columns = vectors;
    this.rows = rows;
  }

  private MixedDataFrame(List<Vector> columns, int rows, Index columnIndex, Index recordIndex) {
    super(columnIndex, recordIndex);
    Check.argument(columns.size() == columnIndex.size());
    Check.argument(recordIndex.size() == rows);
    this.columns = columns;
    this.rows = rows;
  }

  private static Vector.Builder padVectorWithNA(Vector.Builder builder, int maximumRows) {
    if (builder.size() < maximumRows) {
      builder.setNA(maximumRows - 1);
    }
    return builder;
  }

  public static DataFrame of(Object name, Vector c) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(name, c);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3,
                             Object n4, Vector v4) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    return new MixedDataFrame(map);
  }

  public static DataFrame of(Object n1, Vector v1, Object n2, Vector v2, Object n3, Vector v3,
                             Object n4, Vector v4, Object n5, Vector v5) {
    HashMap<Object, Vector> map = new LinkedHashMap<>();
    map.put(n1, v1);
    map.put(n2, v2);
    map.put(n3, v3);
    map.put(n4, v4);
    map.put(n5, v5);
    return new MixedDataFrame(map);
  }

  public static DataFrame read(DataInputStream io) throws IOException {
    Collection<Object> index = io.readColumnIndex();
    DataFrame frame = new MixedDataFrame.Builder(io.readColumnTypes()).read(io).build();
    if (index != null) {
      frame.setColumnIndex(HashIndex.from(index));
      return frame;
    } else {
      return frame;
    }
  }

  @Override
  public <T> T getAt(Class<T> cls, int row, int column) {
    return columns.get(column).get(cls, row);
  }

  @Override
  public double getAsDoubleAt(int row, int column) {
    return columns.get(column).getAsDouble(row);
  }

  @Override
  public int getAsIntAt(int row, int column) {
    return columns.get(column).getAsInt(row);
  }

  @Override
  public String toStringAt(int row, int column) {
    return columns.get(column).toString(row);
  }

  @Override
  public boolean isNaAt(int row, int column) {
    return columns.get(column).isNA(row);
  }

  @Override
  public VectorType getTypeAt(int index) {
    return columns.get(index).getType();
  }

  @Override
  public int rows() {
    return rows;
  }

  @Override
  public int columns() {
    return columns.size();
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(this);
  }

  @Override
  public DataFrame add(Vector column) {
    List<Vector> newColumns = new ArrayList<>(columns);
    if (column.size() == rows()) {
      newColumns.add(column);
    } else if (column.size() < rows()) {
      newColumns.add(padVectorWithNA(column.newCopyBuilder(), rows()).build());
    } else {
      throw new IllegalArgumentException();
    }
    return new MixedDataFrame(newColumns, rows());
  }

  @Override
  public Vector getAt(int index) {
    return columns.get(index); // TODO: the index?!
  }

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }

  public static class Builder extends AbstractBuilder {

    private List<Vector.Builder> buffers = null;

    public Builder() {
      this.buffers = new ArrayList<>();
    }

    /**
     * Construct a builder with {@code types.length} columns.
     *
     * @param types the column types
     */
    public Builder(VectorType... types) {
      this(Arrays.asList(types));
    }

    /**
     * Construct a builder with {@code types.size()} columns. The column names will be {@code 1 ...
     * types.length}
     *
     * @param types the column types
     */
    public Builder(Collection<? extends VectorType> types) {
      this(types.stream().map(VectorType::newBuilder).toArray(Vector.Builder[]::new));
    }

    /**
     * <p> Construct a builder using vector builders. Vector builders of different sizes are
     * allowed, but padded with NA values until to match the longest. </p>
     *
     * <p> Hence,
     *
     * <pre>
     *     [1 2 3]
     *     [1]
     *     [1,2,3,4]
     * </pre>
     *
     * Added would result in:
     *
     * <pre>
     *     [1,2,3, NA]
     *     [1, NA, NA, NA]
     *     [1,2,3,4]
     * </pre>
     *
     * </p>
     *
     * @param builders the vector builders
     */
    public Builder(Vector.Builder... builders) {
      int rows = Stream.of(builders).mapToInt(Vector.Builder::size).max().getAsInt();
      this.buffers = new ArrayList<>();
      for (Vector.Builder builder : builders) {
        if (builder.size() < rows) {
          builder.setNA(rows - 1);
        }

        buffers.add(builder);
      }
    }

    /**
     * Clones {@code frame}. If {@code copy == true}, the values are copied. Otherwise, only the
     * types and column names are copied.
     *
     * @param frame the DataFrame to clone
     */
    public Builder(MixedDataFrame frame) {
      super(frame.getColumnIndex().newCopyBuilder(), frame.getRecordIndex().newCopyBuilder());
      buffers = frame.columns.stream()
          .map(Vector::newCopyBuilder)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected void setNaAt(int row, int column) {
      ensureColumnCapacity(column);
      buffers.get(column).setNA(row);
    }

    @Override
    public void setAt(int r, int c, Vector from, int i) {
      ensureColumnCapacity(c - 1);
      ensureColumnCapacity(c, from.getType());
      buffers.get(c).set(r, from, i);
    }

    @Override
    protected void setRecordAt(int index, Vector.Builder builder) {
      ensureColumnCapacity(builder.size() - 1);
      final int columns = columns();
      final int size = builder.size();
      final Vector vector = builder.getTemporaryVector();
      for (int j = 0; j < Math.max(size, columns); j++) {
        if (j < size) {
          setAt(index, j, vector, j);
        } else {
          setNaAt(index, j);
        }
      }
    }

    @Override
    public void setAt(int r, int c, Object value) {
      ensureColumnCapacity(c - 1);
      ensureColumnCapacity(c, VectorType.from(value));
      buffers.get(c).set(r, value);
    }

    @Override
    public void removeAt(int column) {
      buffers.remove(column);
    }

    @Override
    public void swapAt(int a, int b) {
      Collections.swap(buffers, a, b);
    }

    public Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).swap(a, b);
      return this;
    }

    @Override
    public void swapRecordsAt(int a, int b) {
      for (int i = 0; i < columns(); i++) {
        swapInColumn(i, a, b);
      }
    }


    @Override
    public Builder read(EntryReader entryReader) throws IOException {
      while (entryReader.hasNext()) {
        DataEntry entry = entryReader.next();
        ensureColumnCapacity(entry.size() - 1);
        for (int i = 0; i < entry.size(); i++) {
          buffers.get(i).read(entry);
        }
      }

      return this;
    }

    @Override
    public int columns() {
      return buffers.size();
    }

    /**
     * Returns the vector with most rows
     *
     * @return the number of rows
     */
    @Override
    public int rows() {
      return buffers.stream().mapToInt(Vector.Builder::size).reduce(0, Integer::max);
    }

    @Override
    public DataFrame getTemporaryDataFrame() {
      int rows = rows();
      List<Vector> vectors = buffers.stream()
          .map((builder) -> padVectorWithNA(builder, rows).getTemporaryVector())
          .collect(Collectors.toCollection(ArrayList::new));
      return new MixedDataFrame(vectors, rows);
    }

    /**
     * Constructs a new MixedDataFrame
     *
     * @return a new MixedDataFrame
     */
    @Override
    public MixedDataFrame build() {
      int rows = rows();
      List<Vector> vectors = buffers.stream()
          .map(x -> padVectorWithNA(x, rows).build())
          .collect(Collectors.toCollection(ArrayList::new));
      buffers = null;
      return new MixedDataFrame(vectors, rows, columnIndex.build(), recordIndex.build());
    }

    private void ensureColumnCapacity(int index, VectorType type) {
      int i = buffers.size();
      while (i <= index) {
        buffers.add(type.newBuilder());
        i++;
      }
    }

    private void ensureColumnCapacity(int index) {
      int i = buffers.size();
      while (i <= index) {
        buffers.add(VectorType.inferringBuilder());
        i++;
      }
    }

    @Override
    public void setAt(int c, Vector.Builder builder) {
      if (c == buffers.size()) {
        this.buffers.add(builder);
      } else {
        this.buffers.set(c, builder);
      }
//      return this;
    }
//
//    @Override
//    public DataFrame.Builder addColumn(int index, Vector vector) {
//      return addColumn(index, vector.newCopyBuilder());
//    }
  }
}
