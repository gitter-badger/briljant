package org.briljantframework.shapelet;

import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vec;

/**
 * Created by Isak Karlsson on 27/10/14.
 */
public class IndexSortedNormalizedShapelet extends NormalizedShapelet {

  /**
   * The Order.
   */
  protected final int[] order;

  /**
   * Instantiates a new Index sorted normalized shapelet.
   *
   * @param start the start
   * @param length the length
   * @param vector the vector
   */
  public IndexSortedNormalizedShapelet(int start, int length, Vector vector) {
    super(start, length, vector);
    this.order =
        Vec.indexSort(this,
                      (i, j) -> Double.compare(Math.abs(this.getAsDouble(j)),
                                               Math.abs(this.getAsDouble(i))));
  }

  /**
   * Get order.
   *
   * @return the int [ ]
   */
  public int[] getSortOrder() {
    return order;
  }
}