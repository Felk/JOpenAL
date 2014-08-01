package de.felk.JOpenAL;

public class SoundCategory {

	private float gain = 1f;
	public static final SoundCategory MUSIC = new SoundCategory(1f);
	public static final SoundCategory EFFECT = new SoundCategory(1f);

	private SoundCategory() {
	}
	
	private SoundCategory(float gain) {
		setGain(gain);
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
		SoundManager.updateGain(this);
	}
}
