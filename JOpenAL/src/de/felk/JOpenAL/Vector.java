package de.felk.JOpenAL;

public interface Vector {
	
	public float getX();
	public float getY();
	public Vector setX(float x);
	public Vector setY(float y);

	public Vector set(Vector v);

	public Vector add(Vector v);
	public Vector added(Vector v);
	public Vector subtract(Vector v);
	public Vector subtracted(Vector v);
	public Vector multiply(float r);
	public Vector multiplied(float r);
	
	public Vector clone();

}
