package de.ur.parentime.parent;

/*
 * This class displays the User a list of Teachers
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseQuery.CachePolicy;

import de.ur.korbinian.kasberger.R;
import de.ur.parentime.custom.TeacherListArrayAdapter;
import de.ur.parentime.main.Constants;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TeacherListFragment extends Fragment {
	
	protected static final String TAG = "Debug";
	protected ListView listView;
	protected List<ParseUser> ob;
	protected ProgressDialog mProgressDialog;
	protected TeacherListArrayAdapter adapter;
	protected String teacherUsername;
	protected String teacher;
	protected String subjects;
	protected String subject1;
	protected String subject2;
	protected String subject3;
	protected String firstName;
	protected String lastName;
	protected Menu optionsMenu;
	protected CachePolicy cache = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK;
	protected long cacheTime = TimeUnit.MINUTES.toMillis(15);
	protected Bundle data;
	protected boolean requestRunning = false;
	private RemoteDataTask remoteData;

//	Enables the Actionbar items
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
//	Creates the View
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teacher_list_layout, container, false);     
        listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setClickable(true);
        remoteData = new RemoteDataTask();
        remoteData.execute();   
		registerListener();
        return rootView;
    }
	

	
//	Stops asynctask when User leaves the Fragment, prevents NullPointer
	@Override
	public void onPause(){
		super.onPause();
		if (this.remoteData != null && this.remoteData.getStatus() == Status.RUNNING) {
			this.remoteData.cancel(true);
			Toast.makeText(getActivity(),
   					"Vorgang abgebrochen",
   					Toast.LENGTH_LONG).show();
		}
	}
	
//	Hides all Actionbar items by default, when Menu is created
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(false);
        }
	}
	
//	Setup when menu is loaded again, shows only the refresh button
	@Override
    public void onPrepareOptionsMenu(Menu menu) {		
		MenuItem refresh = menu.findItem(R.id.refresh);
		refresh.setVisible(true);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		refresh.setIcon(R.drawable.ic_action_refresh);
		menu.findItem(R.id.help)
		.setVisible(true);
    }
	
//	Handles the click on actionbar items
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch(item.getItemId()){
		 case R.id.refresh:
			 startRefresh();
			 break;
		 }
		return true;	 
	 }
	 
//	Starts the AsyncTask to load the Teacher from Database
	private void startRefresh() {
		if ( !requestRunning ) {
			TeacherListFragment.this.requestRunning = true ;
			ParseQuery.clearAllCachedResults();
			new RemoteDataTask().execute();
			this.setRefreshActionButtonState(true);	
		}
		else {
			Log.d(TAG,"request already running");
		}	
	}

// Handles the state of the Action Button
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
	
//	Gets data of the clicked Teacher
	private void registerListener() {
		
			listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listView.setEnabled(false);
				ParseObject clickedTeacher = ob.get(position);
				firstName = clickedTeacher.getString(Constants.FIRSTNAME);
				lastName = clickedTeacher.getString(Constants.LASTNAME);
				subject1 = clickedTeacher.getString(Constants.SUBJECT1);
				subject2 = clickedTeacher.getString(Constants.SUBJECT2);
				subject3 = clickedTeacher.getString(Constants.SUBJECT3);
				teacherUsername = clickedTeacher.getString(Constants.USERID);
				data = new Bundle();
		        data.putString("teacher",teacher);
		        data.putString(Constants.SUBJECT1,subject1);
		        if(subject2!=null){
		        	data.putString(Constants.SUBJECT2,subject2);
		        }
		        if(subject3!=null){
		        	data.putString(Constants.SUBJECT3,subject3);
		        }
		        data.putString(Constants.FIRSTNAME,firstName);
		        data.putString(Constants.LASTNAME,lastName);
		        data.putString("teacherUsername",teacherUsername);
		        Log.d(TAG, "data" + teacher + subject1 + subject2 +subject3 + teacherUsername);
				Fragment fragment = new SingleTeacherFragment();
				fragment.setArguments(data);
				android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
		        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		        transaction.replace(R.id.content_frame, fragment);
		        transaction.addToBackStack("Single Teacher");
		        transaction.commit();   
			}
		});
	}
	

//	Gets Teacherlist from Database in the Background
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		@Override
		
//		Shows the user a Progress Dialog while Data is loading
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle(getResources().getText(R.string.please_wait));
			mProgressDialog.setMessage(getResources().getText(R.string.loading));
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

//		Loads the Teacher in the background, sorts them alphabetical
		@Override
		protected Void doInBackground(Void... params) {
			
			ParseQuery<ParseUser> query = ParseUser.getQuery();
			query.whereEqualTo(Constants.ISTEACHER, true);
			query.addAscendingOrder(Constants.LASTNAME);
			query.setCachePolicy(cache);
			query.setMaxCacheAge(cacheTime);
			try {
				ob  = query.find();
				adapter = new TeacherListArrayAdapter(getActivity(), ob);
			} catch (ParseException e) {
				e.printStackTrace();
			}		
			return null;
		}

//		Binds adapter to List, dismisses the Progress Dialog
		@Override
		protected void onPostExecute(Void result) {
			requestRunning = false;
			listView.setAdapter(adapter);
			mProgressDialog.dismiss();
		}
	}
	
}
