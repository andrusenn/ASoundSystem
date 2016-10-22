package hidensource.asoundsystem;

import beads.*;

public class ASound {
	private float freq = 440.f;
	private Sample sample;
	private Glide glide_fq;
	private Glide glide_gain;
	private Glide glide_gain_verb;
	private Glide glide_pan;
	private Glide glide_modFq;
	private Gain gain;
	private Gain gain_verb;
	private Panner panner;
	private SamplePlayer player;
	private WavePlayer fqWave;
	private final int PLAYER = 1;
	private final int WT = 2;
	private int soundType = PLAYER;

	private float volume = 0.8f;
	
	// Modulations
	private WavePlayer fqModulator;
	private WavePlayer fqCarrier;
	private Glide glide_fqMod;
	private Glide glide_fqMult;

	// Wave Table Types
	/*
	 * Sinusoidal Wave
	 */
	public static final Buffer SINE = Buffer.SINE;
	public static final Buffer SQUARE = Buffer.SQUARE;
	public static final Buffer NOISE = Buffer.NOISE;
	public static final Buffer TRIANGLE = Buffer.TRIANGLE;
	public static final Buffer SAW = Buffer.SAW;

	// FX
	private Reverb reverb;
	private Compressor compressor;
	private AudioContext context;
	private ASoundSystem ss;

	/**
	 * Constructor
	 * 
	 * @param _ss
	 *            ASoundSystem
	 * @param _path_sample
	 *            String Path to sample file
	 */
	public ASound(ASoundSystem _ss, String _path_sample) {
		soundType = PLAYER;
		try {
			sample = new Sample(_path_sample);
		} catch (Exception e) {
			//
		}
		ss = _ss;
		context = ss.getContext();
		player = new SamplePlayer(context, sample);
		player.setKillOnEnd(false);
		// sample = player.getSample();

		// FX Reverb
		reverb = new Reverb(context, 2);
		reverb.setSize(0.3f);
		reverb.setDamping(0.1f);

		// FX Compress
		compressor = new Compressor(context, 1);
		compressor.setAttack(99999); // the attack is how long it takes for
		// compression to ramp up, once the
		// threshold is crossed
		compressor.setDecay(0);
		compressor.setRatio(0.0f);
		compressor.setThreshold(1.0f);

		glide_gain = new Glide(context, volume, 20);
		gain = new Gain(context, 1, glide_gain);

		glide_gain_verb = new Glide(context, 0.8f, 20);
		gain_verb = new Gain(context, 2, glide_gain_verb);

		glide_pan = new Glide(context, 0.0f, 20);

		panner = new Panner(context, glide_pan);
		// Compress
		compressor.addInput(player);
		// 2 Gain DRY / WET
		// Manejo por separado
		gain.addInput(compressor);
		gain_verb.addInput(compressor);
		reverb.addInput(gain_verb);
		//
		panner.addInput(gain);
		// Add to System
		ss.addInput(panner);
	}

	/**
	 * WaveTable Constructor
	 * 
	 * @param _ss
	 *            ASoundSystem
	 * @param _fq
	 *            int frequency
	 * @param _type
	 *            Buffer wave type (SINE/SQUARE/SAW/TRIANGLE/NOISE)
	 */
	public ASound(ASoundSystem _ss, int _fq, Buffer _type) {
		soundType = WT;
		freq = _fq;
		ss = _ss;
		context = ss.getContext();
		// player = new SamplePlayer(context, sample);
		// player.setKillOnEnd(false);
		// sample = player.getSample();

		// FX Reverb
		reverb = new Reverb(context, 2);
		reverb.setSize(0.3f);
		reverb.setDamping(0.1f);

		// FX Compress
		compressor = new Compressor(context, 1);
		compressor.setAttack(99999); // the attack is how long it takes for
		// compression to ramp up, once the
		// threshold is crossed
		compressor.setDecay(0);
		compressor.setRatio(0.0f);
		compressor.setThreshold(1.0f);

		glide_fq = new Glide(context, freq, 20);
		fqWave = new WavePlayer(context, glide_fq, _type);

		glide_gain = new Glide(context, 0.8f, 20);
		gain = new Gain(context, 1, glide_gain);

		glide_gain_verb = new Glide(context, 0.8f, 20);
		gain_verb = new Gain(context, 2, glide_gain_verb);

		glide_pan = new Glide(context, 0.0f, 20);

		panner = new Panner(context, glide_pan);
		// Compress
		compressor.addInput(fqWave);

		// 2 Gain DRY / WET
		// Manejo por separado
		gain.addInput(compressor);
		gain_verb.addInput(compressor);
		reverb.addInput(gain_verb);
		panner.addInput(gain);
		// Add to System
		ss.addInput(panner);
	}

