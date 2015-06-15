package org.briljantframework.evaluation.result;

import com.google.common.base.Strings;

import org.briljantframework.vector.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * In the field of machine learning, a confusion matrix, also known as a contingency table or an
 * error matrix, is a specific table layout that allows visualization of the performance of an
 * algorithm, typically a supervised learning one. Each column of the matrix represents the
 * instances in a predicted class, while each row represents the instances in an actual class. The
 * name stems from the fact that it makes it easy to see if the system is confusing two classes
 * (i.e. commonly mislabeling one as another).
 * <p>
 * Example:
 *
 * <pre>
 *           acc       unacc       vgood       good
 *   acc     344.0     28.0        3.0         15.0
 *   unacc   29.0      1179.0      0.0         0.0
 *   vgood   5.0       0.0         61.0        9.0
 *   good    6.0       3.0         1.0         45.0
 * </pre>
 *
 * Created by isak on 02/10/14.
 */
public class ConfusionMatrix {

  private final Map<Object, Map<Object, Double>> matrix;
  private final Set<Object> labels;
  private final double sum;

  public ConfusionMatrix(Map<Object, Map<Object, Double>> matrix, Set<Object> labels, double sum) {
    this.matrix = checkNotNull(matrix, "Matrix cannot be null");
    this.labels = Collections.unmodifiableSet(checkNotNull(labels, "Labels cannot be null"));

    this.sum = sum;
  }

  public static ConfusionMatrix compute(Vector predictions, Vector truth, Vector domain) {
    checkArgument(predictions.size() == truth.size(), "The vector sizes don't match %s != %s.",
                  predictions.size(), truth.size());

    Map<Object, Map<Object, Double>> matrix = new HashMap<>();
    Set<Object> labels = new HashSet<>();
    double sum = 0;
    for (int i = 0; i < predictions.size(); i++) {
      Object predicted = predictions.get(Object.class, i);
      Object actual = truth.get(Object.class, i);

      Map<Object, Double> actuals = matrix.get(predicted);
      if (actuals == null) {
        actuals = new HashMap<>();
        matrix.put(predicted, actuals);
      }
      actuals.compute(actual, (key, value) -> value == null ? 1 : value + 1);

      labels.add(predicted);
      labels.add(actual);
      sum++;
    }
    return new ConfusionMatrix(matrix, labels, sum);
  }

  public double getAverageRecall() {
    return labels.stream().mapToDouble(this::getRecall).average().orElse(0);
  }

  public double getAveragePrecision() {
    return labels.stream().mapToDouble(this::getPrecision).average().orElse(0);
  }

  public double getAverageFMeasure(double beta) {
    return labels.stream().mapToDouble(value -> getFMeasure(value, beta)).average().orElse(0);
  }

  public double getFMeasure(Object target, double beta) {
    double precision = getPrecision(target);
    double recall = getRecall(target);
    double beta2 = beta * beta;
    if (precision > 0 && recall > 0) {
      return (1 + beta2) * ((precision * recall) / ((beta2 * precision) + recall));
    } else {
      return 0;
    }
  }

  public double getPrecision(Object target) {
    double tp = get(target, target);
    if (tp == 0) {
      return 0;
    } else {
      double conditional = 0.0;
      for (Object actual : getLabels()) {
        conditional += get(target, actual);
      }
      return conditional > 0 ? tp / conditional : 0;
    }
  }

  public double getRecall(Object target) {
    double tp = get(target, target);
    if (tp == 0) {
      return 0;
    } else {
      double conditional = 0.0;
      for (Object actual : getLabels()) {
        conditional += get(actual, target);
      }
      return conditional > 0 ? tp / conditional : 0;
    }
  }

  public double get(Object predicted, Object actual) {
    Map<Object, Double> values = matrix.get(predicted);
    if (values == null) {
      return 0;
    } else {
      return values.getOrDefault(actual, 0.0);
    }
  }

  public Set<Object> getLabels() {
    return labels;
  }

  public double getAccuracy() {
    double diagonal = 0.0;
    for (Object value : labels) {
      diagonal += get(value, value);
    }
    return diagonal / sum;
  }

  public double getError() {
    return 1 - getAccuracy();
  }

  public double getActual(String actual) {
    double sum = 0;
    for (Object predicted : getLabels()) {
      sum += get(predicted, actual);
    }
    return sum;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    int longest = labels.stream()
        .map(Object::toString)
        .mapToInt(String::length)
        .summaryStatistics()
        .getMax();
    if (longest < 3) {
      longest = 3;
    }

    int longestValue = 0;
    for (Object p : labels) {
      for (Object n : labels) {
        int len = Double.toString(get(p, n)).length();
        if (len > longestValue) {
          longestValue = len;
        }
      }
    }

    builder.append(Strings.repeat(" ", longest + 3));
    for (Object value : labels) {
      builder.append(value);
      builder.append(Strings.repeat(" ", longestValue + 1));
    }

    builder.append("\n");
    for (Object predicted : labels) {
      builder.append(Strings.padEnd(predicted.toString(), longest + 3, ' '));

      for (Object actual : labels) {
        String valueStr = Double.toString(get(predicted, actual));
        builder.append(valueStr);
        builder.append(
            Strings.repeat(" ", actual.toString().length() + 1 + longestValue - valueStr.length()));
      }
      builder.append("\n");
    }
    builder.append(" ---- (rows: predicted, columns: actual) ---- \n");
    builder.append("Accuracy       ");
    builder.append(String.format("%.2f", getAccuracy()));
    builder.append(" (");
    builder.append(String.format("%.2f", getError()));
    builder.append(")\n");
    builder.append("Avg. precision ").append(String.format("%.2f\n", getAveragePrecision()));
    builder.append("Avg. recall    ").append(String.format("%.2f\n", getAverageRecall()));

    return builder.toString();
  }
}