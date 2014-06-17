package com.group15.thermal.app;

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
import android.widget.Toast;

import com.group15.thermal.webservice.CorruptWeekProgramException;
import com.group15.thermal.webservice.Heating;
import com.group15.thermal.webservice.WeekProgram;

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


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

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
	public static WeekProgram weekProgram=null;
	public static int currentFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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
	            currentFragment=position;

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
                            .setTabListener(this));
//		    getSupportFragmentManager().beginTransaction().add(mSectionsPagerAdapter
//			        .getItem(i), Integer.toString(i)).commit();

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
					showToast("Connected!",1);
					try {
						WeekProgram wp = Heating.getWeekProgram();
						weekProgram = wp;
						if( wp.get_nr_switches_active(1)!=10){
							wp.setDefault();
							Heating.setWeekProgram(wp);
						}
					} catch (ConnectException e) {
						e.printStackTrace();
					} catch (CorruptWeekProgramException e) {
						e.printStackTrace();
					}
				}else showToast("Unable to connect to web service!",1);
			}
		});
		t.start();
		t.join();
	}

	public void showToast(final String toast,final Integer length) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				/* TODO Auto-generated method stub */
				Toast.makeText(MainActivity.this, toast, length==1?Toast.LENGTH_SHORT:Toast.LENGTH_LONG).show();

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
	        ((OnRefreshListener)mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem())).onRefresh();

	        return true;
        }
	    if(id == R.id.refreshall){
		    recreate();
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

  /*  *//**
     * A placeholder fragment containing a simple view.
     *//*
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener,OnRefreshListener {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

	    int[] buttons = {R.id.switchbtn1,R.id.switchbtn2, R.id.switchbtn3,
			    R.id.switchbtn4,R.id.switchbtn5,R.id.switchbtn6,R.id.switchbtn7
	            ,R.id.switchbtn8,R.id.switchbtn9,R.id.switchbtn10};
	    Button usedbutton,savebtn;
	    ArrayList<String> day2night= new ArrayList<String>(5);
	    ArrayList<String> night2day= new ArrayList<String>(5);
		TextView titleview;



	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    View rootView = inflater.inflate(R.layout.fragment_main, container, false);


		    //Toast.makeText(getActivity(),getTitle(getArguments().getInt(ARG_SECTION_NUMBER))+" loaded",Toast.LENGTH_SHORT).show();
		    titleview = (TextView)rootView.findViewById(R.id.section_label);
		    titleview.setText(getTitle(getArguments().getInt(ARG_SECTION_NUMBER)-1) + " Settings");
		    return rootView;
	    }
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
		   // updateOnPageChange();
		    super.onViewCreated(view, savedInstanceState);
	    }

	    @Override
	    public void onClick(View view) {
			for(Integer button : buttons){
				if(button.equals(view.getId())) {
					usedbutton = (Button) getFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+getTitle(getArguments().getInt(ARG_SECTION_NUMBER)-1))
							.getActivity().findViewById(button);
					savebtn = (Button)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+getTitle(getArguments().getInt(ARG_SECTION_NUMBER)-1))
							.getActivity().findViewById(R.id.savebtn);
					System.out.println("Clicked!");
					break;
				}
			}
		    TimePickerDialog.OnTimeSetListener gettime;
		    gettime = new TimePickerDialog.OnTimeSetListener() {
			    @Override
			    public void onTimeSet(TimePicker timePicker, int i, int i2) {
					String time;
				    time = i<10?"0"+Integer.toString(i):Integer.toString(i);
				    time = time +":"+ (i2<10?"0"+Integer.toString(i2):Integer.toString(i2));
				    usedbutton.setText(time);
				    savebtn.setVisibility(View.VISIBLE);
			    }
		    };
		    TimePickerDialog picker = new TimePickerDialog(getActivity(), gettime,
				    new Integer(usedbutton.getText().toString().split(":")[0]),
				    new Integer(usedbutton.getText().toString().split(":")[1]), true);
		    picker.show();


	    }
		private void updateOnPageChange(){
			System.out.println(currentFragment);
			System.out.println(getTitle(currentFragment));
			for(Switch swbtn : weekProgram.getDaySwitches(getTitle(currentFragment))){
				if(swbtn.getType().equalsIgnoreCase("day")){
					night2day.add(swbtn.getTime());
				}else{
					day2night.add(swbtn.getTime());
				}
			}
			for (int i=0; i<5;i++){
				Button z = (Button)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+(getArguments().getInt(ARG_SECTION_NUMBER)-1))
					.getView().findViewById(buttons[i + 5]);
				Button y = (Button)getFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+(getArguments().getInt(ARG_SECTION_NUMBER)-1))
						.getView().findViewById(buttons[i]);
				z.setText(night2day.get(i));
				y.setText(day2night.get(i));
				z.setOnClickListener(this);
				y.setOnClickListener(this);
			}
		}

	    private String getTitle(Integer a){
		    switch (a) {
			    case 0:
				    return getString(R.string.title_section1);
			    case 1:
				    return getString(R.string.title_section2);
			    case 2:
				    return getString(R.string.title_section3);
			    case 3:
				    return getString(R.string.title_section4);
			    case 4:
				    return getString(R.string.title_section5);
			    case 5:
				    return getString(R.string.title_section6);
			    case 6:
				    return getString(R.string.title_section7);
		    }
			return "?";
	    }
	    @Override
	    public void onRefresh() {

			while(!this.isAdded()){
				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		    updateOnPageChange();
		    Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
	    }
    }
*/
}
