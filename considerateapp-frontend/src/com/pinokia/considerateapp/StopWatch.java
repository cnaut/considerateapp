package com.pinokia.considerateapp;


public class StopWatch {
	private long startTime = System.currentTimeMillis();
	private long stopTime = 0;
	private boolean running = true;
	private long totalTime = 0;

	public long getTotalTime() {
		if (this.running) {
			long elapsed = this.totalTime
					+ (System.currentTimeMillis() - this.startTime);
			return elapsed;
		}
		return this.totalTime;
	}

	public void setTotalTime(long setTime) {
		this.totalTime = setTime;
		startTime = System.currentTimeMillis();
		stopTime = 0;
		running = true;
	}

	public void start() {
		this.startTime = System.currentTimeMillis();
		this.running = true;
	}

	public void stop() {
		// if phone starts off in SCREEN_OFF state
		if (this.startTime == 0) {
			return;
		}

		this.stopTime = System.currentTimeMillis();
		this.running = false;

		long elapsed = this.stopTime - this.startTime;
		this.totalTime = this.totalTime + elapsed;

		this.startTime = 0;
		this.stopTime = 0;
	}

}
