package hidensource.asoundsystem;

import beads.*;
import java.util.HashMap;
/**
 * Simple Processing library for sound manipulations
 * 
 * Some methods ready to use
 * 
 * @author Andru
 *
 */
public class ASoundSystem {
	private AudioContext context;
	private Gain gain;
	private Glide glid_gain;
	private Compressor compressor;
	private HashMap<String, Float> properties;

	private float volume = 0.5f;

	public static final String VERSION = "0.1a (Alpha version)";
	public static final String UPDATE = "20161013";

	/**
	 * Constructor
	 */
	public ASoundSystem() {
		context = new AudioContext();
		glid_gain = new Glide(context, volume, 20);
		gain = new Gain(context, 2, glid_gain);
		properties = new HashMap<String, Float>();

		// FX Compressor
		properties.put("FXCompress-attack", 99999.0f);
		properties.put("FXCompress-decay", 0.0f);
		properties.put("FXCompress-ratio", 0.0f);
		properties.put("FXCompress-threshold", 1.0f);
		compressor = new Compressor(context, 2);
		System.out.println("ASounSystem - Version " + VERSION);
		updateProp();
	}

	public void setProp(String _prop, float _val) {
		properties.put(_prop, new Float(_val));
		updateProp();
	}

	private void updateProp() {
		compressor.setAttack(properties.get("FXCompress-attack"));
		compressor.setDecay(properties.get("FXCompress-decay"));
		compressor.setRatio(properties.get("FXCompress-ratio"));
		compressor.setThreshold(properties.get("FXCompress-threshold"));
	}

	public Compressor getFXCompressor() {
		return compressor;
	}

	/**
	 * Get AudioContext (beads)
	 * 
	 * @return AudioContext
	 */
	public AudioContext getContext() {
		return context;
	}

	protected void addInput(Panner _p) {
		gain.addInput(_p);
	}

	protected void addInput(Reverb _p) {
		gain.addInput(_p);
	}

	protected void removeInput(Panner _p) {
		gain.removeAllConnections(_p);
	}

	protected void removeInput(Reverb _p) {
		gain.removeAllConnections(_p);
	}

	/**
	 * Get version
	 * 
	 * @return String
	 */
	public String version() {
		return VERSION + " - " + "Update " + UPDATE;
	}

	/**
	 * Set master volume
	 * 
	 * @param _vol
	 *            set volume (0.0 to 1.0)
	 */
	public void setVol(float _vol) {
		volume = _vol;
		glid_gain.setValue(volume);
	}

	/**
	 * Set master volume
	 * 
	 * @param _vol
	 *            set volume (0.0 to 1.0)
	 * @param _time
	 *            set time to reach the value in millis
	 */
	public void setVol(float _vol, int _time) {
		volume = _vol;
		glid_gain.setValue(_time);
		glid_gain.setValue(volume);
	}

	/**
	 * Start AudioContext
	 */
	public void start() {
		compressor.addInput(gain);
		context.out.addInput(compressor);
		context.start();
	}
}
