package com.sheep.synth;

public interface AudioSource {
    public double nextSample();
    public void setBaseFrequency(double baseFrequency);
    public void seekStart();
}
