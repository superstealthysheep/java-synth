package com.sheep.synth;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Synthesizer {
    // temporary
    private boolean shouldGenerate;
    private int wavePos;

    private final JFrame frame = new JFrame("Synthesizer");
    private final AudioThread audioThread = new AudioThread(() -> {
        if (!shouldGenerate) {
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i < AudioThread.BUFFER_SIZE; ++i) {
            s[i] = (short)(Short.MAX_VALUE * Math.sin((2 * Math.PI * 550) / AudioInfo.SAMPLE_RATE * wavePos++));
        }
        return s;
    });

    Synthesizer() { // what is this syntax.. constructor ig without access keyword?
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); //  clojure beckons
        frame.addKeyListener(new KeyAdapter() { // what is this syntax?? :0
            @Override
            public void keyPressed(KeyEvent e) {
                if (!audioThread.isRunning()) {
                    shouldGenerate = true;
                    audioThread.triggerPlayback();
                }
                // shouldGenerate=true;
                // audioThread.triggerPlayback();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                shouldGenerate = false;
            }
        });
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