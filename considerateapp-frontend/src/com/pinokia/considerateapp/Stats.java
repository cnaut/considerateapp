package com.pinokia.considerateapp;

import java.util.TreeMap;

public class Stats {
	long timeInMs;
	int numUnlocks;
	int numScreenChecks;
	double totalTime;
	TreeMap<String, Double> timeOnApps;

	public Stats(long time, int numUnlocks, int numScreenChecks,
			double totalTime, TreeMap<String, Double> timeOnApps) {
		this.timeInMs = time;
		this.numUnlocks = numUnlocks;
		this.numScreenChecks = numScreenChecks;
		this.totalTime = totalTime;

		this.timeOnApps = new TreeMap<String, Double>();
		timeOnApps.putAll(timeOnApps);
	}

}
