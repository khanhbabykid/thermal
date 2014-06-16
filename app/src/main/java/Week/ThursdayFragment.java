package Week;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.group15.thermal.app.MainActivity;
import com.group15.thermal.app.OnRefreshListener;
import com.group15.thermal.app.R;

public class ThursdayFragment extends Fragment implements OnRefreshListener {

	String title = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main,container,false);
		TextView titleview = (TextView)rootView.findViewById(R.id.section_label);
		titleview.setText(title + " Settings");
		return rootView;
	}

	@Override
	public void onRefresh() {
		if(MainActivity.currentFragment==2||MainActivity.currentFragment==4){
			Toast.makeText(getActivity(), "Thursday Created!", Toast.LENGTH_SHORT).show();
		}
	}
}
