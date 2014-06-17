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

import com.group15.thermal.app.WeekDetails;
import com.group15.thermal.app.OnRefreshListener;
import com.group15.thermal.app.R;
import com.group15.thermal.webservice.Switch;

import java.util.ArrayList;

public class SundayFragment extends Fragment implements View.OnClickListener,OnRefreshListener{

	String title = null;
	int[] buttons = {R.id.switchbtn1,R.id.switchbtn2, R.id.switchbtn3,
			R.id.switchbtn4,R.id.switchbtn5,R.id.switchbtn6,R.id.switchbtn7
			,R.id.switchbtn8,R.id.switchbtn9,R.id.switchbtn10};
	Button usedbutton,savebtn;
	ArrayList<String> day2night= new ArrayList<String>(5);
	ArrayList<String> night2day= new ArrayList<String>(5);
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		title=getString(R.string.title_section7);
		View rootView = inflater.inflate(R.layout.fragment_main,container,false);
		TextView titleview = (TextView)rootView.findViewById(R.id.section_label);
		titleview.setText(title + " Settings");
		return rootView;
	}

	View thisview=null;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		thisview=view;
		savebtn = (Button) thisview.findViewById(R.id.savebtn);
		savebtn.setOnClickListener(this);
		UpdateDisplay();

		super.onViewCreated(view, savedInstanceState);
	}


	private void UpdateDisplay(){
		for(Switch swbtn : WeekDetails.weekProgram.getDaySwitches(title)){
			if(swbtn.getType().equalsIgnoreCase("day")){
				night2day.add(swbtn.getTime());
			}else{
				day2night.add(swbtn.getTime());
			}
		}
		savebtn.setVisibility(View.INVISIBLE);
		for (int i=0; i<5;i++){
			Button z = (Button)this.thisview.findViewById(buttons[i + 5]);
			Button y = (Button)thisview.findViewById(buttons[i]);
			z.setText(night2day.get(i));
			y.setText(day2night.get(i));
			z.setOnClickListener(this);
			y.setOnClickListener(this);
		}
	}

	@Override
	public void onRefresh() {
		if(WeekDetails.currentFragment==1){
			//Toast.makeText(getActivity(), "Monday Created!", Toast.LENGTH_SHORT).show();
		}
		while(this.isAdded()==false) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		UpdateDisplay();
		System.out.println("Updated");

	}

	@Override
	public void onClick(View view) {

		if(view.getId()==R.id.savebtn){

			savebtn.setVisibility(View.INVISIBLE);
		}else {
			for (Integer button : buttons) {
				if (button.equals(view.getId())) {
					usedbutton = (Button) this.thisview.findViewById(button);
					break;
				}
			}
			TimePickerDialog.OnTimeSetListener gettime;
			gettime = new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker timePicker, int i, int i2) {

					String time;
					time = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
					time = time + ":" + (i2 < 10 ? "0" + Integer.toString(i2) : Integer.toString(i2));
					usedbutton.setText(time);
					savebtn.setVisibility(View.VISIBLE);
				}
			};
			TimePickerDialog picker = new TimePickerDialog(getActivity(), gettime,
					new Integer(usedbutton.getText().toString().split(":")[0]),
					new Integer(usedbutton.getText().toString().split(":")[1]), true);
			picker.show();
		}
	}
}
