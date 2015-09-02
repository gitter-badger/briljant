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

package org.briljantframework.data.dataseries;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.DoubleVector;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataSeriesCollectionTest {

  @Test
  public void testDropRows() throws Exception {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(VectorType.DOUBLE);
    builder.addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6))
        .addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6))
        .addRecord(DoubleVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 6));

    DataSeriesCollection collection = builder.build();
    DataFrame drop = collection.loc().drop(0, 1);
    for (Vector row : drop) {
      assertEquals(3, row.loc().getAsDouble(0), 0.0001);
      assertEquals(4, row.loc().getAsDouble(1), 0.0001);
      assertEquals(5, row.loc().getAsDouble(2), 0.0001);
      assertEquals(6, row.loc().getAsDouble(3), 0.0001);
    }
  }
}