package com.pinokia.considerateapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;
import java.util.Vector;

public class ConsiderateAppActivity extends FragmentActivity {

	public static final boolean testing = false;

	public static final int chartWidth = 500;
	public static final int chartHeight = 220;

	private PagerAdapter mPagerAdapter;
	public static final String prefsName = "considerateapp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.viewpager_layout);
		SharedPreferences prefs = getSharedPreferences(prefsName, 0);
		StatsService.start(getApplicationContext());
		if (prefs.getBoolean("lockscreen", true)) {
			SleepMonitorService.start(getApplicationContext(), false);
		} else{
			SleepMonitorService.stop(getApplicationContext(), false);
		}
		if (prefs.getBoolean("considerate_mode", true)) {
			FlipService.start(getApplicationContext(), false);
		} else{
			FlipService.stop(getApplicationContext(), false);
		}
		this.initialisePaging();
	}

	/*
	 * Initialize the fragments to be paged
	 */
	private void initialisePaging() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this,
				UnlocksFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				TotalTimeFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				TopAppsFragment.class.getName()));

		FragmentManager fm = super.getSupportFragmentManager();
		if (fm == null) {
			System.out.println("fm is null");
			throw new IllegalStateException("fm is null");
		} else {
			this.mPagerAdapter = new MyPagerAdapter(fm, fragments);
			ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
			pager.setAdapter(this.mPagerAdapter);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		System.out.println("OnPause: Main Activity");
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("OnResume: Main Activity");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		return(super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.toggle_lockscreen:
			SleepMonitorService.toggleService(getApplicationContext());
			return true;
		case R.id.toggle_consideratemode:
			FlipService.toggleService(getApplicationContext());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void toggleConsiderateMode() {
		FlipService.toggleService(getApplicationContext());
	}

	protected void toggleSleepMonitor() {
		SleepMonitorService.toggleService(getApplicationContext());
	}
}
