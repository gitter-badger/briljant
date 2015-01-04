package org.briljantframework.dataframe;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.vector.*;
import org.briljantframework.vector.Vector;

import com.google.common.collect.Lists;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 * <p>
 * Created by Isak Karlsson on 21/11/14.
 */
public class MixedDataFrame extends AbstractDataFrame {

  private final List<String> names;
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
    checkArgument(vectors.size() > 0);

    this.names = Lists.newArrayList("0");
    this.columns = new ArrayList<>(vectors.size());
    int rows = 0, index = 0;
    for (Vector vector : vectors) {
      if (rows == 0) {
        rows = vector.size();
      }
      checkArgument(vector.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size());
      this.columns.add(vector);
      this.names.add(String.valueOf(index++));
    }
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from a map of strings (column names) and balanced (i.e. of
   * same size) vectors.
   *
   * @param vectors the map of vectors and names
   */
  public MixedDataFrame(Map<String, ? extends Vector> vectors) {
    checkArgument(vectors.size() > 0);
    this.names = new ArrayList<>(vectors.size());
    this.columns = new ArrayList<>(vectors.size());

    int rows = 0;
    for (Map.Entry<String, ? extends Vector> kv : vectors.entrySet()) {
      Vector vector = kv.getValue();
      String name = kv.getKey();
      if (rows == 0) {
        rows = vector.size();
      }

      checkArgument(vector.size() == rows, "Arguments imply different numbers of rows: %s, %s.",
          rows, vector.size());
      this.names.add(name);
      this.columns.add(vector);
    }
    this.rows = rows;
  }

  /**
   * Constructs a new mixed data frame from an iterable sequence of of
   * {@link org.briljantframework.vector.VariableVector} treated as rows of equal length
   *
   * @param sequences
   */
  public MixedDataFrame(Iterable<? extends VariableVector> sequences) {
    this.names = new ArrayList<>();
    this.columns = new ArrayList<>();

    List<Vector.Builder> builders = new ArrayList<>();
    int columns = 0, rows = 0;
    for (VariableVector row : sequences) {
      if (columns == 0) {
        columns = row.size();
      }
      checkArgument(row.size() == columns, "Arguments imply different numbers of rows: %s, %s.",
          columns, row.size());
      for (int i = 0; i < row.size(); i++) {
        if (builders.size() <= i) {
          checkArgument(row.getType(i) != VariableVector.TYPE,
              "Can't create untyped vector as column.");
          builders.add(row.getType(i).newBuilder());
        }
        builders.get(i).add(row.getAsValue(i));
      }
      rows++;
    }
    int index = 0;
    for (Vector.Builder builder : builders) {
      this.names.add(String.valueOf(index++));
      this.columns.add(builder.build());
    }

    this.rows = rows;
  }

  /**
   * Unsafe construction of a mixed data frame. Performs no sanity checking (should only be used for
   * performance by checked builder).
   *
   * @param names the names
   * @param vectors the vectors
   * @param rows the expected size of the vectors (not checked but should be enforced)
   */
  protected MixedDataFrame(List<String> names, List<Vector> vectors, int rows) {
    checkArgument(names.size() == vectors.size());
    this.names = names;
    this.columns = vectors;
    this.rows = rows;
  }

  @Override
  public String getAsString(int row, int column) {
    return columns.get(column).getAsString(row);
  }

  @Override
  public double getAsDouble(int row, int column) {
    return columns.get(column).getAsDouble(row);
  }

  @Override
  public int getAsInt(int row, int column) {
    return columns.get(column).getAsInt(row);
  }

  @Override
  public Binary getAsBinary(int row, int column) {
    return columns.get(column).getAsBinary(row);
  }

  @Override
  public Complex getAsComplex(int row, int column) {
    return columns.get(column).getAsComplex(row);
  }

  @Override
  public Value getAsValue(int row, int column) {
    return columns.get(column).getAsValue(row);
  }

  @Override
  public String toString(int row, int column) {
    return columns.get(column).toString(row);
  }

  @Override
  public boolean isNA(int row, int column) {
    return columns.get(column).isNA(row);
  }

  @Override
  public Type getColumnType(int index) {
    return columns.get(index).getType();
  }

  @Override
  public String getColumnName(int index) {
    return names.get(index);
  }

  @Override
  public DataFrame setColumnName(int index, String columnName) {
    names.set(index, columnName);
    return this;
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
    return new Builder(this, false);
  }

  @Override
  public Builder newBuilder(int rows) {
    return new Builder(this, rows, columns());
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(this, true);
  }

  @Override
  public Vector getColumn(int index) {
    return columns.get(index);
  }

  @Override
  public DataFrame dropColumn(int index) {
    checkArgument(index >= 0 && index < columns());
    ArrayList<Vector> columns = new ArrayList<>(this.columns);
    ArrayList<String> names = new ArrayList<>(this.names);

    columns.remove(index);
    names.remove(index);
    return new MixedDataFrame(names, columns, rows());
  }

  @Override
  public DataFrame dropColumns(Collection<Integer> indexes) {
    if (!(indexes instanceof Set)) {
      indexes = new HashSet<>(indexes);
    }

    ArrayList<Vector> columns = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    for (int i = 0; i < columns(); i++) {
      if (!indexes.contains(i)) {
        columns.add(getColumn(i));
        names.add(getColumnName(i));
      }
    }

    return new MixedDataFrame(names, columns, rows());
  }

