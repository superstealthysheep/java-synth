package com.sheep.synth;

public enum Wavetable {
    Sine, Square, Saw, Triangle;

    static final int SIZE = 64;

    private final float[] samples = new float[SIZE];

    static {
        // fill table with one wave period
        for (int i = 0; i < SIZE; i++) {
            double tDivP = (double) i / SIZE; // fraction through one period of the wave
            Sine.samples[i] = (float) Math.sin(2 * Math.PI * tDivP);
            Square.samples[i] = Math.signum(Sine.samples[i]);
            Saw.samples[i] = (float) (2d * (tDivP - Math.floor(0.5 + tDivP)));
            Triangle.samples[i] = (float) (2d * Math.abs(Saw.samples[i]) - 1d);
        }
    }

    float[] getSamples() {
        return samples;
    }
}
