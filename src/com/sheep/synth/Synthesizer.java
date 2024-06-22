package com.sheep.synth;

import com.sheep.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;

public class Synthesizer {
    private boolean shouldGenerate;

    // private static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
    private static final HashMap<Integer, Double> KEY_FREQUENCIES = new HashMap<>();
    private final Oscillator[] oscillators = new Oscillator[3];
    private final AudioSource[] audioSources = new AudioSource[3];
    private final JFrame frame = new JFrame("Synthesizer");
    private HashSet<Integer> keysPressed = new HashSet<>(); // could just do an array but will keep this for flexibility

    private final AudioThread audioThread = new AudioThread(() -> {
        if (!shouldGenerate) {
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i < AudioThread.BUFFER_SIZE; ++i) {
            double d = 0;
            for (AudioSource as : audioSources) {
                d += as.nextSample();
            }
            s[i] = (short)(Short.MAX_VALUE * d / audioSources.length); // divide to normalize
        }
        return s;
    });

    private final KeyAdapter keyAdapter = new KeyAdapter() { // what is this syntax?? :0
        @Override
        public void keyPressed(KeyEvent e) {
            for (AudioSource as : audioSources) {
                // char k = e.getKeyChar();
                int k = e.getKeyCode();
                keysPressed.add(k);
                if (KEY_FREQUENCIES.containsKey(k)) {
                    as.setBaseFrequency(KEY_FREQUENCIES.get(k));
                } // else if (k == '/') {
                // }
            }
            if (!audioThread.isRunning()) {
                shouldGenerate = true;
                audioThread.triggerPlayback();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            keysPressed.remove(e.getKeyCode());
            if (keysPressed.size() == 0) {
                for (AudioSource as : audioSources) {
                    as.seekStart();
                }
                shouldGenerate = false;
            }
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
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(KEYS[i]);
            KEY_FREQUENCIES.put(keyCode, Utils.Tuning.midiToFrequency(i + STARTING_KEY));
        }
    }

    Synthesizer() { // what is this syntax.. constructor ig without access keyword?
        int oscillatorY = 0;
        //     {Oscillator o = new Oscillator(this);
        //     o.setLocation(5, oscillatorY);
        //     audioSources[0] = o;
        //     frame.add(o); // TODO: NEED to find a better way to decouple UI and sound logic
        //     oscillatorY += 105;}
        for (int i = 0; i < oscillators.length; ++i) {
            Sampler o = new Sampler(this);
            o.setLocation(5, oscillatorY);
            audioSources[i] = o;
            frame.add(o); // TODO: NEED to find a better way to decouple UI and sound logic
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