package com.group15.thermal.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nguyen Quoc Khanh on 17-Jun-14.
 */
public class WeekActivity extends Activity implements View.OnClickListener {
	int[] buttons = {R.id.monbtn,R.id.tuebtn,R.id.wedbtn,R.id.thubtn,R.id.fribtn,R.id.satbtn,R.id.sunbtn};
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.list_weekprogram);
		super.onCreate(savedInstanceState);
		for(int i =0;i<7;i++){
			Button a = (Button)findViewById(buttons[i]);
			a.setOnClickListener(this);
		}

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
		a = new Intent(this,WeekDetails.class);
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
