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

package org.briljantframework.data.vector;

import org.apache.commons.math3.complex.Complex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VectorsTest {

  private Vector vec6;
  private Vector vec8;

  @Before
  public void setUp() throws Exception {
    vec6 = DoubleVector.wrap(1, 2, 3, 4, 5, 6);
    vec8 = Vector.of("a", "sb", "cds", "qdsa", "fdasdsadsa", "dd", "r", "a");
  }

  @Test
  public void testInferType() throws Exception {
    assertEquals(VectorType.DOUBLE, VectorType.from(Double.class));
    assertEquals(VectorType.INT, VectorType.from(Integer.class));
    assertEquals(VectorType.LOGICAL, VectorType.from(Boolean.class));
    assertEquals(VectorType.LOGICAL, VectorType.from(Logical.class));
    assertEquals(VectorType.COMPLEX, VectorType.from(Complex.class));
    assertEquals(VectorType.STRING, VectorType.from(String.class));
    assertEquals(VectorType.DOUBLE, VectorType.from(Double.TYPE));
    assertEquals(VectorType.INT, VectorType.from(Integer.TYPE));
    assertEquals(VectorType.OBJECT, VectorType.from(null));
  }

  @Test
  public void testMode() throws Exception {
//    Vector v = Vector.of("a", "b", "c", "d", "e", "f", "a");
//    assertEquals("a", Vec.mode(v));
  }

  @Test
  public void testSplitExact() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 3);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(3, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2), listChunks.get(0));
    assertEquals(DoubleVector.wrap(3, 4), listChunks.get(1));
    assertEquals(DoubleVector.wrap(5, 6), listChunks.get(2));
  }

  @Test
  public void testSplitSingleton() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 6);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(1, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2, 3, 4, 5, 6), listChunks.get(0));
  }

  @Test
  public void testSplitUneven() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 4);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(4, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2), listChunks.get(0));
    assertEquals(DoubleVector.wrap(3, 4), listChunks.get(1));
    assertEquals(DoubleVector.wrap(5), listChunks.get(2));
    assertEquals(DoubleVector.wrap(6), listChunks.get(3));

  }
}