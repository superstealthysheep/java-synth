package com.sheep.synth.utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.Color;
        
import static java.lang.Math.PI;
        
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

    public static class WindowDesign {
        public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    }

    public static class Math {
        public static double frequencyToAngularFrequency(double freq) {
            return 2 * PI * freq;
        }

        // public static double frequencyToRadPerSample(double freq) {
        //     return 2 * PI * freq / Synthesizer.AudioInfo.SAMPLE_RATE;
        // }
    }
    
}
