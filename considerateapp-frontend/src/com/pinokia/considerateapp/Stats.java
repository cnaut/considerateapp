package com.pinokia.considerateapp;

import java.util.TreeMap;

public class Stats {
	private long timeInMs;
	private int numUnlocks;
	private int numScreenChecks;
	private long totalTime;
	private TreeMap<String, Double> timeOnApps;

	public Stats(long time, int numUnlocks, int numScreenChecks,
			long totalTime, TreeMap<String, Double> timeOnApps) {
		this.timeInMs = time;
		this.numUnlocks = numUnlocks;
		this.numScreenChecks = numScreenChecks;
		this.totalTime = totalTime;

		this.timeOnApps = new TreeMap<String, Double>();
		timeOnApps.putAll(timeOnApps);
	}
	
	public String toJsonString() {
		String timeOnAppsString = "{ ";
		for (String key : timeOnApps.keySet()) {
			double value = timeOnApps.get(key);
			timeOnAppsString += key + ":" + value + ", ";
		}
		timeOnAppsString = timeOnAppsString.substring(timeOnAppsString.length() - 2) + " }";
		return "{ "
				+ "time:" + timeInMs + ", "
				+ "unlocks:" + numUnlocks + ", "
				+ "checks:" + numScreenChecks + ", "
				+ "totaltime:" + totalTime + ", "
				+ "apps:" + timeOnAppsString
				+ " }";
	}

}
