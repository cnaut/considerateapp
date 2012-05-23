package com.pinokia.considerateapp;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ConsiderateAppActivity extends FragmentActivity {

	// testing or release mode?
	public static final boolean testing = false;
	
	public static final int chartWidth = 500;
	public static final int chartHeight = 220;

	private PagerAdapter mPagerAdapter;
	private SharedPreferences prefs;
	public static final String prefsName = "considerateapp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.viewpager_layout);
		refreshWhitelist();
		StatsService.start(getApplicationContext());
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
		prefs = getSharedPreferences(prefsName, 0);
		System.out.println("OnResume: Main Activity");
	}

	// @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		return true;
	}

    	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    		switch (item.getItemId()) {
        		case R.id.toggle_lockscreen:
                    	toggleSleepMonitor();
                    	return true;
                	case R.id.whitelist_button:
                    	toggleWhitelist();
                    	return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    	protected static boolean whitelistEnabled = testing ? false : true;

    	private void toggleWhitelist() {
        	whitelistEnabled = !whitelistEnabled;
        	FlipService.toggleService(getApplicationContext());
        	if(whitelistEnabled) {
        	    refreshWhitelist();
        	    Toast.makeText(getApplicationContext(),
        	            	"Whitelist is enabled.",
                	    	Toast.LENGTH_SHORT).show();
        	} else {
            		Toast.makeText(getApplicationContext(),
             			"Whitelist is disabled.",
                    		Toast.LENGTH_SHORT).show();
        	}
    	}

    	protected static ArrayList<String> whitelist;

    	private void refreshWhitelist() {
		if (whitelistEnabled) {
			whitelist = new ArrayList<String>();
			CursorLoader loader1 = new CursorLoader(this,
					ContactsContract.Contacts.CONTENT_URI, new String[] {
							ContactsContract.Contacts._ID,
							ContactsContract.Contacts.DISPLAY_NAME },
					"starred=1 AND has_phone_number=1", null, null);
			Cursor cCur = loader1.loadInBackground();

			while (cCur.moveToNext()) {
				String id = cCur.getString(cCur
						.getColumnIndex(ContactsContract.Contacts._ID));

				// String name =
				// (cCur.getString(cCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				CursorLoader loader2 = new CursorLoader(this,
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { id }, null);
				Cursor pCur = loader2.loadInBackground();

				while (pCur.moveToNext()) {
					String phoneNumber = pCur
							.getString(pCur
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					whitelist.add(phoneNumber);
					Log.d("whitelist", phoneNumber);
				}
				pCur.close();
			}
			cCur.close();
		}
	}

	protected void toggleSleepMonitor() {
		SharedPreferences.Editor prefsEdit = prefs.edit();
		if (SleepMonitorService.toggleService(getApplicationContext())) {
			Toast.makeText(getApplicationContext(),
					"Lockscreen is currently being replaced.",
					Toast.LENGTH_SHORT).show();
			prefsEdit.putBoolean("boot", true);

		} else {
			Toast.makeText(getApplicationContext(),
					"Lockscreen is no longer being replaced.",
					Toast.LENGTH_SHORT).show();
			prefsEdit.putBoolean("boot", true);
		}
		prefsEdit.commit();
	}
}
