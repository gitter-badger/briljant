package org.briljantframework.matrix.base

import spock.lang.Specification

/**
 * Created by isak on 01/06/15.
 */
class BaseMatrixRoutinesSpec extends Specification {

    static bj = new BaseMatrixFactory()
    static bjr = new BaseMatrixRoutines()

    def "mean of double matrices should return the mean"() {
        expect:
        bjr.mean(a) == b

        where:
        a << [
                bj.matrix([1, 2, 3, 4, 5, 6] as double[]),
                bj.matrix([-1, -2, -3, -4, -5, -6] as double[])
        ]
        b << [3.5, -3.5]
    }




}