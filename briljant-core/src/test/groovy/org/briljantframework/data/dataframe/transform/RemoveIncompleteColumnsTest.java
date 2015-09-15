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

package org.briljantframework.data.dataframe.transform;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.MixedDataFrame;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveIncompleteColumnsTest {

  @Test
  public void testTransform() throws Exception {
    DataFrame df = MixedDataFrame.of(
        "a", Vector.of(1, 1, 2, 2, null),
        "b", Vector.of(1, 2, 3, 4, 5),
        "c", Vector.of("hello", "a", "b", "c", "d"));
    Transformer ric = new RemoveIncompleteColumns();
    DataFrame removed = ric.transform(df);

    assertEquals(2, removed.columns());
    assertEquals(5, removed.rows());
    assertEquals(Vector.of(1, 2, 3, 4, 5), df.get("b"));
    assertEquals(Vector.of("hello", "a", "b", "c", "d"), df.get("c"));
  }
}
