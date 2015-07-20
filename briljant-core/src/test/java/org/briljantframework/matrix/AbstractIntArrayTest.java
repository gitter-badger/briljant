package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.ArrayFactory;
import org.briljantframework.matrix.netlib.NetlibArrayBackend;
import org.junit.Test;

import java.util.Arrays;
import java.util.IntSummaryStatistics;

import static org.briljantframework.matrix.MatrixAssert.assertMatrixEquals;
import static org.briljantframework.matrix.MatrixAssert.assertValuesEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractIntArrayTest {

  private final ArrayFactory bj = new NetlibArrayBackend().getArrayFactory();

  @Test
  public void testAssign() throws Exception {
    IntArray m = bj.intArray(3, 3);
    m.assign(3);
    assertMatrixEquals(3, m);
  }

  @Test
  public void testAssign1() throws Exception {
    IntArray m = bj.intArray(3, 3);
    m.assign(() -> 3);
    assertMatrixEquals(3, m);
  }

  @Test
  public void testAssign2() throws Exception {
    IntArray m = bj.intArray(3, 3);
    m.assign(3).update(x -> x * 2);
    assertMatrixEquals(6, m);
  }

  @Test
  public void testAssign3() throws Exception {
    DoubleArray d = bj.doubleArray(3, 3).assign(3);
    IntArray i = bj.intArray(3, 3).assign(d, x -> (int) x);
    assertMatrixEquals(3, i);
  }

  @Test
  public void testAssign4() throws Exception {
    ComplexArray c = bj.complexArray(3, 3).assign(Complex.valueOf(3));
    IntArray i = bj.intArray(3, 3).assign(c, Complex::intValue);
    assertMatrixEquals(3, i);
  }

  @Test
  public void testAssign5() throws Exception {
    LongArray l = bj.longArray(3, 3).assign(3L);
    IntArray i = bj.intArray(3, 3).assign(l, x -> (int) x);
    assertMatrixEquals(3, i);
  }

  @Test
  public void testAssign6() throws Exception {
    IntArray i = bj.intArray(3, 3).assign(bj.intArray().assign(3));
    assertMatrixEquals(3, i);
  }

  @Test
  public void testAssign7() throws Exception {
    IntArray i = bj.intArray(3, 3).assign(bj.intArray().assign(3), x -> x * 2);
    assertMatrixEquals(6, i);
  }

  @Test
  public void testAssign8() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(2);
    IntArray d = bj.intArray(3, 3).assign(5);
    x.assign(d, Integer::sum);
    assertMatrixEquals(7, x);
  }

  @Test
  public void testMap() throws Exception {
    IntArray i = bj.intArray(3, 3).assign(3);
    IntArray m = i.map(Integer::bitCount);
    assertMatrixEquals(2, m);
  }

  @Test
  public void testMapToLong() throws Exception {
    IntArray i = bj.intArray(3, 3).assign(3);
    LongArray l = i.mapToLong(x -> Integer.MAX_VALUE + (long) x);
    assertMatrixEquals(l, ((long) Integer.MAX_VALUE) + 3L);
  }

  @Test
  public void testMapToDouble() throws Exception {
    DoubleArray i = bj.intArray(3, 3).assign(3).mapToDouble(Math::sqrt);
    assertMatrixEquals(Math.sqrt(3), i, 0.0001);
  }

  @Test
  public void testMapToComplex() throws Exception {
    ComplexArray i = bj.intArray(3, 3).assign(-3).mapToComplex(Complex::sqrt);
    assertMatrixEquals(i, Complex.sqrt(-3));
  }

  @Test
  public void testFilter() throws Exception {
    IntArray i = bj.array(new int[]{0, 1, 2, 3, 4, 5, 6}).filter(x -> x > 3);
    assertValuesEquals(bj.array(new int[]{4, 5, 6}), i);
  }

  @Test
  public void testSatisfies() throws Exception {
    BitArray i = bj.array(new int[]{0, 1, 2, 3, 4, 5}).satisfies(x -> x >= 3);
    MatrixAssert
        .assertValuesEquals(bj.array(new boolean[]{false, false, false, true, true, true}), i);
  }

  @Test
  public void testSatisfies1() throws Exception {
    IntArray x = bj.intArray(3, 3);
    IntArray y = bj.intArray(3, 3).assign(3);
    BitArray z = x.satisfies(y, (a, b) -> a < b);
    assertMatrixEquals(z, true);
  }

  @Test
  public void testReduce() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    int sum = x.reduce(0, Integer::sum);
    assertEquals(3 * 9, sum);
  }

  @Test
  public void testReduce1() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    int squaredSum = x.reduce(0, Integer::sum, i -> i * 2);
    assertEquals(3 * 2 * 9, squaredSum);
  }

  @Test
  public void testReduceColumns() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3).reduceColumns(y -> y.reduce(0, Integer::sum));
    assertEquals(4, x.columns());
    assertEquals(1, x.rows());
    assertMatrixEquals(3 * 3, x);
  }

  @Test
  public void testReduceRows() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3).reduceRows(y -> y.reduce(0, Integer::sum));
    assertEquals(4, x.rows());
    assertEquals(1, x.columns());
    assertMatrixEquals(3 * 3, x);
  }

  @Test
  public void testReshape() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(2, 3);
    assertEquals(2, x.rows());
    assertEquals(3, x.columns());
  }

  @Test
  public void testGet() throws Exception {
    IntArray x = bj.array(new int[]{0, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0));
    assertEquals(5, x.get(5));
  }

  @Test
  public void testGet1() throws Exception {
    IntArray x = bj.array(new int[]{0, 1, 2, 3, 4, 5}).reshape(3, 2);
    assertEquals(0, x.get(0, 0));
    assertEquals(3, x.get(0, 1));
    assertEquals(4, x.get(1, 1));
  }

  @Test
  public void testSet() throws Exception {
    IntArray x = bj.intArray(3, 3);
    x.set(0, 0, 1);
    x.set(0, 1, 2);
    x.set(1, 1, 3);

    assertEquals(1, x.get(0, 0));
    assertEquals(2, x.get(0, 1));
    assertEquals(3, x.get(1, 1));
  }

  @Test
  public void testSet1() throws Exception {
    IntArray x = bj.array(new int[]{0, 1, 2, 3});
    assertEquals(0, x.get(0));
    assertEquals(1, x.get(1));
    assertEquals(2, x.get(2));
    assertEquals(3, x.get(3));
  }

  @Test
  public void testAddTo() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 1});
    x.addTo(0, 10);
    assertEquals(11, x.get(0));
  }

  @Test
  public void testAddTo1() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 1}).reshape(2, 2);
    x.addTo(0, 0, 10);
    x.addTo(0, 1, 10);
    assertEquals(11, x.get(0, 0));
    assertEquals(11, x.get(0, 1));
  }

  @Test
  public void testUpdate() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 1}).reshape(2, 2);
    x.update(0, 0, i -> i * 3);
    assertEquals(3, x.get(0, 0));
  }

  @Test
  public void testUpdate1() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 1});
    x.update(0, i -> i * 3);
    assertEquals(3, x.get(0));
  }

  @Test
  public void testGetRowView() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    assertMatrixEquals(1, x.getRow(0));
    assertMatrixEquals(2, x.getRow(1));
    assertMatrixEquals(3, x.getRow(2));
  }

  @Test
  public void testGetColumnView() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 2, 2, 2, 3, 3, 3}).reshape(3, 3);
    assertMatrixEquals(1, x.getColumn(0));
    assertMatrixEquals(2, x.getColumn(1));
    assertMatrixEquals(3, x.getColumn(2));
  }

  @Test
  public void testGetDiagonalView() throws Exception {

  }

  @Test
  public void testGetView() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 1, 2, 2}).reshape(2, 3);
    assertMatrixEquals(1, x.getView(0, 0, 2, 2));
  }

  @Test
  public void testTranspose() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3}).reshape(2, 3).transpose();
    assertEquals(3, x.rows());
    assertEquals(2, x.columns());
    assertEquals(1, x.get(0, 0));
    assertEquals(3, x.get(2, 1));
  }

  @Test
  public void testCopy() throws Exception {
    IntArray x = bj.array(new int[]{1, 1, 1, 1});
    IntArray y = x.copy();
    x.set(0, 1000);
    assertEquals(1, y.get(0));
  }

  @Test
  public void testNewEmptyMatrix() throws Exception {
    assertNotNull(bj.intArray(3, 3).newEmptyArray(2, 2));
  }

  @Test
  public void testNewEmptyVector() throws Exception {
    IntArray x = bj.intArray(3, 3).newEmptyArray(2);
    assertNotNull(x);
    assertEquals(2, x.rows());
    assertEquals(1, x.columns());
  }

  @Test
  public void testMmul() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    IntArray y = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(2, 3);

    IntArray z = y.mmul(x);
    IntArray za = bj.array(new int[]{22, 28, 49, 64}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(y);
    za = bj.array(new int[]{9, 12, 15, 19, 26, 33, 29, 40, 51}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul1() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    IntArray y = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(2, 3);

    IntArray z = y.mmul(2, x);
    IntArray za = bj.array(new int[]{44, 56, 98, 128}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(4, y);
    za = bj.array(new int[]{36, 48, 60, 76, 104, 132, 116, 160, 204}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul2() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    IntArray y = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);

    IntArray z = y.mmul(Op.TRANSPOSE, x, Op.KEEP);
    IntArray za = bj.array(new int[]{14, 32, 32, 77}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(Op.KEEP, y, Op.TRANSPOSE);
    za = bj.array(new int[]{17, 22, 27, 22, 29, 36, 27, 36, 45}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMmul3() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    IntArray y = bj.array(new int[]{1, 2, 3, 4, 5, 6}).reshape(3, 2);
    IntArray z = y.mmul(2, Op.TRANSPOSE, x, Op.KEEP);
    IntArray za = bj.array(new int[]{28, 64, 64, 154}).reshape(2, 2);
    assertMatrixEquals(za, z);

    z = x.mmul(2, Op.KEEP, y, Op.TRANSPOSE);
    za = bj.array(new int[]{34, 44, 54, 44, 58, 72, 54, 72, 90}).reshape(3, 3);
    assertMatrixEquals(za, z);
  }

  @Test
  public void testMul() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    IntArray z = x.mul(2);
    assertMatrixEquals(6, z);
  }

  @Test
  public void testMul1() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    IntArray y = bj.intArray(3, 3).assign(2);
    IntArray z = x.mul(y);
    assertMatrixEquals(6, z);
  }

  @Test
  public void testMul2() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    IntArray y = bj.intArray(3, 3).assign(2);
    IntArray z = x.mul(-1, y, -1);
    assertMatrixEquals(6, z);
  }

  @Test
  public void testAdd() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(2);
    assertMatrixEquals(5, x.add(3));
  }

  @Test
  public void testAdd1() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(2);
    IntArray y = bj.intArray(3, 3).assign(3);
    assertMatrixEquals(5, x.add(y));
  }

  @Test
  public void testAdd2() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(2);
    IntArray y = bj.intArray(3, 3).assign(3);
    assertMatrixEquals(-1, x.add(1, y, -1));
  }

  @Test
  public void testSub() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    assertMatrixEquals(1, x.sub(2));
  }

  @Test
  public void testSub1() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    IntArray y = bj.intArray(3, 3).assign(2);
    assertMatrixEquals(1, x.sub(y));
  }

  @Test
  public void testSub2() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3);
    IntArray y = bj.intArray(3, 3).assign(2);
    assertMatrixEquals(5, x.sub(1, y, -1));
  }

  @Test
  public void testRsub() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(2);
    IntArray y = x.rsub(3);
    assertMatrixEquals(1, y);
  }

  @Test
  public void testDiv() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(4);
    IntArray y = x.div(2);
    assertMatrixEquals(2, y);
  }

  @Test
  public void testDiv1() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(4);
    IntArray y = bj.intArray(3, 3).assign(2);
    IntArray z = x.div(y);
    assertMatrixEquals(2, z);
  }

  @Test
  public void testRdiv() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(6);
    IntArray y = x.rdiv(12);
    assertMatrixEquals(2, y);
  }

  @Test
  public void testNegate() throws Exception {
    IntArray x = bj.intArray(3, 3).assign(3).negate();
    assertMatrixEquals(-3, x);
  }

  @Test
  public void testSlice1() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3}).reshape(3, 2);
    IntArray slice = x.get(bj.range(3));
    assertValuesEquals(bj.array(new int[]{1, 2, 3}), slice);
  }

  @Test
  public void testSlice3() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    IntArray s = x.get(bj.range(0, 3), bj.range(0, 3));
    assertEquals(2, s.rows());
    assertEquals(2, s.columns());
    assertValuesEquals(bj.array(new int[]{1, 1}), s.getRow(0));
    assertValuesEquals(bj.array(new int[]{2, 2}), s.getRow(1));
  }

  @Test
  public void testSlice4() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    IntArray s = x.slice(Arrays.asList(0, 2, 5, 7));
    assertValuesEquals(bj.array(new int[]{1, 3, 3, 2}), s);
  }

  @Test
  public void testSlice6() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    IntArray s = x.slice(Arrays.asList(0, 1), Arrays.asList(0, 1));
    assertValuesEquals(bj.array(new int[]{1, 1}), s.getRow(0));
    assertValuesEquals(bj.array(new int[]{2, 2}), s.getRow(1));
  }

  @Test
  public void testSlice7() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3}).reshape(3, 3);
    BitArray bits =
        bj.array(new boolean[]{true, true, true, false, false, false, false, false, false})
            .reshape(3, 3);
    IntArray s = x.slice(bits);
    assertValuesEquals(bj.array(new int[]{1, 2, 3}), s);
  }

  @Test
  public void testSwap() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3});
    x.swap(0, 2);
    assertValuesEquals(bj.array(new int[]{3, 2, 1}), x);
  }

  @Test
  public void testSetRow() throws Exception {
    IntArray x = bj.intArray(3, 3);
    x.setRow(0, bj.array(new int[]{1, 2, 3}));
    assertValuesEquals(bj.array(new int[]{1, 2, 3}), x.getRow(0));
  }

  @Test
  public void testSetColumn() throws Exception {
    IntArray x = bj.intArray(3, 3);
    x.setColumn(0, bj.array(new int[]{1, 2, 3}));
    assertValuesEquals(bj.array(new int[]{1, 2, 3}), x.getColumn(0));
  }

  @Test
  public void testHashCode() throws Exception {

  }

  @Test
  public void testEquals() throws Exception {

  }

  @Test
  public void testToString() throws Exception {

  }

  @Test
  public void testIterator() throws Exception {
    IntArray x = bj.array(new int[]{1, 2, 3, 4, 5, 6});
    int i = 0;
    for (int v : x.flat()) {
      assertEquals(x.get(i++), v);
    }
  }

  @Test
  public void testStream() throws Exception {
    IntArray m = bj.intArray(3, 3).assign(3);
    IntSummaryStatistics s = m.stream().summaryStatistics();
    assertEquals(3 * 3 * 3, s.getSum());
  }
}
