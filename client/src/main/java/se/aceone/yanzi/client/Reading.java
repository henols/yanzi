package se.aceone.yanzi.client;

import java.time.Instant;

public class Reading {
	private long timestamp;
	private double reading; // 64bit float
	
	public Reading() {
	}

	
	public Reading(long timestamp, double reading) {
		this.timestamp = timestamp;
		this.reading = reading;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public double getReading() {
		return reading;
	}


	public void setReading(double reading) {
		this.reading = reading;
	}


	@Override
	public String toString() {
		return Instant.ofEpochSecond(timestamp).toString() + " " + reading ;
	}
}
