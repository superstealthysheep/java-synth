package com.sheep.synth;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class AudioTrack implements AutoCloseable {
    private final long handle;
    public final int channels;
    public final int sampleRate;
    public final int samplesLength;
    // private final float samplesSec;
    // public float sampleIndex;
    // public float index;
    private final ShortBuffer sampleTable; // just hold the whole file as one chunk


    public AudioTrack(String oggPath) {
        // load ogg
        try (MemoryStack stack = MemoryStack.stackPush()) { // for scoping
            IntBuffer error = stack.mallocInt(1); // stuff gets freed when exit try block
            
            // Goal: open ogg vorbis file into memory as "a big wavetable" that we can stride through
            // Q: do we want this big wavetable to be a regular Java array, or do we want it to be the lower-level lwjgl native array thing?
            // handle = STBVorbis.stb_vorbis_open_filename("../sounds/meow.ogg", error, null);
            handle = STBVorbis.stb_vorbis_open_filename(oggPath, error, null); // not sure what alloc_buffer null does
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
        // this.samplesSec = STBVorbis.stb_vorbis_stream_length_in_seconds(handle);
        // this.sampleIndex = 0;
        this.sampleTable = MemoryUtil.memAllocShort(samplesLength);
        STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, channels, sampleTable);

    }    

    @Override
    public void close() {
        STBVorbis.stb_vorbis_close(handle);
    }

    // assumes mono audio for now
    public short fetchSample(int index) {
        // if (index % 10000 == 0) System.out.println(index);
        return sampleTable.get(index);
    }

    // public short fetchInterpolatedSample(float index) {
    //     int x = (int) index;
    //     float right_fraction = index - x;
    //     float res = 0;

    //     res = (1-right_fraction) * fetchSample(x) + right_fraction * fetchSample((x + 1) % samplesLength);
    //     return (short) res;
    // }
}