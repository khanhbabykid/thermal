package com.group15.thermal.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.group15.thermal.webservice.Heating;
import com.group15.thermal.webservice.InvalidInputValueException;

import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {

	public static double temperature = 0.0;
	public static String mode = "day";
	public static int interval = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_activity);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container1, new PlaceholderFragment())
					.commit();
		}
		try {
			GetThisWeekProgram();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void GetThisWeekProgram() throws InterruptedException {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				if (Heating.startservice()) {
					showToast("Connected!", 1);
				} else
					showToast("Unable to connect to web service!", 1);
			}
		});
		t.start();
		t.join();
	}

	public void showToast(final String toast, final Integer length) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				/* TODO Auto-generated method stub */
				Toast.makeText(MainActivity.this, toast, length == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu_weeklist, menu);
		MenuItem b = menu.findItem(R.id.weekbtn);
		b.setTitle(changebtn);
		return true;
	}

	Boolean change = false;
	String changebtn = "Week Program";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
			case R.id.weekbtn:
				if (change == false) {
					WeekActivity weekActivity = new WeekActivity();
					FragmentTransaction a = getSupportFragmentManager().beginTransaction();
					a.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					a.replace(R.id.container1, weekActivity, "list_weekprogram");
					a.addToBackStack("fragment_day");
					a.commit();
					changebtn = "Today";
					change = true;
					invalidateOptionsMenu();
				} else {
					getSupportFragmentManager().popBackStack();
					item.setTitle("Week Program");
					change = false;
					changebtn = "Week Program";
					invalidateOptionsMenu();
				}
				break;
		}
		return true;
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

		public PlaceholderFragment() {

		}

		Button plus, minus, setPermanent;
		ToggleButton vacationMode;
		TextView currentTemp, daytext, daytemp, nighttext, nighttemp;
		ImageView sunmoon;
		String curTemp = "10.1", dayTemp, nightTemp, weekState;
		SeekBar seekBar;
		View thisview = null;
		int stopupdate = 2;

		Handler timeHandler;
		private TimerTask mTimerTask;
		private Timer timer = null;


		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {

			super.onViewCreated(view, savedInstanceState);
			Thread a = new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						dayTemp = Heating.get(Heating.DAY_TEMP);
						nightTemp = Heating.get(Heating.NIGHT_TEMP);
						curTemp = Heating.get(Heating.CURRENT_TEMP);
					} catch (ConnectException e) {
						e.printStackTrace();
					}
				}
			});
			a.start();
			try {
				a.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			seekBar.setProgress((int) (Double.parseDouble(curTemp) * 10));
			VerticalSeekBar s = (VerticalSeekBar) getActivity().findViewById(R.id.seekBar1);
			s.onSizeChanged(s.getWidth(), s.getHeight(), 0, 0);
//			timer = new Timer();
//			timeHandler = new Handler();
//			dotimertask();

		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_day, container, false);
			thisview = rootView;

			seekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);

			vacationMode = (ToggleButton) rootView.findViewById(R.id.btnVacation);
			vacationMode.setOnClickListener(this);

			plus = (Button) rootView.findViewById(R.id.buttonPlus);
			plus.setOnClickListener(this);

			minus = (Button) rootView.findViewById(R.id.buttonMinus);
			minus.setOnClickListener(this);

			setPermanent = (Button) rootView.findViewById(R.id.btSetPermanently);
			setPermanent.setOnClickListener(this);

			currentTemp = (TextView) rootView.findViewById(R.id.tvCurrentTemperature);
			daytext = (TextView) rootView.findViewById(R.id.tvDay);
			daytemp = (TextView) rootView.findViewById(R.id.tvdaytemp);
			nighttext = (TextView) rootView.findViewById(R.id.tvNight);
			nighttemp = (TextView) rootView.findViewById(R.id.tvnighttemp);

			sunmoon = (ImageView) rootView.findViewById(R.id.ivSwitchIcon);
			sunmoon.setOnClickListener(this);


			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
				                              boolean fromUser) {
					// TODO Auto-generated method stub
					if (progress < 50) {
						seekBar.setProgress(50);
					} else {
						double d = (double) progress;
						d = (d / 10);
						System.out.println("...");
						temperature = d;
						currentTemp.setText(d + "\u2103");
					}
				}
			});
			seekBar.setOnTouchListener(new SeekBar.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {

					switch (motionEvent.getAction()) {
						case MotionEvent.ACTION_DOWN:
							System.out.println("down");
							stopupdate = 3;
							return true;
						case MotionEvent.ACTION_UP:
							System.out.println("up");
							putTemp();
							stopupdate = 2;
							return true;
					}
					return false;
				}
			});

			return rootView;
		}

		@Override
		public void onStop() {

			System.out.println("Stop");
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			super.onStop();
		}

		@Override
		public void onResume() {

			System.out.println("resumed");
			timer = new Timer();
			timeHandler = new Handler();
			dotimertask();
			super.onResume();
		}

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
				case R.id.btnVacation:
					if (vacationMode.isChecked()) {
						new Thread(new Runnable() {
							@Override
							public void run() {

								try {
									Heating.put(Heating.WEEK_STATE, "off");
									System.out.println("Vacation Mode: ON");
								} catch (InvalidInputValueException e) {
									e.printStackTrace();
								}
							}
						}).start();
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {

								try {
									Heating.put(Heating.WEEK_STATE, "on");
									System.out.println("Vacation Mode: OFF");
								} catch (InvalidInputValueException e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
					vacationMode.setChecked(!vacationMode.isChecked());
					break;
				case R.id.buttonPlus:
					changeTemperature(0);
					stopupdate = 1;
					break;
				case R.id.buttonMinus:
					changeTemperature(1);
					stopupdate = 1;
					break;
				case R.id.ivSwitchIcon:
					changemode(0);
					break;
				case R.id.btSetPermanently:
					new Thread(new Runnable() {
						@Override
						public void run() {

							try {
								Heating.put(mode.equals("day") ? Heating.DAY_TEMP : Heating.NIGHT_TEMP, Double.toString(temperature));
							} catch (InvalidInputValueException e) {
								e.printStackTrace();
							}
						}
					}).start();
					break;
			}
		}

		private void changemode(int a) {

			System.out.println(sunmoon.getTag());
			System.out.println(mode);
			stopupdate = 0;
			if (a == 0) {
				if (mode.equals("day")) {
					mode = "night";
					temperature = Double.parseDouble(nightTemp);
					putTemp();
					thisview.setBackgroundResource(R.drawable.night_bg);
					sunmoon.setImageResource(R.drawable.moon_switch_ico);
				} else if (mode.equals("night")) {
					mode = "day";
					temperature = Double.parseDouble(dayTemp);
					putTemp();
					thisview.setBackgroundResource(R.drawable.day_bg);
					sunmoon.setImageResource(R.drawable.sun_switch_ico);
				}
			}
		}

		public void changeTemperature(int a) {

			if (a == 1) {
				temperature -= 0.1;
			} else {
				temperature += 0.1;
			}
			putTemp();
			VerticalSeekBar s = (VerticalSeekBar) getActivity().findViewById(R.id.seekBar1);
			s.setProgress((int) (10 * temperature));
			s.onSizeChanged(s.getWidth(), s.getHeight(), 0, 0);
		}

		public void putTemp() {

			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						Heating.put(Heating.CURRENT_TEMP, Double.toString(temperature));
					} catch (InvalidInputValueException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

		public void dotimertask() {

			mTimerTask = new TimerTask() {
				//this method is called every 1ms
				public void run() {

					timeHandler.post(new Runnable() {
						public void run() {

							AsyncTaskRunner abc = new AsyncTaskRunner();
							abc.execute();
						}
					});
				}
			};
			timer.schedule(mTimerTask, 0, interval);
		}

		private class AsyncTaskRunner extends AsyncTask<String, String, String> {

			@Override
			protected void onPostExecute(String s) {

				if (stopupdate == 2) {

					//Update TEMP
					currentTemp.setText(curTemp + "\u2103");
					daytemp.setText(dayTemp + "\u2103");
					nighttemp.setText(nightTemp + "\u2103");

					//check Week State
					if (weekState.equals("off")) {
						vacationMode.setChecked(true);
					} else {
						vacationMode.setChecked(false);
					}

					//Check mode and set button
					if (mode.equals("day")) {
						//check SET button
						if (!Double.toString(temperature).equals(dayTemp)) {
							setPermanent.setEnabled(true);
						} else {
							setPermanent.setEnabled(false);
						}
						if (!sunmoon.getTag().equals("sun")) {
							sunmoon.setImageResource(R.drawable.sun_switch_ico);
							thisview.setBackgroundResource(R.drawable.day_bg);
							sunmoon.setTag("sun");
						}
						if (curTemp.equals(nightTemp)) {
							mode = "night";
						}

					} else if (mode.equals("night")) {
						if (!Double.toString(temperature).equals(nightTemp)) {
							setPermanent.setEnabled(true);
						} else {
							setPermanent.setEnabled(false);
						}
						if (!sunmoon.getTag().equals("moon")) {
							sunmoon.setImageResource(R.drawable.moon_switch_ico);
							thisview.setBackgroundResource(R.drawable.night_bg);
							sunmoon.setTag("moon");
						}
						if (curTemp.equals(dayTemp)) {
							mode = "day";
						}
					}
				}
//				} else if (stopupdate == 1) {
//					stopupdate=2;
//				}
				else{
					stopupdate++;
				}
				super.onPostExecute(s);
			}

			@Override
			protected String doInBackground(String... strings) {

				try {
					curTemp = Heating.get(Heating.CURRENT_TEMP);
					dayTemp = Heating.get(Heating.DAY_TEMP);
					nightTemp = Heating.get(Heating.NIGHT_TEMP);
					weekState = Heating.get(Heating.WEEK_STATE);
				} catch (ConnectException e) {
					e.printStackTrace();
				}

				return null;
			}
		}
	}
}
