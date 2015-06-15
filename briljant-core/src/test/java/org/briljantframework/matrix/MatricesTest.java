package org.briljantframework.matrix;

import org.briljantframework.Bj;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.matrix.netlib.NetlibMatrixBackend;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.junit.Assert.assertEquals;

public class MatricesTest {

  private final NetlibMatrixBackend b = new NetlibMatrixBackend();
  private final MatrixFactory bj = b.getMatrixFactory();
  private final MatrixRoutines bjr = b.getMatrixRoutines();


  @Test
  public void testArgMax() throws Exception {
    DoubleMatrix v = bj.matrix(new double[]{1.0, 2, 3, 9, 5, 1, 2});
    assertEquals(3, Bj.argmax(v));
  }

  @Test
  public void testGetMatrixFactory() throws Exception {

  }

  @Test
  public void testZeros() throws Exception {

  }

  @Test
  public void testZeros1() throws Exception {

  }

  @Test
  public void testZeros2() throws Exception {

  }

  @Test
  public void testZeros3() throws Exception {

  }

  @Test
  public void testOnes() throws Exception {

  }

  @Test
  public void testOnes1() throws Exception {

  }

  @Test
  public void testFilledWith() throws Exception {

  }

  @Test
  public void testFilledWith1() throws Exception {

  }

  @Test
  public void testFilledWith2() throws Exception {

  }

  @Test
  public void testFilledWith3() throws Exception {

  }

  @Test
  public void testRandn() throws Exception {

  }

  @Test
  public void testRandn1() throws Exception {

  }

  @Test
  public void testRand() throws Exception {

  }

  @Test
  public void testRand1() throws Exception {

  }

  @Test
  public void testEye() throws Exception {

  }

  @Test
  public void testMax() throws Exception {

  }

  @Test
  public void testArgmax() throws Exception {

  }

  @Test
  public void testArgmin() throws Exception {

  }

  @Test
  public void testRange() throws Exception {

  }

  @Test
  public void testRange1() throws Exception {

  }

  @Test
  public void testTake() throws Exception {

  }

  @Test
  public void testMask() throws Exception {

  }

  @Test
  public void testPutMask() throws Exception {

  }

  @Test
  public void testSelect() throws Exception {

  }

  @Test
  public void testSort() throws Exception {

  }

  @Test
  public void testSort1() throws Exception {

  }

  @Test
  public void testLinspace() throws Exception {

  }

  @Test
  public void testMap() throws Exception {

  }

  @Test
  public void testSqrt() throws Exception {

  }

  @Test
  public void testLog() throws Exception {

  }

  @Test
  public void testLog2() throws Exception {

  }

  @Test
  public void testPow() throws Exception {

  }

  @Test
  public void testLog10() throws Exception {

  }

  @Test
  public void testSignum() throws Exception {

  }

  @Test
  public void testAbs() throws Exception {

  }

  @Test
  public void testRound() throws Exception {

  }

  @Test
  public void testTrace() throws Exception {

  }

  @Test
  public void testSum() throws Exception {

  }

  @Test
  public void testSum1() throws Exception {

  }

  @Test
  public void testSum2() throws Exception {

  }

  @Test
  public void testSum3() throws Exception {

  }

  @Test
  public void testSum4() throws Exception {

  }

  @Test
  public void testNewComplexMatrix() throws Exception {

  }

  @Test
  public void testShuffle() throws Exception {

  }

  @Test
  public void testVstack() throws Exception {
    DoubleMatrix a = bj.doubleMatrix(3, 3).assign(10);
    DoubleMatrix b = bj.doubleMatrix(2, 3).assign(3);
    DoubleMatrix c = bj.doubleMatrix(1, 3).assign(1);
    DoubleMatrix hstack = bjr.vstack(Arrays.asList(a, b, c));
    System.out.println(hstack);
    assertMatrixEquals(a, bjr.vstack(Arrays.asList(a)), 0);
    assertEquals(3 + 2 + 1, hstack.rows());
    assertEquals(3, hstack.columns());
    assertMatrixEquals(10, hstack.getView(0, 0, 3, 3), 0);
    assertMatrixEquals(3, hstack.getView(3, 0, 2, 3), 0);
    assertMatrixEquals(1, hstack.getView(3 + 2, 0, 1, 3), 0);
  }

  @Test
  public void testHstack() throws Exception {
    DoubleMatrix a = bj.doubleMatrix(3, 3).assign(10);
    DoubleMatrix b = bj.doubleMatrix(3, 2).assign(2);
    DoubleMatrix c = bj.doubleMatrix(3, 1).assign(1);
    DoubleMatrix vstack = bjr.hstack(Arrays.asList(a, b, c));

    assertMatrixEquals(a, bjr.hstack(Arrays.asList(a)), 0);
    assertEquals(3 + 2 + 1, vstack.columns());
    assertEquals(3, vstack.rows());
    assertMatrixEquals(10, vstack.getView(0, 0, 3, 3), 0);
    assertMatrixEquals(2, vstack.getView(0, 3, 3, 2), 0);
    assertMatrixEquals(1, vstack.getView(0, 3 + 2, 3, 1), 0);
  }

  @Test
  public void testShuffle1() throws Exception {

  }

  @Test
  public void testParseMatrix() throws Exception {

  }

  @Test
  public void testEye1() throws Exception {

  }

  @Test
  public void testSort2() throws Exception {

  }

  @Test
  public void testSort3() throws Exception {

  }

  @Test
  public void testSort4() throws Exception {

  }

  @Test
  public void testMean() throws Exception {

  }

  @Test
  public void testMean1() throws Exception {

  }

  @Test
  public void testStd() throws Exception {

  }

  @Test
  public void testStd1() throws Exception {

  }

  @Test
  public void testStd2() throws Exception {

  }

  @Test
  public void testVar() throws Exception {

  }

  @Test
  public void testVar1() throws Exception {

  }

  @Test
  public void testVar2() throws Exception {

  }

  @Test
  public void testMmul() throws Exception {

  }

  @Test
  public void testMmul1() throws Exception {

  }

  @Test
  public void testNewComplexVector() throws Exception {

  }

  @Test
  public void testArgmaxnot() throws Exception {

  }

  @Test
  public void testMaxnot() throws Exception {

  }

  @Test
  public void testVsplit() throws Exception {
    DoubleMatrix a = bj.range(0, 9).reshape(3, 3).asDoubleMatrix();
    List<DoubleMatrix> m = bjr.vsplit(a, 3);
    assertEquals(3, m.size());
    assertMatrixEquals(a.getRow(0), m.get(0), 0);
    assertMatrixEquals(a.getRow(1), m.get(1), 0);
    assertMatrixEquals(a.getRow(2), m.get(2), 0);


  }

  @Test
  public void testHsplit() throws Exception {
    DoubleMatrix a = bj.range(0, 9).reshape(3, 3).asDoubleMatrix();
    List<DoubleMatrix> m = bjr.hsplit(a, 3);
    assertEquals(3, m.size());
    assertMatrixEquals(a.getColumn(0), m.get(0), 0);
    assertMatrixEquals(a.getColumn(1), m.get(1), 0);
    assertMatrixEquals(a.getColumn(2), m.get(2), 0);
  }
}