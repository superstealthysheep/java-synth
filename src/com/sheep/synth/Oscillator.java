package com.sheep.synth;

import com.sheep.synth.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Oscillator extends SynthControlContainer {
    private static final int TONE_OFFSET_LIMIT = 2000;

    private Waveform waveform = Waveform.Sine;
    // private static final double FREQUENCY = 440;
    private final Random random = new Random();
    private double keyFrequency = 440;
    private double frequency;
    private int toneOffset;
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

        JLabel toneParameter = new JLabel(" 0.000");
        toneParameter.setBounds(165, 65, 50, 25);
        toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        toneParameter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit()
                .createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank_cursor");
                setCursor(BLANK_CURSOR);
                mouseClickLocation = e.getLocationOnScreen();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });
        toneParameter.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { // do something better than this
                int dy = e.getYOnScreen() - mouseClickLocation.y; // note: pos y is downward
                if (dy != 0) {
                    if (dy < 0 && toneOffset < TONE_OFFSET_LIMIT) {
                        toneOffset++;
                        // toneOffset -= dy;
                        // toneOffset -= dy*Math.abs(dy)/2; // will need to 
                    } else if (dy > 0 && toneOffset > -TONE_OFFSET_LIMIT) {
                        toneOffset--;
                        // toneOffset -= dy;
                        // toneOffset -= dy*Math.abs(dy)/2;
                    }
                    toneParameter.setText(String.format(" %.3f", getToneOffset()));
                    applyToneOffset();

                }
                Utils.ParameterHandling.PARAMETER_ROBOT.mouseMove(mouseClickLocation.x, mouseClickLocation.y);
            }
        });
        add(toneParameter);

        JLabel toneText = new JLabel("Octave");
        toneText.setBounds(160, 40, 75, 25);
        add(toneText);
        setSize(275, 100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    private enum Waveform {
        Sine, Square, Saw, Triangle, Noise
    }

    public double getKeyFrequency() {
        return keyFrequency;
    }

    public void setKeyFrequency(double keyFrequency) {
        this.keyFrequency = keyFrequency;
        applyToneOffset();
    }
    
    public double getToneOffset() {
        return toneOffset / 1000d;
    }
    
    public double nextSample() {
        double tDivP = (wavePos++ / (double) Synthesizer.AudioInfo.SAMPLE_RATE) * frequency; // t/P = t*nu
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

    private void applyToneOffset() {
        frequency = keyFrequency * Math.pow(2, getToneOffset());
    }
}
