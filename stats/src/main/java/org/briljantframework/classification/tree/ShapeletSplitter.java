package org.briljantframework.classification.tree;

import org.briljantframework.distance.Distance;

/**
 * Created by isak on 02/10/14.
 */
public abstract class ShapeletSplitter implements Splitter<ShapeletThreshold> {

  protected final Gain gain;
  private final Distance metric;

  /**
   * Instantiates a new Shapelet splitter.
   *
   * @param metric the metric
   */
  protected ShapeletSplitter(Distance metric, Gain gain) {
    this.metric = metric;
    this.gain = gain;
  }

  /**
   * Gets metric.
   *
   * @return the metric
   */
  public final Distance getDistanceMetric() {
    return metric;
  }

  /**
   * Gets gain.
   *
   * @return the gain
   */
  public final Gain getGain() {
    return gain;
  }
}
