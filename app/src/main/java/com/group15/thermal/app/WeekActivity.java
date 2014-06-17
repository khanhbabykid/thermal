package com.group15.thermal.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Nguyen Quoc Khanh on 17-Jun-14.
 */
public class WeekActivity extends Activity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.list_weekprogram);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View view) {

	}
}
