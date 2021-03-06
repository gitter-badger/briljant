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

package org.briljantframework.data.dataframe.transform;

import org.briljantframework.Check;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public class MeanImputer implements Transformation {

  @Override
  public Transformer fit(DataFrame frame) {
    Vector.Builder builder = Vector.Builder.of(Double.class);
    for (Object key : frame.getColumnIndex().keySet()) {
      builder.set(key, Vectors.mean(frame.get(key)));
    }
    Vector means = builder.build();
    return new MeanImputTransformer(means);
  }

  @Override
  public String toString() {
    return "MeanImputer{}";
  }

  private static class MeanImputTransformer implements Transformer {

    private final Vector means;

    public MeanImputTransformer(Vector means) {
      this.means = means;
    }

    @Override
    public DataFrame transform(DataFrame x) {
      Check.size(x.columns(), means.size());
      DataFrame.Builder df = x.newBuilder();
      for (Object colKey : x.getColumnIndex().keySet()) {
        Vector column = x.get(colKey);
        for (Object rowKey : x.getIndex().keySet()) {
          if (column.isNA(rowKey)) {
            df.set(rowKey, colKey, means, colKey);
          } else {
            df.set(rowKey, colKey, column, rowKey);
          }
        }
      }
      return df.build();
    }

    @Override
    public String toString() {
      return "MeanImputTransformer{" + "means=" + means + '}';
    }
  }
}
