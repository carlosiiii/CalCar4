package com.cigsa.carlos.calcar4;

import android.test.InstrumentationTestCase;

/**
 * Created by Carlos on 20/04/2016.
 */
public class ExampleTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
