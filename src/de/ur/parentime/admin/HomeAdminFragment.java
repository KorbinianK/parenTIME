package de.ur.parentime.admin;

/*
 * Home Fragment for the Admin, shows useful information like:
 * Last user created, amount of users/appointments etc
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.ur.korbinian.kasberger.R;
import de.ur.parentime.main.Constants;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeAdminFragment extends Fragment {
	
//	Declares variables
	private static final String TAG = "Debug";
	private TextView lastAppoint;
	private TextView lastUser;
	private List<ParseUser> latestUser;
	private List<ParseObject> latestAppointment;
	private int teacherCount;
	private int parentCount;
	private TextView userCountView;
	private TextView teacherCountView;
	private ParseUser lastUs;
	private boolean requestRunning = false;
	private ParseObject lastApp;
	private Menu optionsMenu;
	private TextView appCountView;
	
//	Creates View
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.admin_home, container, false);     
        setupUI(rootView);
        new RemoteDataTask().execute();
        return rootView;
    }
	
//	Enables the actionbar items
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    getActivity().invalidateOptionsMenu();  
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin, menu);
	}
	
	@Override
    public void onPrepareOptionsMenu(Menu menu) {
		MenuItem refresh = menu.findItem(R.id.refresh);
		refresh.setVisible(true);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		refresh.setIcon(R.drawable.ic_action_refresh);
    }
	
//	Empties the cache when Fragment gets destroyed
	public void onDestroy (){
		super.onDestroy();
		ParseQuery.clearAllCachedResults();
	}
	
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		  
	    	switch (item.getItemId()) {
	        case R.id.refresh:
	        	Log.d(TAG, "clicked refresh");
	        	this.startRefresh();
	        	
	        return true;
	        }
	          return super.onOptionsItemSelected(item);
	    }
	  
	  private void startRefresh() {
			if ( !requestRunning ) {
				HomeAdminFragment.this.requestRunning = true ;
				ParseQuery.clearAllCachedResults();
				new RemoteDataTask().execute();
				this.setRefreshActionButtonState(true);	
			}
			else {
				Log.d(TAG,"request already running");
			}
		}

	public void setRefreshActionButtonState(final boolean refreshing) {
	        if (optionsMenu != null) {
	            final MenuItem refreshItem = optionsMenu
	                .findItem(R.id.refresh);
	            if (refreshItem != null) {
	                if (refreshing) {
	                    refreshItem.setActionView(R.layout.actionbar_progress);
	                } else {
	                    refreshItem.setActionView(null);
	                }
	            }
	        }
	    }
	
//	Gets the amount of teacher accounts
	private void getTeacherCount() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
		query.whereEqualTo(Constants.ISTEACHER, true);
		try {
			teacherCount = query.count();
			Log.d(TAG,"teacher =" +teacherCount);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	Gets the amount of User Accounts
	private void getUserCount() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
		query.whereEqualTo(Constants.ISTEACHER, false);
		try {
			parentCount = query.count();
			Log.d(TAG,"parent= " +parentCount);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	Loads the UI Elements
	private void setupUI(View v) {
       lastUser = (TextView) v.findViewById(R.id.last_user_date);
       lastAppoint = (TextView) v.findViewById(R.id.last_appointment_date);
       userCountView = (TextView) v.findViewById(R.id.userCount);
       teacherCountView = (TextView) v.findViewById(R.id.teacherCount);
       appCountView = (TextView) v.findViewById(R.id.appointCount);
	}
	
//	Gets the time when the last user was created
	private void getLastUser() {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
		query.addDescendingOrder(Constants.CREATE);
		try {
			latestUser = query.find();
			lastUs = latestUser.get(0);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
//	Gets the time when the last appointment was created or updated
	private void getLastAppoint() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.APPIONT);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
		query.addDescendingOrder(Constants.UPDATE);
		try {
			latestAppointment = query.find();
			lastApp = latestAppointment.get(0);
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
//	AsyncTask to load the Data from the Database in the Background
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		private int appCount;

//		Sets the TextViews to a loading text while Data is getting load, does not block the UI with an Alert Dialog
		@Override
		protected void onPreExecute() {
			setRefreshActionButtonState(true);
			lastUser.setText(R.string.loading);
			lastAppoint.setText(R.string.loading);
			userCountView.setText(R.string.loading);
			teacherCountView.setText(R.string.loading);
			super.onPreExecute();
		}

//		Fetches the Data in the Background
		@Override
		protected Void doInBackground(Void... params) {
			getLastUser();
			getLastAppoint();
			getUserCount();
			getTeacherCount();
			getAppointCount();
			return null;
		}

//		Gets the number of Appointments
		private void getAppointCount() {
			ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.APPIONT);
			try {
				appCount = query.count();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		Sets the TextViews to the retrieved Data
		@Override
		protected void onPostExecute(Void result) {
			requestRunning = false;
			setRefreshActionButtonState(false);
			String dateUs = lastUs.getCreatedAt().toString();
			lastUser.setText(dateUs);
			String dateApp = lastApp.getUpdatedAt().toString();
			lastAppoint.setText(dateApp);
			appCountView.setText(Integer.toString(appCount));
			userCountView.setText(Integer.toString(parentCount));
			teacherCountView.setText(Integer.toString(teacherCount));
		}
	}
	
}
