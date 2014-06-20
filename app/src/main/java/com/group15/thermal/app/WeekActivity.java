package com.group15.thermal.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Nguyen Quoc Khanh on 17-Jun-14.
 */
public class WeekActivity extends Fragment implements View.OnClickListener {
	int[] buttons = {R.id.monbtn,R.id.tuebtn,R.id.wedbtn,R.id.thubtn,R.id.fribtn,R.id.satbtn,R.id.sunbtn};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.list_weekprogram,container,false);
		for(int i =0;i<7;i++){
			Button a = (Button)rootView.findViewById(buttons[i]);
			a.setOnClickListener(this);
		}
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View view) {
		for(int i = 0; i <7;i++){
			if(view.getId()==buttons[i]){
				setintent(i);
				break;
			}
		}
	}
	private void setintent(int i){
		Intent a=null;
		a = new Intent(getActivity(),WeekDetails.class);
		switch (i){
			case 0:
				a.putExtra("which_one",0);
				break;
			case 1:
				a.putExtra("which_one",1);
				break;
			case 2:
				a.putExtra("which_one",2);
				break;
			case 3:
				a.putExtra("which_one",3);
				break;
			case 4:
				a.putExtra("which_one",4);
				break;
			case 5:
				a.putExtra("which_one",5);
				break;
			case 6:
				a.putExtra("which_one",6);
				break;
		}
		startActivity(a);
	}
}
