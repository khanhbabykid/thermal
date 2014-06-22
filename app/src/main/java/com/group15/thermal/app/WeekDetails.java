package com.group15.thermal.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.group15.thermal.webservice.CorruptWeekProgramException;
import com.group15.thermal.webservice.Heating;
import com.group15.thermal.webservice.WeekProgram;

import java.lang.reflect.Field;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Week.FridayFragment;
import Week.MondayFragment;
import Week.SaturdayFragment;
import Week.SundayFragment;
import Week.ThursdayFragment;
import Week.TuesdayFragment;
import Week.WednesdayFragment;


public class WeekDetails extends ActionBarActivity implements ActionBar.TabListener, OnRefreshListener {

	public static WeekProgram weekProgram = null;
	public static int currentFragment = 0;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	ShowcaseView sv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(6);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				actionBar.setSelectedNavigationItem(position);
				currentFragment = position;
			}
		});


		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
							.setText(mSectionsPagerAdapter.getPageTitle(i))
							.setTabListener(this)
			);
		}
		try {
			GetThisWeekProgram();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Bundle b = getIntent().getExtras();
		if (b != null) {
			int a = b.getInt("which_one");
			mViewPager.setCurrentItem(a);
		}
		//Force overflow menu
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore

		}
	}

	public void GetThisWeekProgram() throws InterruptedException {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				if (Heating.startservice()) {
					showToast("Connected!", 1);
					try {
						WeekProgram wp = Heating.getWeekProgram();
						weekProgram = wp;
						System.out.println("got it!");
						if (wp.get_nr_switches_active(1) != 10) {
							wp.setDefault();
							Heating.setWeekProgram(wp);
						}
					} catch (ConnectException e) {
						e.printStackTrace();
					} catch (CorruptWeekProgramException e) {
						e.printStackTrace();
					}
				} else showToast("Unable to connect to web service!", 1);
			}
		});
		t.start();
		t.join();
	}

	public void showToast(final String toast, final Integer length) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(WeekDetails.this, toast, length == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();

			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			try {
				GetThisWeekProgram();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			((OnRefreshListener) mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem())).onRefresh();
			return true;
		}
		if (id == R.id.refreshall) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_help) {
			Fragment currentFragment = null;
			for (Fragment a : getSupportFragmentManager().getFragments()) {
				if (a.isVisible()) {
					currentFragment = a;
					break;
				}
			}
			final int[] counter = {0};
			final Fragment finalCurrentFragment = currentFragment;
			sv = new ShowcaseView.Builder(currentFragment != null ? currentFragment.getActivity() : null)
					.setTarget(new ViewTarget(currentFragment != null ? currentFragment.getView().findViewById(R.id.tvd2n) : null))
					.setContentTitle("Switches")
					.setStyle(R.style.CustomShowcaseTheme)
					.setContentText("The thermostat can either be in the day-mode or in the night-mode, " +
							"for which there exist a corresponding day-temperature and night-temperature. ")
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {

							switch (counter[0]) {
								case 0:
									sv.setShowcase(new ViewTarget(finalCurrentFragment != null ? finalCurrentFragment.getView().findViewById(R.id.switchbtn5) : null), true);
									sv.setContentText("Each day can have different switching times and " +
											"you are allowed to switch 5 times a day");
									break;
								case 3:
									ActionViewTarget home = new ActionViewTarget(finalCurrentFragment != null ? finalCurrentFragment.getActivity() : null, ActionViewTarget.Type.HOME);
									sv.setShowcase(home, true);
									sv.setContentText("Click on Home to go back!");
									sv.setButtonText("Done");
									break;
								case 1:
									sv.setShowcase(new ViewTarget(finalCurrentFragment != null ? finalCurrentFragment.getView().findViewById(R.id.savebtn) : null), true);
									if (finalCurrentFragment != null) {
										finalCurrentFragment.getView().findViewById(R.id.savebtn).setVisibility(View.VISIBLE);
									}
									sv.setContentText("Click save to save all changes");
									break;
								case 2:
									ActionViewTarget a = new ActionViewTarget(finalCurrentFragment != null ? finalCurrentFragment.getActivity() : null, ActionViewTarget.Type.OVERFLOW);
									sv.setShowcase(a, true);
									sv.setContentText("You can click on Refresh to Refresh current day setting.\nOr Refresh All to refresh" +
											" the whole week settings");
									break;
								case 4:
									sv.hide();
							}
							counter[0]++;
						}
					})
					.setShowcaseEventListener(new OnShowcaseEventListener() {
						@Override
						public void onShowcaseViewHide(ShowcaseView showcaseView) {

						}

						@Override
						public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

							if (finalCurrentFragment != null) {
								finalCurrentFragment.getView().findViewById(R.id.savebtn).setVisibility(View.INVISIBLE);
							}
						}

						@Override
						public void onShowcaseViewShow(ShowcaseView showcaseView) {

						}
					})
					.build();
			sv.setButtonText("Next");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

//	    getSupportActionBar().setSelectedNavigationItem(tab.getPosition());
//	    currentFragment=mViewPager.getCurrentItem();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onRefresh() {

	}


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public SectionsPagerAdapter(FragmentManager fm) {

			super(fm);
			this.fragments = new ArrayList<Fragment>();
			fragments.add(new MondayFragment());
			fragments.add(new TuesdayFragment());
			fragments.add(new WednesdayFragment());
			fragments.add(new ThursdayFragment());
			fragments.add(new FridayFragment());
			fragments.add(new SaturdayFragment());
			fragments.add(new SundayFragment());
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			//return PlaceholderFragment.newInstance(position + 1);
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			// Show 7 total pages.
			return 7;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
				case 3:
					return getString(R.string.title_section4).toUpperCase(l);
				case 4:
					return getString(R.string.title_section5).toUpperCase(l);
				case 5:
					return getString(R.string.title_section6).toUpperCase(l);
				case 6:
					return getString(R.string.title_section7).toUpperCase(l);
			}
			return null;
		}
	}
}
