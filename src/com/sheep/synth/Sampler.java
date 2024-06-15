package com.sheep.synth;

import com.sheep.synth.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
// import java.util.Random;

public class Sampler extends SynthControlContainer implements AudioSource {
    private static final int TONE_OFFSET_LIMIT = 2000;

    private double baseFrequency;
    private double frequency; // true frequency once toneOffset is applied
    private int toneOffset; // logarithmic-scale pitch offset. 1000 is one octave
    private AudioTrack wavetable;
    private double wavetablePos;
    private double wavetableStepSize;

    public Sampler(Synthesizer synth) {
        super(synth);

        wavetable = new AudioTrack("sounds/meow.ogg");
        // JComboBox<Waveform> comboBox = new JComboBox<>(Waveform.values());
        // comboBox.setSelectedItem(Waveform.Sine);
        // comboBox.setBounds(10, 10, 75, 25);
        // comboBox.addItemListener(l -> {
        //     if (l.getStateChange() == ItemEvent.SELECTED) {
        //         setWaveform((Waveform) l.getItem());
        //     }
        // });
        // add(comboBox);

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
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toneOffset = 0;
                    toneParameter.setText(String.format(" %.3f", getToneOffset()));
                    applyToneOffset();
                }
            }
        });
        toneParameter.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { // TODO: experiment with different functions mapping dy to toneOffset increment
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

    public double getBaseFrequency() {
        return baseFrequency;
    }

    public void setBaseFrequency(double baseFrequency) {
        this.baseFrequency = baseFrequency;
        applyToneOffset();
    }
    
    public double getToneOffset() {
        return toneOffset / 1000d;
    }
    
    private void applyToneOffset() {
        frequency = baseFrequency * Math.pow(2, getToneOffset());
        // double multiplier = Math.pow(2, getToneOffset());

        // wavetableStepSize = frequency / Synthesizer.AudioInfo.SAMPLE_RATE * wavetable.samplesLength; // TODO: check dim analysis
        wavetableStepSize = frequency / 440 * wavetable.sampleRate / Synthesizer.AudioInfo.SAMPLE_RATE; // TODO: check dim analysis
    }
    
    public double nextSample() {
        return nextWavetableSample();
    }

    public double nextWavetableSample() {
        // linearly interpolate wavetable entries for sample
        int left = (int) wavetablePos;
        int right = (left + 1) % wavetable.samplesLength;
        double frac = wavetablePos - left;
        wavetablePos += wavetableStepSize;
        // wavetablePos += 1;
        wavetablePos %= wavetable.samplesLength;
        short samp_l = wavetable.fetchSample(left);
        short samp_r = wavetable.fetchSample(right);

        return (double) (samp_l + frac * (samp_r - samp_l)) / Short.MAX_VALUE;
    }
}
