package de.felk.JOpenAL;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

public class ALSource {

	private final int sourceID;
	private ALBuffer buffer = null;
	public int priority = Sound.PRIORITY_MODERATE;
	private int byteOffset;
	private float offset;
	private int state;

	private Sound sound;

	/** Creates a new ALSource object that is linked to a new source on the sound device */
	public ALSource() {
		this.sourceID = ALHelper.genSource();
		ALHelper.setRolloffFactor(sourceID, SoundManager.DEFAULT_ROLLOFF_FACTOR);
	}

	/** uncouples this source from the sound it was playing */
	public void uncouple() {
		sound.setUncoupled();
		sound = null;
	}

	/** couples this source with a new sound to be played */
	public void couple(Sound sound) {
		this.sound = sound;
	}

	/** returns whether this source is currently coupled to a sound */
	public boolean isUncoupled() {
		return sound == null;
	}

	public void update() {
		if (isUncoupled())
			return;
		try {
			// byteOffset is needed to calculate local offset between 0 and 1
			byteOffset = ALHelper.getByteOffset(sourceID);
			state = ALHelper.getSourceState(sourceID);
		} catch (OpenALException e) {
			e.printStackTrace();
			System.out.println("error fetching offset and state for Source with ID " + sourceID);
		}
		offset = (float) byteOffset / buffer.getSize();
		if (isStopped() && sound.isLooping()) {
			// TODO distinguish between multi-buffer handling
			sound.play();
		}
	}

	/** plays the sound this source is coupled with */
	public void play() {
		ALHelper.play(sourceID);
	}

	/** stops the playback of the sound this source is coupled with */
	public void stop() {
		ALHelper.stop(sourceID);
	}

	/** Pauses the playback of the sound this source is coupled with */
	public void pause() {
		ALHelper.pause(sourceID);
	}

	/** returns whether the playback is stopped or not */
	public boolean isStopped() {
		return (state == AL10.AL_STOPPED);
	}

	/** returns whether the playback is paused or not */
	public boolean isPaused() {
		return (state == AL10.AL_PAUSED);
	}

	/** returns whether the playback is running or not */
	public boolean isPlaying() {
		return (state == AL10.AL_PLAYING);
	}

	/** Frees this source from the sound device */
	public void destroy() {
		ALHelper.destroySource(sourceID);
	}

	public int getSourceID() {
		return sourceID;
	}

	/** Gets the current playback offset (between 0 and 1) */
	public float getOffset() {
		return offset;
	}

	/** Set a new buffer (audio file) to be played */
	public void setBuffer(ALBuffer buffer) {
		this.buffer = buffer;
		ALHelper.bindBufferToSource(buffer.getBufferID(), sourceID);
	}

	public ALBuffer getBuffer() {
		return buffer;
	}

	public void setPosition(float posX, float posY, float posZ) {
		ALHelper.setPosition(sourceID, posX, posY, posZ);
	}

	public void setVelocity(float velX, float velY, float velZ) {
		ALHelper.setVelocity(sourceID, velX, velY, velZ);
	}

	public void setGain(float gain) {
		ALHelper.setGain(sourceID, gain);
	}

	public void setPitch(float pitch) {
		ALHelper.setPitch(sourceID, pitch);
	}

	public void setOffset(float offset) {
		ALHelper.setOffset(sourceID, buffer.getSize(), offset);
	}

	public void setLooping(boolean looping) {
		ALHelper.setLooping(sourceID, looping);
	}

	public boolean equals(Object o) {
		if (!(o instanceof ALSource))
			return false;
		return ((ALSource) o).getSourceID() == sourceID;
	}

	public void updateGain(SoundCategory category) {
		if (isUncoupled())
			return;
		if (category == null || category == sound.getCategory())
			sound.updateGain();
	}

}
