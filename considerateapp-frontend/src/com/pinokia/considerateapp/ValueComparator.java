package com.pinokia.considerateapp;

import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<Object> {

	Map<String, Double> base;

	public ValueComparator(Map<String, Double> base) {
		this.base = base;
	}

	public int compare(Object a, Object b) {

		if ((Double) base.get(a) < (Double) base.get(b)) {
			return 1;
		} else if ((Double) base.get(a) == (Double) base.get(b)) {
			return 0;
		} else {
			return -1;
		}
	}
}