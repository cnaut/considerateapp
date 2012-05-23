package com.pinokia.considerateapp;

import java.util.ArrayList;
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
		String timeOnAppsString = "";
                if (this.timeOnApps.size() == 0)
			timeOnAppsString = "[]";
		else { 
			timeOnAppsString = "[ ";
			for (String key : this.timeOnApps.keySet()) {
				double value = this.timeOnApps.get(key);
				timeOnAppsString += "{ \"name\":\"" + key + "\", \"timeonapp\":\"" + value + "\" }, ";
			}
			timeOnAppsString = timeOnAppsString.substring(0, timeOnAppsString.length() - 2) + " ]";
		}
		return "{ "
				+ "\"time\":" + timeInMs + ", "
				+ "\"unlocks\":" + numUnlocks + ", "
				+ "\"checks\":" + numScreenChecks + ", "
				+ "\"totaltime\":" + totalTime + ", " 
				+ "\"apps\":" + timeOnAppsString
				+ " }";
	}
	
	public static String toJsonString(ArrayList<Stats> array) {
		String json = "[ ";
		for (int i = 0; i < array.size(); i++) {
			json += array.get(i).toJsonString() + ", ";
		}
		json = json.substring(0, json.length() - 2) + " ]";
		return json;
	}
	
	public static String toJsonString(ArrayList<Stats> array, String statsString) {
		String json = toJsonString(array);
		if (statsString != "") {
			json = statsString.substring(0, json.length() - 2) + ", " + json.substring(2);
		}
		return json;
	}

}
