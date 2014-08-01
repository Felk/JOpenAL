package de.felk.JOpenAL;

public class ALBuffer {

	private int size;
	private int bufferID;
	private int channels;

	/** Creates a new buffer, representing an audio file loaded into the sound device */
	public ALBuffer(int bufferID, int size, int channels) {
		this.bufferID = bufferID;
		this.size = size;
		this.channels = channels;
	}

	/** Frees this buffer (unloads data from audio device) */
	public void destroy() {
		ALHelper.destroyBuffer(bufferID);
	}

	/** Returns the size of the raw buffer data in bytes */
	public int getSize() {
		return size;
	}

	public int getChannels() {
		return channels;
	}

	public int getBufferID() {
		return bufferID;
	}

	@Override
	public String toString() {
		return "ALBuffer(size: " + size + ", bufferID: " + bufferID + ", channels: " + channels + ")";
	}
}
