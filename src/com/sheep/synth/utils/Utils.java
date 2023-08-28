package com.sheep.synth.utils;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
        
public class Utils {
    public static void handleProcedure(Procedure procedure, boolean printStackTrace) {
        try {
            procedure.invoke();
        } catch (Exception e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }

    public static class ParameterHandling {
        public static final Robot PARAMETER_ROBOT;
        static {
            try {
                PARAMETER_ROBOT = new Robot();
            } catch (AWTException e) {
                throw new ExceptionInInitializerError("Cannot construct robot instance");
            }
        }
    }

    public static class WindowDesign {
        public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    }

    // public static class Math {
    //     public static double frequencyToAngularFrequency(double freq) {
    //         return 2 * PI * freq;
    //     }

    //     // public static double frequencyToRadPerSample(double freq) {
    //     //     return 2 * PI * freq / Synthesizer.AudioInfo.SAMPLE_RATE;
    //     // }
    // }

    public static class Tuning {
        // public static final double twelfthRootTwo = java.lang.Math.pow(2, 1d/12);
        public static double A4_FREQ = 440d;
        public static double midiToFrequency(int noteNum) {
            return A4_FREQ * java.lang.Math.pow(2, (double) (noteNum-69)/12);
        }
    }
    
}
