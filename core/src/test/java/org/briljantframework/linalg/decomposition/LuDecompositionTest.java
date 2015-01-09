package org.briljantframework.linalg.decomposition;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.briljantframework.matrix.ArrayDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.junit.Before;
import org.junit.Test;

public class LuDecompositionTest {

  ArrayDoubleMatrix matrix;
  LuDecomposition decomposition;

  @Before
  public void setUp() throws Exception {
    matrix = ArrayDoubleMatrix.of(4, 4, 0, 2, 0, 1, 2, 2, 3, 2, 4, -3, 0, 1., 6, 1, -6, -5);

    decomposition = new LuDecomposer().decompose(matrix);
  }

  @Test
  public void testInverse() throws Exception {
    DoubleMatrix inverse = decomposition.inverse();

    assertEquals(inverse.get(0, 0), -0.026, 0.01);
  }

  @Test
  public void testDeterminant() throws Exception {
    System.out.println(decomposition.getDeterminant());

  }

  @Test
  public void testPivot() throws Exception {
    System.out.println(Arrays.toString(decomposition.getPivot()));

  }

  @Test
  public void testIsNonSingular() throws Exception {

  }

  @Test
  public void testLower() throws Exception {
    System.out.println(decomposition.getLower());
  }

  @Test
  public void testUpper() throws Exception {
    System.out.println(decomposition.getUpper());
  }
}
