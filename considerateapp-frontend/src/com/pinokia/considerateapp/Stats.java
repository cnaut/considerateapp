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
		this.timeOnApps.putAll(timeOnApps);
	}
	
	public String toJsonString() {
		String timeOnAppsString = "{ ";
		for (String key : this.timeOnApps.keySet()) {
			double value = this.timeOnApps.get(key);
			timeOnAppsString += "{ name:" + key + ", timeonapp:" + value + " }, ";
		}
		timeOnAppsString = timeOnAppsString.substring(0, timeOnAppsString.length() - 2) + " }";
		return "{ "
				+ "time:" + timeInMs + ", "
				+ "unlocks:" + numUnlocks + ", "
				+ "checks:" + numScreenChecks + ", "
				+ "totaltime:" + totalTime + ", "
				+ "apps:" + timeOnAppsString
				+ " }";
	}

}