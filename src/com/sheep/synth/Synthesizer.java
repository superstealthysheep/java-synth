package com.sheep.synth;

import com.sheep.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class Synthesizer {
    private boolean shouldGenerate;

    private static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
    private final Oscillator[] oscillators = new Oscillator[3];
    private final JFrame frame = new JFrame("Synthesizer");
    private final AudioThread audioThread = new AudioThread(() -> {
        if (!shouldGenerate) {
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i < AudioThread.BUFFER_SIZE; ++i) {
            double d = 0;
            for (Oscillator o : oscillators) {
                d += o.nextSample();
            }
            s[i] = (short)(Short.MAX_VALUE * d / oscillators.length); // divide to normalize
        }
        return s;
    });

    private final KeyAdapter keyAdapter = new KeyAdapter() { // what is this syntax?? :0
        @Override
        public void keyPressed(KeyEvent e) {
            for (Oscillator o : oscillators) {
                char k = e.getKeyChar();
                if (KEY_FREQUENCIES.containsKey(k)) {
                    o.setBaseFrequency(KEY_FREQUENCIES.get(k));
                }
            }
            if (!audioThread.isRunning()) {
                shouldGenerate = true;
                audioThread.triggerPlayback();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            shouldGenerate = false;
        }
    };

    public KeyAdapter getKeyAdapter() {
        return keyAdapter;
    }

    static { // anonymous?
        final int STARTING_KEY = 48; // correspond to pitches according to MIDI spec. 48 = C3
        // final int KEY_FREQUENCY_INCREMENT = 1;
        final char[] KEYS = "zsxdcvgbhnjmq2w3er5t6y7ui9o0p[=]".toCharArray();
        for (int i = 0; i < KEYS.length; i++) {
            KEY_FREQUENCIES.put(KEYS[i], Utils.Tuning.midiToFrequency(i + STARTING_KEY));
        }
    }

    Synthesizer() { // what is this syntax.. constructor ig without access keyword?
        int oscillatorY = 0;
        for (int i = 0; i < oscillators.length; ++i) {
            oscillators[i] = new Oscillator(this);
            oscillators[i].setLocation(5, oscillatorY);
            frame.add(oscillators[i]);
            oscillatorY += 105;
        }

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); //  clojure beckons
        
        frame.addKeyListener(keyAdapter);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audioThread.close();
            }
        });
    }

    public static class AudioInfo {
        public static final int SAMPLE_RATE = 44100;
    }
}