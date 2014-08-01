package de.felk.JOpenAL;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.OpenALException;

public class Sound {

	public static final int PRIORITY_DELETE = 0;
	public static final int PRIORITY_LOW = 1;
	public static final int PRIORITY_MODERATE = 2;
	public static final int PRIORITY_HIGH = 3;

	private ALSource source = null;
	private ALBuffer[] buffers;
	private int selectedBuffer = 0;
	public int priority = PRIORITY_MODERATE;

	private Vector position;
	private Vector velocity;
	private float gain = 1f;
	private float pitch = 1f;
	private boolean looping = false;
	private float offset = 0f;
	private Random random = new Random();
	private SoundCategory category;

	public Sound(SoundCategory category, Vector position, Vector velocity, String... filenames) {
		this(category, filenames, position, velocity, false, 1f, 1f);
	}

	public Sound(SoundCategory category, String[] filenames, Vector position, Vector velocity, boolean looping, float gain, float pitch) {
		try {
			this.buffers = ALBufferBank.getBuffers(filenames);
		} catch (OpenALException | IOException | LWJGLException e) {
			System.out.println("Error creating buffer");
			e.printStackTrace();
		}
		this.category = category;
		this.position = position;
		this.velocity = velocity;
		this.looping = looping;
		this.gain = gain;
		this.pitch = pitch;
	}

	public void setUncoupled() {
		stop();
		source = null;
	}

	public void randomizeBuffer() {
		selectBuffer(random.nextInt(buffers.length));
	}

	public void nextBuffer() {
		if (selectedBuffer + 1 == buffers.length) {
			selectBuffer(0);
		} else {
			selectBuffer(selectedBuffer + 1);
		}
	}

	public void play() {
		getSource().play();
	}

	public void stop() {
		getSource().stop();
	}

	public void pause() {
		getSource().pause();
	}

	public boolean isStopped() {
		return getSource().isStopped();
	}

	public boolean isPaused() {
		return getSource().isPaused();
	}

	public boolean isPlaying() {
		return getSource().isPlaying();
	}

	public ALSource getSource() {
		if (source == null) {
			source = SoundManager.getFreeSource(priority);
			source.couple(this);
			initSource();
		}
		return source;
	}

	public void initSource() {
		selectBuffer(selectedBuffer);
		setGain(gain);
		setLooping(looping);
		setOffset(offset);
		setPitch(pitch);
		setPosition(position);
		setVelocity(velocity);
		if (isPlaying()) play();
		if (isPaused()) pause();
		if (isStopped()) stop();
	}

	public ALBuffer[] getBuffers() {
		return buffers;
	}

	public ALBuffer getSelectedBuffer() {
		return buffers[selectedBuffer];
	}

	private void selectBuffer(int buffer) {
		this.selectedBuffer = buffer;
		getSource().setBuffer(buffers[selectedBuffer]);
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		getSource().setPosition(position);
		this.position = position;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		getSource().setVelocity(velocity);
		this.velocity = velocity;
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
		updateGain();
	}

	public void updateGain() {
		getSource().setGain(gain * category.getGain());
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		getSource().setPitch(pitch);
		this.pitch = pitch;
	}

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		getSource().setOffset(offset);
		this.offset = offset;
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		getSource().setLooping(looping);
		this.looping = looping;
	}

	public SoundCategory getCategory() {
		return category;
	}

	public void setCategory(SoundCategory category) {
		this.category = category;
	}

	public void randomizePitch(float f) {
		setPitch((1 - 0.5f * f) + random.nextFloat() * f);
	}

}
