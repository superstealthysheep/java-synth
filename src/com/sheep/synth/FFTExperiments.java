package com.sheep.synth;
import org.jtransforms.fft.FloatFFT_1D;
import java.util.Arrays;

public record FFTExperiments() {
    public static void printComplexArray(float[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            float mag = (float) Math.sqrt(arr[2*i]*arr[2*i]+arr[2*i+1]*arr[2*i+1]);
            float phase = (float) (Math.atan2(arr[2*i+1], arr[2*i]) / (2 * Math.PI));
            if (Math.abs(arr[2*i]) < .001 && Math.abs(arr[2*i+1]) < .001) phase = Float.NaN;
            System.out.println(String.format("%3d: %.3f + %.3f*i --- %.3f: %.3f", i, arr[2*i], arr[2*i+1], mag, phase));
        }
    }
    public static void main(String[] args) {
        int arr_len = 256;       

        FloatFFT_1D fft = new FloatFFT_1D(arr_len);
        float[] input = new float[arr_len*2];
        // input[2*8] = 1;
        for (int i = 0; i < arr_len; i++) {
            // input[2*i] = -7+2*(i%8);
            // input[2*i] = 2*(i%2)-1;
            // input[2*i] = 2*(i%4)-3;
            input[2*i] = (float) Math.sin(2*Math.PI*i/8);
        }

        System.out.println("Time:");
        printComplexArray(input);

        fft.complexForward(input);
        System.out.println("\nFreq:");
        printComplexArray(input);

        fft.complexInverse(input, true);
        System.out.println("\nTime:");
        printComplexArray(input);
    }   
}
