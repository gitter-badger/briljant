/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.array.base;

import java.util.Objects;

import org.briljantframework.array.AbstractLongArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseLongArray extends AbstractLongArray {

  private long[] data;

  BaseLongArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new long[size()];
  }

  BaseLongArray(ArrayFactory bj, long[] data) {
    super(bj, new int[] {Objects.requireNonNull(data).length});
    this.data = data;
  }

  BaseLongArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride, long[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @Override
  public LongArray newEmptyArray(int... shape) {
    return new BaseLongArray(getArrayFactory(), shape);
  }

  @Override
  public LongArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseLongArray(getArrayFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public long getElement(int index) {
    return data[index];
  }

  @Override
  public void setElement(int index, long value) {
    data[index] = value;
  }
}
