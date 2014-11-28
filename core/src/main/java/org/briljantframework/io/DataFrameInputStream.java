/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Binary;
import org.briljantframework.vector.Complex;
import org.briljantframework.vector.Type;

/**
 * The {@code DataFrameInputStream} is supposed to read a {@code DataFrame} from an input source.
 * <p>
 * There are three steps associated with this
 * <ol>
 * <li>Read the types of the Columns via {@link #readColumnType()}</li>
 * <li>Read the names of the Columns via {@link #readColumnName()}</li>
 * <li>Read values</li>
 * </ol>
 * <p>
 * The simplest is to use the convince methods {@link #readColumnTypes()} and
 * {@link #readColumnNames()} constructing a {@link DataFrame.Builder} and use its
 * {@link org.briljantframework.dataframe.DataFrame.Builder#read(DataFrameInputStream)} method.
 * <p>
 * For example: <code>
 * <pre>
 *      DataFrameInputStream dfis = ...;
 *      Collection<Type> types = dfis.readTypes();
 *      Collection<String> names = dfis.readNames();
 *      DataFrame.Builder builder = new MixedDataFrame(names, types);
 *      DataFrame dataFrame = builder.read(dfis).create();
 * </pre>
 * </code>
 * 
 * Values returned by {@link #nextString()} etc. are returned in row-major order and typed according
 * to the {@link Type}s returned by {@link #readColumnTypes()}.
 * 
 * For example, given the dataset, where the first and second row are names and types respectively:
 * 
 * <pre>
 *     a       b       c
 *   double  string   int
 *    3.2     hello    1
 *    2.0     sx       3
 *    2       dds     100
 * </pre>
 * 
 * {@link #readColumnNames()} should return {@code ["a", "b", "c"]} and {@link #readColumnTypes()}
 * should return {@code [DoubleVector.TYPE, StringVector.TYPE, IntVector.TYPE]}.
 * 
 * Then, subsequent calls to {@link #nextDouble()}, {@link #nextString()} and {@link #nextInt()}
 * should return {@code 3.2, "hello", 1, 2.0, "sx", 3, 2, "dds", 100} in sequence.
 * <p>
 * Created by Isak Karlsson on 14/08/14.
 */
public abstract class DataFrameInputStream extends FilterInputStream {

  protected static final String NAMES_BEFORE_TYPE = "Can't read name before types";
  protected static final String UNEXPECTED_EOF = "Unexpected EOF.";
  protected static final String VALUES_BEFORE_NAMES_AND_TYPES =
      "Reading values before names and types";


  protected final TypeFactory typeFactory;

  /**
   * Instantiates a new Storage input stream.
   *
   * @param in the in
   * @param typeFactory the factory
   */
  protected DataFrameInputStream(InputStream in, TypeFactory typeFactory) {
    super(in);
    this.typeFactory = typeFactory;
  }

  /**
   * Instantiates a new Storage input stream.
   *
   * @param in the in
   */
  protected DataFrameInputStream(InputStream in) {
    this(in, new DefaultTypeFactory());
  }

  /**
   * Reads the column types of this data frame input stream. Returns {@code null} when there are no
   * more types to read.
   *
   * @return a type or {@code null}
   * @throws IOException
   */
  public abstract Type readColumnType() throws IOException;

  /**
   * Reads the column names from the input stream. Returns {@code null} when there are no more
   * column names.
   *
   * @return a column name or {@code null}
   * @throws IOException
   */
  public abstract String readColumnName() throws IOException;

  /**
   * For convenience. This method reads all column types from the input stream.
   * <p>
   * Same as:
   * 
   * <pre>
   * Type t = null;
   * while ((t = f.readColumnType()) != null) {
   *   coll.add(t);
   * }
   * </pre>
   *
   * @return a collection of types
   * @throws IOException
   */
  public Collection<Type> readColumnTypes() throws IOException {
    List<Type> types = new ArrayList<>();
    for (Type type = readColumnType(); type != null; type = readColumnType()) {
      types.add(type);
    }
    return Collections.unmodifiableCollection(types);
  }

  /**
   * For convenience. This method read all the column names from the input stream.
   * <p>
   * Same as:
   * 
   * <pre>
   * String n = null;
   * while ((n = f.readColumnName()) != null) {
   *   coll.add(t);
   * }
   * </pre>
   *
   * @return a collection of column names
   * @throws IOException
   */
  public Collection<String> readColumnNames() throws IOException {
    List<String> names = new ArrayList<>();
    for (String type = readColumnName(); type != null; type = readColumnName()) {
      names.add(type);
    }
    return Collections.unmodifiableCollection(names);
  }

  /**
   * Reads the next string in this stream
   * 
   * @return the next string
   * @throws IOException
   */
  public abstract String nextString() throws IOException;

  /**
   * Reads the next int in this stream
   * 
   * @return the next int
   * @throws IOException
   * @throws java.lang.NumberFormatException
   */
  public abstract int nextInt() throws IOException;

  /**
   * Reads the next {@code double} in this stream
   * 
   * @return the next {@code double}
   * @throws IOException
   * @throws java.lang.NumberFormatException
   */
  public abstract double nextDouble() throws IOException;

  /**
   * Reads the next {@code Binary} in this stream.
   * 
   * @return the next binary
   * @throws IOException
   * @throws java.lang.NumberFormatException
   */
  public abstract Binary nextBinary() throws IOException;

  /**
   * Reads the next {@code Complex} in this stream.
   * 
   * @return the next complex
   * @throws IOException
   * @throws NumberFormatException
   */
  public abstract Complex nextComplex() throws IOException;

  /**
   * Returns {@code true} if there are more values in the stream
   * 
   * @return if has next
   * @throws IOException
   */
  public abstract boolean hasNext() throws IOException;
}