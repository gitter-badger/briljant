package org.briljantframework.shapelet;

import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.matrix.ArrayMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.VectorLike;
import org.junit.Test;

public class EarlyAbandonSlidingDistanceTest {

  @Test
  // TODO - bug!
  public void testDistance() throws Exception {
    Matrix a = ArrayMatrix.of(1, 6, 1, 2, 3, 4, 5, 6);
    Matrix a1 = ArrayMatrix.of(1, 4, 2, 3, 4, 6);
    Matrix a2 = ArrayMatrix.of(1, 6, 0, 1, 2, 4, 2, 3);
    Matrix b = ArrayMatrix.of(1, 2, 2, 3);

    Shapelet shapelet = NormalizedShapelet.create(1, 2, VectorLike.wrap(a));
    // System.out.println(shapelet);
    // System.out.println(b);

    System.out.println(shapelet);
    System.out.println(NormalizedShapelet.create(0, a2.size(), VectorLike.wrap(a2)));


    Distance distance = EarlyAbandonSlidingDistance.create(Euclidean.getInstance());
    // System.out.println(distance.distance(a, shapelet));
    // System.out.println(distance.distance(a1, shapelet));
    System.out.println(distance.distance(VectorLike.wrap(a2), shapelet));


    distance = OnlineReorderEarlyAbandonSlidingDistance.create();

    System.out.println(distance.distance(VectorLike.wrap(a2), shapelet));


    // System.out.println(Distance.EUCLIDEAN.distance(b, b));
  }

  @Test
  public void testLong() throws Exception {
    // Matrix l = DenseMatrix.of(1, 22, 3, 3, 2, 1, 4, 32, 5, 6, 7, 7, 8, 8, 1, 2, 1, 4, 32, 432, 1,
    // 34, 5, 32);
    // Shapelet candidate = NormalizedShapelet.create(5, 10, l);
    //
    //
    VectorLike l = VectorLike.wrap(Matrices.randn(1, 3213));
    Shapelet candidate = new Shapelet(2314, 500, l);
    //

    // Matrix l = DenseMatrix.of(1, 6, 1, 2, 3, 4, 5543, 6);
    // Shapelet candidate = NormalizedShapelet.create(2, 4, l);

    Distance distance = EarlyAbandonSlidingDistance.create(Euclidean.getInstance());

    long s = System.currentTimeMillis();
    // for (int i = 0; i < 100; i++) {
    // blacbox(distance.distance(l, candidate));
    // }
    System.out.println(distance.distance(l, candidate));
    System.out.println(System.currentTimeMillis() - s);

    Distance d = OnlineReorderEarlyAbandonSlidingDistance.create();
    s = System.currentTimeMillis();
    // for (int i = 0; i < 100; i++) {
    // blacbox(d.distance(l, candidate));
    // }

    System.out.println(d.distance(l, candidate));
    System.out.println(System.currentTimeMillis() - s);

  }

  private void blacbox(double distance) {

  }

  @Test
  public void testName() throws Exception {
    // DataSeriesInputStream tsis = new DataSeriesInputStream(new
    // FileInputStream("erlang/synthetic_control_TRAIN
    // .txt"));
    // Container<Frame, DefaultTarget> container = tsis.read(Frame.FACTORY, DefaultTarget.FACTORY);
    //
    // VectorView a = container.getDataset().getEntry(69);
    // Shapelet shapelet = Shapelet.create(3, 50, container.getDataset().getEntry(70));
    //
    // System.out.println(a.copy());
    // System.out.println(container.getDataset().getRowView(70));
    // System.out.println(shapelet);
    //
    // Distance distance = EarlyAbandonSlidingDistance.create(Distance.EUCLIDEAN);
    // System.out.println(1 / (double) shapelet.size() * distance.distance(a, shapelet));
  }
}
