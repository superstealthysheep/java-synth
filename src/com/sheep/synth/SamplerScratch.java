package com.sheep.synth;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.annotation.processing.SupportedSourceVersion;
import javax.swing.SwingWorker.StateValue;
import javax.xml.stream.events.StartDocument;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
// import org.lwjgl.stb.*;
// import org.lwjgl.system.*;

// crazy memory management stuff is from https://github.com/LWJGL/lwjgl3/blob/master/modules/lwjgl/core/src/main/java/org/lwjgl/system/MemoryStack.java#L914

public class SamplerScratch implements AutoCloseable {
    private final AudioTrack track = new AudioTrack(); // for now, initialize it here :/

    public class AudioTrack implements AutoCloseable {
        private final long handle;
        private final int channels;
        private final int sampleRate;
        private final int samplesLength;
        private final float samplesSec;
        public int sampleIndex;
        public float index;
        // private final short[] sampleTable; // just hold onto these in a java array that we can randomly index into. can optimize later
        private final ShortBuffer sampleTable;


        public AudioTrack() {
            // load ogg
            try (MemoryStack stack = MemoryStack.stackPush()) { // for scoping
                IntBuffer error = stack.mallocInt(1); // stuff gets freed when exit try block
                
                // Goal: open ogg vorbis file into memory as "a big wavetable" that we can stride through
                // Q: do we want this big wavetable to be a regular Java array, or do we want it to be the lower-level lwjgl native array thing?
                // handle = STBVorbis.stb_vorbis_open_filename("../sounds/meow.ogg", error, null);
                handle = STBVorbis.stb_vorbis_open_filename("sounds/meow.ogg", error, null); // not sure what alloc_buffer null does
                if (handle == MemoryUtil.NULL) {
                    throw new RuntimeException("Error opening ogg vorbis. Error: " + error.get(0));
                }
                STBVorbisInfo info = STBVorbisInfo.malloc(stack);
                // print(info);

                STBVorbis.stb_vorbis_get_info(handle, info);
                this.channels = info.channels();
                this.sampleRate = info.sample_rate();
                System.out.println("Channels: " + this.channels);
                System.out.println("Sample rate: " + this.sampleRate);
            }

            this.samplesLength = STBVorbis.stb_vorbis_stream_length_in_samples(handle);
            this.samplesSec = STBVorbis.stb_vorbis_stream_length_in_seconds(handle);
            this.sampleIndex = 0;
            this.sampleTable = MemoryUtil.memAllocShort(samplesLength);
            STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, channels, sampleTable);

            // // qwfdfasdf
            // sampleTable = new short[samplesLength];
            // for (int i = 0; i < samplesLength; i++) {
            //     sampleTable[i]
            // }

        }    

        @Override
        public void close() {
            STBVorbis.stb_vorbis_close(handle);
        }

        // assumes mono audio for now
        public short fetchSample(int index) {
            if (index % 10000 == 0) System.out.println(index);
            return sampleTable.get(index);
        }

        public short fetchInterpolatedSample(float index) {
            int x = (int) index;
            float right_fraction = index - x;
            float res = 0;
            res = (1-right_fraction) * fetchSample(x) + right_fraction * fetchSample((x + 1) % samplesLength);
            return (short) res;
        }

    }

    public final AudioThread audioThread = new AudioThread(() -> {
        // if (!shouldGenerate) {
        //     return null;
        // }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i < AudioThread.BUFFER_SIZE; ++i) {
            double d = 0;
            // for (Oscillator o : oscillators) {
                // d += track.fetchSample(track.sampleIndex);
            // }
            // s[i] = (short)(Short.MAX_VALUE * d); // divide to normalize when mixing
            // s[i] = track.fetchSample(track.sampleIndex);
            // s[i] = d;
            float speed = 0.5f;
            s[i] = track.fetchInterpolatedSample(track.index);
            track.index = (track.index + speed) % track.samplesLength;
            // track.sampleIndex = (track.sampleIndex + 1) % track.samplesLength; // TODO: account for mismatched sample rates
        }
        return s;
    });

    public SamplerScratch() {
        // this.track = new AudioTrack();
    }

    @Override
    public void close() {
        track.close();
    }

    

    public static void main(String[] args) {
        // private 
        SamplerScratch samp = new SamplerScratch();
        samp.audioThread.triggerPlayback();
        // samp.play();

    } 
}
