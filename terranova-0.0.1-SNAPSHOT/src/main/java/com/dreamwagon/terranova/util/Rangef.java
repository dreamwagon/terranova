package com.dreamwagon.terranova.util;

/**
 * 
 * @author J. Demarco
 *
 */
public class Rangef {

	private float min = 0;
	private float max = 1;
	
	public Rangef() {
		
	}
	
	public Rangef(float min, float max) {
		super();
		this.min = min;
		this.max = max;
	}
	
	public int getMinIntValue() {
		return (int)min;
	}
	
	public int getMaxIntValue() {
		return (int)max;
	}
	
	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}
	
	public boolean isInRange(float value) {
		return value >=min && value<=max;
	}
}
