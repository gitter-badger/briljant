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

package org.briljantframework.data.dataseries;

import org.briljantframework.Check;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;

/**
 * The MeanResampler implements the perhaps simplest resampling (approximation) method for data
 * series. Divide the data series into bins, and take the mean of each bin as the new data series.
 *
 * This is know under the fancy name Piecewise Aggregate Approximation
 *
 * @author Isak Karlsson
 */
public class MeanAggregator implements Aggregator {

  private final int targetSize;

  public MeanAggregator(int targetSize) {
    this.targetSize = targetSize;
  }

  @Override
  public Vector.Builder partialAggregate(Vector in) {
    Check.argument(in.size() >= targetSize, "Input size must be larger than target size.");
    if (in.size() == targetSize) {
      return in.newCopyBuilder();
    }
    Vector.Builder out = Vector.Builder.withCapacity(Double.class, targetSize);
    int bin = in.size() / targetSize;
    int pad = in.size() % targetSize;

    int currentIndex = 0;
    int toPad = 0;
    while (currentIndex < in.size()) {
      int inc = 0;
      if (toPad++ < pad) {
        inc = 1;
      }
      double sum = 0;
      int binInc = bin + inc;
      for (int j = 0; j < binInc; j++) {
        sum += in.loc().getAsDouble(currentIndex++);
      }
      out.add(sum / binInc);
    }
    return out;
  }

  @Override
  public VectorType getAggregatedType() {
    return VectorType.DOUBLE;
  }
}
