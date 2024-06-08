Project seeded by [G223 Productions video](https://www.youtube.com/watch?v=q09cNItGhLQ&t=1965s)
### Libraries
- LWJGL OpenAL Binding (3.3.2)
- LWJGL STB Binding (3.1.4) (i hope this is compatible)

Future ideas:
- Add eq (get to implement fft :))
- Add envelopes
- Midi input

Goal at the moment: add samples
How will I do this? 
- Load an audio file (e.g. ogg vorbis) into a buffer
- Use this buffer as a wave table.
    - Note: this buffer will have MANY more samples than our previous wave tables, since the period of the sample is much longer than a single wave period
    - This behavior is also different from other waveforms in other ways:
        - Ideally want meow to start *when key is pressed*. Currently, each oscillator is "constantly running" through the wavetable, but when we press a key, we "unmute" it. If we did the same thing with a meow song, there is no guarantee that the keypress will sync up with the start of the meow.
        - So perhaps the meow should not be an oscillator, then. 


