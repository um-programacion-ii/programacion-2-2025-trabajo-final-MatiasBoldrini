package com.eventos.pf.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SesionCompraTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SesionCompra getSesionCompraSample1() {
        return new SesionCompra().id(1L).pasoActual(1);
    }

    public static SesionCompra getSesionCompraSample2() {
        return new SesionCompra().id(2L).pasoActual(2);
    }

    public static SesionCompra getSesionCompraRandomSampleGenerator() {
        return new SesionCompra().id(longCount.incrementAndGet()).pasoActual(intCount.incrementAndGet());
    }
}
