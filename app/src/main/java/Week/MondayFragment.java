package Week;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.group15.thermal.app.OnRefreshListener;
import com.group15.thermal.app.R;
import com.group15.thermal.app.WeekDetails;
import com.group15.thermal.webservice.Heating;
import com.group15.thermal.webservice.Switch;

import java.util.ArrayList;
import java.util.Collections;

public class MondayFragment extends Fragment implements View.OnClickListener, OnRefreshListener {

	String title = null;
	int[] buttons = {R.id.switchbtn1, R.id.switchbtn2, R.id.switchbtn3,
			R.id.switchbtn4, R.id.switchbtn5, R.id.switchbtn6, R.id.switchbtn7
			, R.id.switchbtn8, R.id.switchbtn9, R.id.switchbtn10};
	Button usedbutton, savebtn;
	ArrayList<String> day2night = new ArrayList<String>(5);
	ArrayList<String> night2day = new ArrayList<String>(5);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		title = getString(R.string.title_section1);
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		TextView titleview = (TextView) rootView.findViewById(R.id.section_label);
		titleview.setText(title + " Settings");
		setRetainInstance(true);
		return rootView;
	}

	View thisview = null;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		thisview = view;
		savebtn = (Button) thisview.findViewById(R.id.savebtn);
		savebtn.setOnClickListener(this);
		UpdateDisplay();

		super.onViewCreated(view, savedInstanceState);
	}


	private void UpdateDisplay() {
		System.out.println("Updated");
		for (Switch swbtn : WeekDetails.weekProgram.getDaySwitches(title)) {
			if (swbtn.getType().equalsIgnoreCase("day")) {
				night2day.add(swbtn.getTime());
			} else {
				day2night.add(swbtn.getTime());
			}
		}
		savebtn.setVisibility(View.INVISIBLE);
		for (int i = 0; i < 5; i++) {
			Button z = (Button) this.thisview.findViewById(buttons[i + 5]);
			Button y = (Button) thisview.findViewById(buttons[i]);
			z.setText(night2day.get(i));
			y.setText(day2night.get(i));
			z.setOnClickListener(this);
			y.setOnClickListener(this);
		}
		night2day.clear();
		day2night.clear();
	}

	@Override
	public void onRefresh() {
		UpdateDisplay();


	}

	@Override
	public void onClick(View view) {

		int UsedButton;
		if (view.getId() == R.id.savebtn) {
			putWeekProgram();
			showToast(2);
			savebtn.setVisibility(View.INVISIBLE);
		} else {
			UsedButton = -1;
			for (int i = 0; i < 10; i++) {
				if (view.getId() == buttons[i]) {
					usedbutton = (Button) thisview.findViewById(buttons[i]);
					UsedButton = i;
					break;
				}
			}
			//Get and check time!!!
			final int finalUsedint = UsedButton;
			TimePickerDialog.OnTimeSetListener gettime;
			gettime = new TimePickerDialog.OnTimeSetListener() {
				int callCount = 0;

				@Override
				public void onTimeSet(TimePicker timePicker, int i, int i2) {

					if (callCount == 1) {
						if (finalUsedint == 0) {
							setTime(i, i2);
							sortTimes(1);
						} else if (finalUsedint >= 1 && finalUsedint <= 4) {
							setTime(i, i2);
							sortTimes(1);
						} else if (finalUsedint >= 5 && finalUsedint <= 9) {
							setTime(i, i2);
							sortTimes(2);
						}
					}
					callCount++;
				}
			};
			TimePickerDialog picker = new TimePickerDialog(thisview.getContext(), gettime,
					new Integer(usedbutton.getText().toString().split(":")[0]),
					new Integer(usedbutton.getText().toString().split(":")[1]), true);
			picker.show();
		}
	}

	private void sortTimes(int type) {

		if (type == 1) {
			System.out.println("Sorted??");
			ArrayList<Integer> allTime = new ArrayList<Integer>(0);
			for (int j = 0; j < 5; j++) {
				Button c = (Button) thisview.findViewById(buttons[j]);
				int eleTime = Integer.parseInt(c.getText().toString().split(":")[0] +
						c.getText().toString().split(":")[1]);
				allTime.add(eleTime);
			}
			Collections.sort(allTime);
			for (int j = 0; j < 5; j++) {
				Button c = (Button) thisview.findViewById(buttons[j]);
				int i = (allTime.get(j) / 100);
				int i2 = (allTime.get(j) % 100);
				String time;
				time = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
				time = time + ":" + (i2 < 10 ? "0" + Integer.toString(i2) : Integer.toString(i2));
				c.setText(time);

			}
			System.out.println("Sorted!!");
		} else if (type == 2) {
			ArrayList<Integer> allTime = new ArrayList<Integer>(0);
			for (int j = 5; j < 10; j++) {
				Button c = (Button) thisview.findViewById(buttons[j]);
				int eleTime = Integer.parseInt(c.getText().toString().split(":")[0] +
						c.getText().toString().split(":")[1]);
				allTime.add(eleTime);
			}
			Collections.sort(allTime);
			for (int j = 5; j < 10; j++) {
				Button c = (Button) thisview.findViewById(buttons[j]);
				int i = (allTime.get(j - 5) / 100);
				int i2 = (allTime.get(j - 5) % 100);
				String time;
				time = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
				time = time + ":" + (i2 < 10 ? "0" + Integer.toString(i2) : Integer.toString(i2));
				c.setText(time);
			}
		}
	}

	private void setTime(int i, int i2) {

		String time;
		time = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
		time = time + ":" + (i2 < 10 ? "0" + Integer.toString(i2) : Integer.toString(i2));
		String oldtime = usedbutton.getText().toString();
		usedbutton.setText(time);
		if (!oldtime.equals(time))
			savebtn.setVisibility(View.VISIBLE);
	}

	private void showToast(int status) {

		String text = "";
		switch (status) {
			case 0:
				text = "Can't set value";
				break;
			case 1:
				text = "Set previous button first!";
				break;
			case 2:
				text = "Updated";
				break;
		}
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}



	private boolean putWeekProgram() {

		ArrayList<Switch> newSwitches = new ArrayList<Switch>();
		for (int i = 0; i < 5; i++) {
			Button tempbtn1 = (Button) thisview.findViewById(buttons[i]);
			Button tempbtn2 = (Button) thisview.findViewById(buttons[i + 5]);
			newSwitches.add(new Switch("night", true, tempbtn1.getText().toString()));
			newSwitches.add(new Switch("day", true, tempbtn2.getText().toString()));
		}
		WeekDetails.weekProgram.setDaySwitches(title, newSwitches);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Heating.setWeekProgram(WeekDetails.weekProgram);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		return false;
	}

	private void checkDuplicates(int type) {

		switch (type) {
			case 1:
				for (int i = 1; i < 5; i++) {

				}
				break;
			case 2:

				break;
		}
	}

}
