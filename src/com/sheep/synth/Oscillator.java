package com.sheep.synth;

import com.sheep.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Random;

public class Oscillator extends SynthControlContainer {
    private Waveform waveform = Waveform.Sine;
    private static final double FREQUENCY = 440;
    private final Random random = new Random();
    private int wavePos;

    public Oscillator(Synthesizer synth) {
        super(synth);
        JComboBox<Waveform> comboBox = 
        new JComboBox<>(new Waveform[] {Waveform.Sine, Waveform.Square, Waveform.Saw, Waveform.Triangle, Waveform.Noise});
        comboBox.setSelectedItem(Waveform.Sine);
        comboBox.setBounds(10, 10, 75, 25);
        comboBox.addItemListener(l -> {
            if (l.getStateChange() == ItemEvent.SELECTED) {
                waveform = (Waveform) l.getItem();
            }
        });
        add(comboBox);
        setSize(275, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    private enum Waveform {
        Sine, Square, Saw, Triangle, Noise
    }
    
    public double nextSample() {
        double tDivP = (wavePos++ / (double) Synthesizer.AudioInfo.SAMPLE_RATE) * FREQUENCY; // t/P = t*nu
        switch (waveform) {
            case Sine:
                return Math.sin(2d * Math.PI * tDivP);
            case Square:
                return Math.signum(Math.sin(2d * Math.PI * tDivP));
            case Saw:
                return 2d * (tDivP - Math.floor(0.5d + tDivP));
            case Triangle:
                return 2d * Math.abs(2d * (tDivP - Math.floor(0.5d + tDivP))) - 1;
            case Noise:
                return random.nextDouble();
            default:
                throw new RuntimeException("Oscillator set to unknown waveform");
        }
    }
}
