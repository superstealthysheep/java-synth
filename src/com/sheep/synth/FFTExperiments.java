package com.sheep.synth;
import org.jtransforms.fft.FloatFFT_1D;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public record FFTExperiments() {
    public static void printComplexArray(float[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            float mag = (float) Math.sqrt(arr[2*i]*arr[2*i]+arr[2*i+1]*arr[2*i+1]);
            float phase = (float) (Math.atan2(arr[2*i+1], arr[2*i]) / (2 * Math.PI));
            if (Math.abs(arr[2*i]) < .001 && Math.abs(arr[2*i+1]) < .001) phase = Float.NaN;
            System.out.println(String.format("%3d: %.3f + %.3f*i --- %.3f: %.3f", i, arr[2*i], arr[2*i+1], mag, phase));
        }
    }

    public static float[] resampleComplexArray(float[] arr, int crunchFactor) {
        int inputSize = arr.length / 2;
        int outputSize = (inputSize / crunchFactor) + (inputSize % crunchFactor != 0 ? 1 : 0);
        float[] output = new float[2*outputSize];
        int inIdx = 0;
        for (int outIdx = 0; outIdx < outputSize; outIdx++) {
            float reAccum = 0;
            float imAccum = 0;
            int chunkSize = Integer.min(crunchFactor, inputSize - inIdx);
            for (int i = 0; i < chunkSize; i++) {
                float re = arr[2*inIdx];
                float im = arr[2*inIdx+1];
                // reAccum += arr[2*inIdx];
                // imAccum += arr[2*inIdx+1];
                reAccum += Math.sqrt(re*re+im*im); // just sum up the magnitudes and see what happens
                inIdx++;
            }
            output[2*outIdx] = reAccum / chunkSize;
            output[2*outIdx+1] = imAccum / chunkSize;
        }
        return output;
    }

    public static void main(String[] args) {
        AudioTrack track = new AudioTrack("sounds/bell.ogg");

        int arr_len = 1024;       
        // if sample rate is 44100, 4096 samples corresponds to a window of length 1/10 of a second, so the lowest possible freq is 20 Hz
        // one period of 440 Hz is ~100 samples long

        FloatFFT_1D fft = new FloatFFT_1D(arr_len);
        float[] input = new float[arr_len*2];
        // input[2*8] = 1;
        // for (int i = 0; i < arr_len; i++) {
        //     // input[2*i] = -7+2*(i%8);
        //     // input[2*i] = 2*(i%2)-1;
        //     // input[2*i] = 2*(i%4)-3;
        //     input[2*i] = (float) Math.sin(2*Math.PI*i/8);
        // }
        for (int i = 0; i < arr_len; i++) {
            input[2*i] = track.fetchSample(i);
        }

        System.out.println("Time:");
        printComplexArray(input);
        // printComplexArray(resampleComplexArray(input, 8));
        displayFFT(-1, -1, input, -1, 10);

        fft.complexForward(input);
        System.out.println("\nFreq:");
        printComplexArray(input);
        displayFFT(-1, -1, input, -1, 1500);
        // printComplexArray(resampleComplexArray(input, 8));

        // fft.complexInverse(input, true);
        // System.out.println("\nTime:");
        // printComplexArray(input);

        // displayFFT(100, 100, null, 100);
    }   

    public static void displayFFT(int width, int height, float[] fft, int barCount, int verticalScale) {
        JFrame frame = new JFrame("Ligma fft");
        frame.setSize(fft.length/2+50, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // frame.setResizable(false);
        frame.setVisible(true);
        // JPanel panel = new JPanel();
        
        // frame.add(panel);

        // I am very tired and lazy
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("aw shucks");
        }   

        Graphics g = frame.getGraphics();
        g.setColor(new Color(0, 100, 100));
        // while (true) {
           
        int inputSize = fft.length / 2;
        for (int i = 0; i < inputSize; i++) {
            float re = fft[2*i];
            float im = fft[2*i];
            float magnitude = (float) Math.sqrt(re*re + im*im);
            g.fillRect(i, 0, 1, (int) magnitude / verticalScale);
        }
        // g.fillRect(50, 50, 100, 100);
        // }
        // frame.pack();
        // frame.setVisible(true);

    }
}
