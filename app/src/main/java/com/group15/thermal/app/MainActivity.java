package com.group15.thermal.app;

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
import android.widget.ToggleButton;

import com.group15.thermal.webservice.Heating;
import com.group15.thermal.webservice.InvalidInputValueException;

import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {

	public static double temperature;





	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		temperature = 5.0;
		setContentView(R.layout.status_activity);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container1, new PlaceholderFragment())
					.commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_weeklist, menu);
		MenuItem b = menu.findItem(R.id.weekbtn);
		b.setTitle(changebtn);
		return true;
	}

	Boolean change = false;
	String changebtn = "Week Program";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.weekbtn:
				if (change == false) {
					WeekActivity weekActivity = new WeekActivity();
					FragmentTransaction a = getSupportFragmentManager().beginTransaction();
					a.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
					a.add(R.id.container1, weekActivity);
					a.commit();
					changebtn = "Today";
					change = true;
					invalidateOptionsMenu();
				} else {
					getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
							.add(R.id.container1, new PlaceholderFragment())
							.commit();
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
		String curTemp = "0.0", dayTemp, nightTemp, weekState;
		SeekBar seekBar;
		String mode  = "day";
		View thisview= null;
		Boolean stopupdate = true;

		Handler timeHandler;
		private TimerTask mTimerTask, anothertask;
		private Timer timer = new Timer();

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
			dotimertask();
			thisview=view;

		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			setRetainInstance(true);
			View rootView = inflater.inflate(R.layout.fragment_day, container, false);
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

			sunmoon = (ImageView)rootView.findViewById(R.id.ivSwitchIcon);
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
						temperature = d;
						currentTemp.setText(temperature + "\u2103");
					}
				}
			});
			seekBar.setOnTouchListener(new SeekBar.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {

					switch (motionEvent.getAction()) {
						case MotionEvent.ACTION_DOWN:
							System.out.println("down");
							stoptask();
							stopupdate=false;
							return true;
						case MotionEvent.ACTION_UP:
							System.out.println("up");
							putTemp();
							dotimertask();
							stopupdate=true;
							return true;
					}
					return false;
				}
			});

			timeHandler = new Handler();
			dotimertask();
			return rootView;
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
					break;
				case R.id.buttonPlus:
					changeTemperature(0);
					break;
				case R.id.buttonMinus:
					changeTemperature(1);
					break;
				case R.id.ivSwitchIcon:
					View v = getView().findViewById(R.id.main_fragment);
					if(mode.equals("day")){
						mode="night";
						sunmoon.setImageResource(R.drawable.moon_switch_ico);
						v.setBackgroundResource(R.drawable.night_bg);

					}else{
						mode = "day";
						sunmoon.setImageResource(R.drawable.sun_switch_ico);
						v.setBackgroundResource(R.drawable.day_bg);

					}
					break;
				case R.id.btSetPermanently:
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Heating.put(mode.equals("day")?Heating.DAY_TEMP:Heating.NIGHT_TEMP,Double.toString(temperature));
							} catch (InvalidInputValueException e) {
								e.printStackTrace();
							}
						}
					}).start();
					break;
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

		public void stoptask() {

			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;
				System.out.println("mtimertask cancelled");
			}
			if (anothertask != null) {
				anothertask.cancel();
				anothertask=null;
				System.out.println("anothertask cancelled");
			}
		}

		public void dotimertask() {
			if(anothertask==null){
				System.out.println("oh yeah");
			}
			mTimerTask = new TimerTask() {
				//this method is called every 1ms
				public void run() {

					timeHandler.post(new Runnable() {
						public void run() {

							Thread a = new Thread(new Runnable() {
								@Override
								public void run() {

									try {
										curTemp = Heating.get(Heating.CURRENT_TEMP);
										dayTemp = Heating.get(Heating.DAY_TEMP);
										nightTemp = Heating.get(Heating.NIGHT_TEMP);
										weekState = Heating.get(Heating.WEEK_STATE);
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
						}
					});
				}
			};
			anothertask = new TimerTask() {
				@Override
				public void run() {

					timeHandler.post(new Runnable() {
						@Override
						public void run() {

							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(stopupdate) {
										currentTemp.setText(curTemp + "\u2103");

										daytemp.setText(dayTemp + "\u2103");
										nighttemp.setText(nightTemp + "\u2103");
										if (weekState.equals("off")) {
											vacationMode.setChecked(true);
										} else {
											vacationMode.setChecked(false);
										}
										if (mode.equals("day")) {

											if (!curTemp.equals(dayTemp)) {
												setPermanent.setEnabled(true);
											} else {
												setPermanent.setEnabled(false);
											}
										} else if (mode.equals("night")) {
											if (!Double.toString(temperature).equals(nightTemp)) {
												setPermanent.setEnabled(true);
											} else {
												setPermanent.setEnabled(false);
											}
										}
									}
								}
							});
						}
					});
				}
			};
			timer.schedule(mTimerTask, 1000, 1000);
			timer.schedule(anothertask, 1000, 1200);
		}

	}
}
