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

import org.briljantframework.array.AbstractArray;
import org.briljantframework.array.Array;
import org.briljantframework.array.Indexer;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseReferenceArray<T> extends AbstractArray<T> {

  private final T[] data;

  BaseReferenceArray(ArrayFactory bj, T[] data) {
    this(bj, new int[] {data.length}, data);
  }

  BaseReferenceArray(ArrayFactory bj, int[] shape, T[] data) {
    super(bj, shape);
    this.data = data;
  }

  BaseReferenceArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
      T[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @SuppressWarnings("unchecked")
  public BaseReferenceArray(BaseArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = (T[]) new Object[Indexer.size(shape)];
  }

  @Override
  protected T getElement(int i) {
    return data[i];
  }

  @Override
  protected void setElement(int i, T value) {
    data[i] = value;
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public Array<T> asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseReferenceArray<>(getArrayFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  public Array<T> newEmptyArray(int... shape) {
    @SuppressWarnings("unchecked")
    T[] data = (T[]) new Object[Indexer.size(shape)];
    return new BaseReferenceArray<>(getArrayFactory(), shape, data);
  }

  @Override
  public T[] data() {
    return data;
  }
}
