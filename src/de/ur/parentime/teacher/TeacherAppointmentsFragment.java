package de.ur.parentime.teacher;

/*
 * This class shows the Teacher all appointments including Times and Names of the parents 
 * in a List, sorted by time. This list can also be shared via email
 */

import java.util.ArrayList;
import java.util.List;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.ur.korbinian.kasberger.R;
import de.ur.parentime.custom.Appointment;
import de.ur.parentime.custom.ListViewAdapter;
import de.ur.parentime.main.Constants;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;


public class TeacherAppointmentsFragment extends Fragment {
	
//	Variables
	private ArrayList<Appointment> appointmentList;
	private ListView listView;
	private ProgressDialog mProgressDialog;
	private RemoteDataTask remote;
	private ListViewAdapter listAdapter;
	private ShareActionProvider actionProvider;

//	Creates the View
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teacher_list_layout, container, false);     
       setupUI(rootView);
       remote = new RemoteDataTask();
       remote.execute();
       return rootView;
    }
	
//	Enables actionbar items on start
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}

//	Hides all actionbar items on default
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(false);
        }
       
	}
	
//	Setup for the Actionbar, includes clicklistener
	@Override
    public void onPrepareOptionsMenu(Menu menu) {
		MenuItem share = menu.findItem(R.id.menu_item_share);
		share.setVisible(true);
		share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		share.setIcon(R.drawable.ic_action_share);
		actionProvider = (ShareActionProvider) share.getActionProvider();
		actionProvider.setShareIntent(createShareIntent());
		actionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
			
			@Override
			public boolean onShareTargetSelected(ShareActionProvider source,
					Intent intent) {
					setupShare();				
					return false;
			}
		});
    }
	
//	sets the shared intent
	public void setShareIntent(Intent shareIntent) {
        if (actionProvider != null) {
        	actionProvider.setShareIntent(shareIntent);
        }
    }
	
	 public boolean onOptionsItemSelected(MenuItem item) {
		return true;	 
	 }
	 
//	 Sets the shared intent to the intent with the data
	private void setupShare() {
		setShareIntent(shareIntent());
	}
	
//	 Creates dummy share, to enable share intent
	 protected Intent createShareIntent() {
	        Intent shareIntent = new Intent(Intent.ACTION_SEND);
	        shareIntent.setType("text/plain");
	        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
	        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
	        return shareIntent;
	    }
		
//	 Intent with the Data the user wants to share
		private Intent shareIntent() {
			Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
	        shareIntent.setType("text/plain");
	        int size = appointmentList.size(); 
	        ArrayList<String> appointments = new ArrayList<String>();
	        if(size != 0){
	        	for (int o = 0;o<size;o++){
	        		Appointment getApp = appointmentList.get(o);
	        		String time = getApp.getTime();
	        		String parent = getApp.getParentLastName()+Constants.SPACE+getApp.getParentFirstName();
	        		appointments.add(
	        				time
	        				+ "\n"
	        				+ parent
	        				+ "\n"
	        				);
	        	}
	        	StringBuilder out = new StringBuilder();
	        	for(int a = 0; a < appointments.size();a++){
	        		out.append(appointments.get(a));
	        		out.append("\n");
	        	}
	        	shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.termine));
	        	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, out.toString());
		        }else{
		        	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		        }
			return shareIntent;
		}

//	Loads the layout xml
	private void setupUI(View v) {
		listView = (ListView) v.findViewById(R.id.listview);
	}

//	AsyncTask to get the Data from the Database
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		private ArrayList<Appointment> queriedAppointments;
		private List<ParseObject> tList;

//		Shows a Progress Dialog while Data is loading
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle(getResources().getText(R.string.app_name));
			mProgressDialog.setMessage(getResources().getText(R.string.loading));
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
			
		}

//		Fetches the Data in the Background
		@SuppressLint("UseSparseArrays")
		@Override
		protected Void doInBackground(Void... params) {
			appointmentList = new ArrayList<Appointment>();  
		
			ParseUser currentUser = ParseUser.getCurrentUser();
			ParseQuery<ParseObject> teacherAppointments = ParseQuery.getQuery(Constants.APPIONT);
			teacherAppointments.whereEqualTo(Constants.TEACHERID, currentUser.getUsername());
			teacherAppointments.addAscendingOrder(Constants.SLOTID);
			try {
				tList = teacherAppointments.find();
				queriedAppointments = new ArrayList<Appointment>(); 
				for(ParseObject o : tList){
					Appointment app = new Appointment();
					app.setTime(o.getString(Constants.TIME));
					app.setParentFirstName(o.getString(Constants.PARENT_FIRSTNAME));
					app.setParentLastName(o.getString(Constants.PARENT_LASTNAME));
					queriedAppointments.add(app);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			appointmentList.addAll(queriedAppointments);
			return null;
		}

//		Connects custom Adapter to the List and cancels the progress dialog
		@Override
		protected void onPostExecute(Void result) {
			setupShare();
			listAdapter = new ListViewAdapter(getActivity(),appointmentList,3);
			listView.setAdapter(listAdapter);
			mProgressDialog.dismiss();

		}
	}
	
}