  @Override
  public DataFrame takeColumns(Collection<Integer> indexes) {
    ArrayList<Vector> columns = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    for (int index : indexes) {
      checkArgument(index >= 0 && index < columns());
      columns.add(getColumn(index));
      names.add(getColumnName(index));
    }

    return new MixedDataFrame(names, columns, rows());
  }

  @Override
  public String toString() {
    return DataFrames.toTabularString(this);
  }

  /**
   * <p>
   * Type for constructing a new MixedDataFrame. While for example,
   * {@link org.briljantframework.dataframe.MatrixDataFrame} and
   * {@link org.briljantframework.dataseries.DataSeriesCollection.Builder} can dynamically adapt the
   * number of columns in the constructed DataFrame, this builder can only construct DataFrames with
   * a fixed number of columns due to the fact that each column can be of different types.
   * </p>
   * 
   * <p>
   * To overcome this limitation, {@link #addColumn(org.briljantframework.vector.Vector.Builder)}
   * and {@link #removeColumn(int)} can be used.
   * </p>
   *
   *
   * <p>
   *
   * </p>
   * 
   */
  public static class Builder implements DataFrame.Builder {

    private List<Vector.Builder> buffers = null;
    private List<String> colNames = null;

    /**
     * Construct a builder with {@code types.length} columns. The column names will be
     * {@code 1 ... types.length}
     * 
     * @param types the column types
     */
    public Builder(Type... types) {
      this(Arrays.asList(types));
    }

    /**
     * Construct a builder with {@code types.size()} columns. The column names will be
     * {@code 1 ... types.length}
     * 
     * @param types the column types
     */
    public Builder(Collection<? extends Type> types) {
      buffers = new ArrayList<>(types.size());
      colNames = new ArrayList<>(types.size());
      int index = 0;
      for (Type type : types) {
        colNames.add(String.valueOf(index++));
        buffers.add(type.newBuilder());
      }
    }

    /**
     * Construct a builder with {@code types.size()} columns with names from {@code colNames}.
     * Asserts that {@code colNames.size() == types.size()}
     * 
     * @param colNames the column names
     * @param types the types
     */
    public Builder(Collection<String> colNames, Collection<? extends Type> types) {
      checkArgument(colNames.size() > 0 && colNames.size() == types.size(),
          "Column names and types does not match.");
      this.buffers = new ArrayList<>(types.size());
      this.colNames = new ArrayList<>(colNames);
      for (Type type : types) {
        buffers.add(type.newBuilder());
      }
    }

    /**
     * <p>
     * Construct a builder using vector builders. Vector builders of different sizes are allowed,
     * but padded with NA values until to match the longest.
     * </p>
     * 
     * <p>
     * Hence,
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
     * @param copy copy values or only types
     */
    public Builder(DataFrame frame, boolean copy) {
      buffers = new ArrayList<>(frame.columns());
      colNames = new ArrayList<>(frame.columns());

      for (int i = 0; i < frame.columns(); i++) {
        Vector vector = frame.getColumn(i);
        if (copy) {
          buffers.add(vector.newCopyBuilder());
        } else {
          buffers.add(vector.newBuilder());
        }
        colNames.add(frame.getColumnName(i));
      }
    }

    private Builder(MixedDataFrame frame, int rows, int columns) {
      buffers = new ArrayList<>(columns);
      for (int i = 0; i < columns; i++) {
        buffers.add(frame.getColumn(i).newBuilder(rows));
      }
    }

    @Override
    public Builder setNA(int row, int column) {
      buffers.get(column).setNA(row);
      return this;
    }

    @Override
    public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
      buffers.get(toCol).set(toRow, from.getColumn(fromCol), fromRow);
      return this;
    }

    @Override
    public Builder set(int row, int column, Vector from, int index) {
      buffers.get(column).set(row, from, index);
      return this;
    }

    @Override
    public Builder set(int row, int column, Object value) {
      buffers.get(column).set(row, value);
      return this;
    }

    @Override
    public Builder addColumn(Vector.Builder builder) {
      if (colNames != null) {
        colNames.add(String.valueOf(colNames.size()));
      }
      buffers.add(builder);
      return this;
    }

    @Override
    public Builder removeColumn(int column) {
      if (colNames != null) {
        colNames.remove(column);
      }
      buffers.remove(column);
      return this;
    }

    @Override
    public Builder swapColumns(int a, int b) {
      if (colNames != null) {
        Collections.swap(colNames, a, b);
      }
      Collections.swap(buffers, a, b);
      return this;
    }

    @Override
    public DataFrame.Builder swapInColumn(int column, int a, int b) {
      buffers.get(column).swap(a, b);
      return this;
    }

    @Override
    public DataFrame.Builder read(DataInputStream inputStream) throws IOException {
      while (inputStream.hasNext()) {
        DataEntry entry = inputStream.next();
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

    /**
     * Constructs a new MixedDataFrame
     * 
     * @return a new MixedDataFrame
     */
    @Override
    public MixedDataFrame build() {
      int rows = rows();
      List<Vector> vectors =
          buffers.stream().map(x -> padVectorWithNA(x, rows))
              .collect(Collectors.toCollection(ArrayList::new));
      buffers = null;
      return new MixedDataFrame(colNames, vectors, rows);
    }

    private Vector padVectorWithNA(Vector.Builder builder, int maximumRows) {
      if (builder.size() < maximumRows) {
        builder.setNA(maximumRows - 1);
      }
      return builder.build();
    }
  }
}
