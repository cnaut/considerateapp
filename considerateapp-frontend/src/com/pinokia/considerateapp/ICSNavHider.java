package com.pinokia.considerateapp;

import android.view.View;

//We have to do al of this stupidity because the ClassLoader is lazy -- it won't load this if it's never called. This is the only real way to do versioning on Android.
public class ICSNavHider {
	public static void DisableNav(View v){
		// v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
}