	/**
	 * WaveTable Constructor
	 * 
	 * @param _ss
	 *            ASoundSystem
	 * @param _fq
	 *            int frequency
	 * @param _type
	 *            Buffer wave type (SINE/SQUARE/SAW/TRIANGLE/NOISE)
	 */
	// TODO sacar este constructor e implementar metodos que afecten al constructor de Wavetables
	public ASound(ASoundSystem _ss, int _fqMod, int _fqMult, Buffer _typeCar) {
		soundType = WT;
		//freq = _fqMod;
		ss = _ss;
		context = ss.getContext();
		// player = new SamplePlayer(context, sample);
		// player.setKillOnEnd(false);
		// sample = player.getSample();

		// FX Reverb
		reverb = new Reverb(context, 2);
		reverb.setSize(0.3f);
		reverb.setDamping(0.1f);

		// FX Compress
		compressor = new Compressor(context, 1);
		compressor.setAttack(99999); // the attack is how long it takes for
		// compression to ramp up, once the
		// threshold is crossed
		compressor.setDecay(0);
		compressor.setRatio(0.0f);
		compressor.setThreshold(1.0f);

		glide_fqMod = new Glide(context, _fqMod, 20);
		fqModulator = new WavePlayer(context, glide_fqMod, Buffer.SINE);
		//glide_fqMult = new Glide(context, _fqMult, 20);

		// Modulations -----------------------------------------------
		// glide_modFq = new Glide(context, 20, 20);
		// fqModulator = new WavePlayer(context, glide_modFq, Buffer.SINE);
		// this is a custom function
		// custom functions are a bit like custom Unit Generators (custom Beads)
		// but they only override the calculate function
		Function frequencyModulation = new Function(fqModulator) {
			public float calculate() {
				// return x[0], which is the original value of the modulator
				// signal (a sine wave)
				// multiplied by 200.0 to make the sine vary between -200 and
				// 200
				// the number 200 here is called the "Modulation Index"
				// the higher the Modulation Index, the louder the sidebands
				// then add mouseY, so that it varies from mouseY - 200 to
				// mouseY + 200
				return (x[0] * _fqMult);
			}
		};
		fqCarrier = new WavePlayer(context, frequencyModulation, _typeCar);
		// --------------------------------------------------------------
		glide_gain = new Glide(context, 0.8f, 20);
		gain = new Gain(context, 1, glide_gain);

		glide_gain_verb = new Glide(context, 0.8f, 20);
		gain_verb = new Gain(context, 2, glide_gain_verb);

		glide_pan = new Glide(context, 0.0f, 20);

		panner = new Panner(context, glide_pan);
		// Compress
		compressor.addInput(fqCarrier);

		// 2 Gain DRY / WET
		// Manejo por separado
		gain.addInput(compressor);
		gain_verb.addInput(compressor);
		reverb.addInput(gain_verb);
		panner.addInput(gain);
		// Add to System
		ss.addInput(panner);
	}

	/**
	 * Add reverberation
	 * 
	 * @see mixReverb
	 * 
	 */
	public void reverb() {
		// Reset input / prevent various inputs
		// panner.removeAllConnections(reverb);
		ss.addInput(reverb);
	}

	/**
	 * Remove reverb input
	 */
	public void noReverb() {
		ss.removeInput(reverb);
	}

	/**
	 * Set frequency
	 * 
	 * @param _fq
	 *            Set frequency
	 */
	public void setFq(int _fq) {
		freq = _fq;
		glide_fq.setValue(freq);
	}

	/**
	 * Set frequency
	 * 
	 * @param _fq
	 *            Set frequency
	 * @param _time
	 *            Set time
	 */
	public void setFq(int _fq, float _time) {
		freq = _fq;
		glide_fq.setGlideTime(_time);
		glide_fq.setValue(freq);
	}

	/**
	 * 
	 * @param _pan
	 *            (-1.0 Left to 1.0 Right)
	 */
	public void setPan(float _pan) {
		glide_pan.setValue(_pan);
	}
	public void setFreqMod(int _fqMod){
		glide_fqMod.setValue(_fqMod);
	}
	/**
	 * 
	 * @param _vol
	 *            float (0.0 to 1.0) volume (gain)
	 */
	public void setVol(float _vol) {
		volume = _vol;
		glide_gain.setValue(volume);
	}

	/**
	 * 
	 * @param _dry
	 *            float set dry effect value (0.0 to 1.0)
	 * @param _wet
	 *            float set wet effect value (0.0 to 1.0)
	 * 
	 */
	public void mixReverb(float _dry, float _wet) {
		glide_gain_verb.setValue(_wet);
		glide_gain.setValue(_dry);
	}

	/**
	 * 
	 * @return float duration of sample in seconds
	 */
	public float duration() {
		return (float) sample.getLength();
	}

	/**
	 * 
	 * @return
	 */
	public Sample getSample() {
		return sample;
	}

	public void play() {
		if (soundType == PLAYER)
			player.start(0.0f);
		// if(soundType == WT) player.start(0.0f);
	}

	public void play(float _to) {
		player.start(_to);
	}

	public void pause() {
		player.pause(true);
	}

	public Reverb getFXReverb() {
		return reverb;
	}

	public Compressor getFXCompressor() {
		return compressor;
	}
}
