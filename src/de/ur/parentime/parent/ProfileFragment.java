package de.ur.parentime.parent;

/*
 * This class handles the Display and sharing of the Appointments a User has
 */

import java.util.ArrayList;
import java.util.List;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.timroes.swipetodismiss.SwipeDismissList;
import de.ur.korbinian.kasberger.R;
import de.ur.parentime.custom.Appointment;
import de.ur.parentime.custom.ListViewAdapter;
import de.ur.parentime.main.Constants;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

	private static final String TAG = "Debug";
	private TextView firstName;
	private TextView lastName;
	private TextView appointNumber;
	private String firstNameStr;
	private String lastNameStr;
	private ParseUser currentUser;
	private ListView listView;
	private SwipeDismissList mSwipeList;
	private ProgressDialog mProgressDialog;
	private Appointment deletedItem;
	private ImageView titleImage;
	protected ParseQuery<ParseObject> results;
	private ArrayList<Appointment> appointmentList;
	protected List<ParseObject> ob;
	private ListViewAdapter listAdapter;
	private final String PREFS_NAME = "MyPrefsFile";
	private SharedPreferences settings;
	private ShareActionProvider actionProvider;
	private RemoteDataTask remoteData;
	private UndoAddDB undo;
	private ArrayList<String> appointments;
	protected int appointmentCount;
	private int type;
	protected boolean helpItemRemoved = false;
	private boolean showListView;
	private boolean firstSwipe;
	private TextView noDate;
	
//	Enables the Actionbar items
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);  
	}
	
//	Creates the View, checks if the User has swiped before
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		getActivity().invalidateOptionsMenu();
        View rootView = inflater.inflate(R.layout.profile, container, false);
        setupUI(rootView);
        getData();
        remoteData = new RemoteDataTask();
		remoteData.execute();
        settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("firstSwipe", true)) {  
        	firstSwipe = true;
            Log.d(TAG, "not swiped yet");
        }
        return rootView;
    }
	
	
//	Stops all AsyncTasks when User leaves Fragment
	@Override
	public void onPause(){
		super.onPause();
		if (this.remoteData != null && this.remoteData.getStatus() == Status.RUNNING) {
			this.remoteData.cancel(true);
			Toast.makeText(getActivity(),
   					"Vorgang abgebrochen",
   					Toast.LENGTH_LONG).show();
		}if (this.undo !=null && this.undo.getStatus() == Status.RUNNING) {
			this.undo.cancel(true);
			Toast.makeText(getActivity(),
   					"Vorgang abgebrochen",
   					Toast.LENGTH_LONG).show();
		}
	}
 

//	Discards the Undo Cache
	@Override
	public void onStop() {
		super.onStop();
		if(mSwipeList != null){
			mSwipeList.discardUndo();
		}
	}
	
//	Loads the UI elements for later use
	private void setupUI(View v) {
		firstName = (TextView) v.findViewById(R.id.profile_firstname);
		lastName = (TextView) v.findViewById(R.id.profile_lastname);
		noDate = (TextView) v.findViewById(R.id.no_appointments_yet);
		appointNumber = (TextView) v.findViewById(R.id.appointments_number);
		titleImage = (ImageView) v.findViewById(R.id.account_title_background);
		titleImage.setImageDrawable(getResources().getDrawable(R.drawable.acc_bg));	
		listView = (ListView) v.findViewById(android.R.id.list);
		listView.setSelector(android.R.color.transparent);
		listView.setCacheColorHint(android.R.color.transparent);
	}

//	Gets the current user and adjusts the TextViews accordingly
	private void getData() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		firstNameStr = currentUser.getString(Constants.FIRSTNAME);    
		lastNameStr = currentUser.getString(Constants.LASTNAME);
		lastName.setText(lastNameStr);
		firstName.setText(firstNameStr);
	}

//	Hides all Actionbar items by default
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(false);
        }
        ((ParentMainActivity )  getActivity()).setShareIntent(createShareIntent());
	}
	
//	Loads and enables Actionbar items when Menu is loaded again, sets the Share Button Listener
	@Override
    public void onPrepareOptionsMenu(Menu menu) {
		MenuItem refresh = menu.findItem(R.id.refresh);
		refresh.setVisible(false);
		MenuItem share = menu.findItem(R.id.menu_item_share);
		share.setVisible(true);
		share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
		menu.findItem(R.id.help)
		.setVisible(true)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

    }

//	External AsyncTask to load Data from the Database
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

		private List<ParseObject> queryList;

//		Shows the user a Progress Dialog while the Data gets loaded
		protected void onPreExecute() {	
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle(getResources().getText(R.string.please_wait));
			mProgressDialog.setMessage(getResources().getText(R.string.loading));
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}

//		Loads the Appointments the user has in the Background
		@Override
		protected Void doInBackground(Void... params) {
			appointmentList = new ArrayList<Appointment>();
			currentUser = ParseUser.getCurrentUser();
			int i = 0;

			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.APPIONT);
			query.whereEqualTo(Constants.PARENTID, currentUser.getUsername());
			query.addAscendingOrder(Constants.SLOTID);
			try {
				queryList = query.find();
				Log.i(TAG, "queryList= " + queryList);
				for(ParseObject o : queryList){
					Appointment a = new Appointment();
					appointmentCount = queryList.size();
					a.setSlot(o.getInt(Constants.SLOTID));
					a.setTeacherUsername(o.getString(Constants.TEACHERID));
					a.setTeacherFirstName(o.getString(Constants.TEACHER_FIRSTNAME));
					a.setTeacherLastName(o.getString(Constants.TEACHER_LASTNAME));
					a.setParentFirstName(o.getString(Constants.PARENT_FIRSTNAME));
					a.setParentLastName(o.getString(Constants.PARENT_LASTNAME));
					a.setTime(o.getString(Constants.TIME));
					appointmentList.add(i,a);
					i++;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!appointmentList.isEmpty()){
            	showListView = true;
            	if(firstSwipe){
  		   			type = 0;	
      		   		Appointment appoint = new Appointment();
      		   		appoint.setId("firstSwipe");
      		   		appoint.setTime("");
					appoint.setTeacher("");
   					appointmentList.add(0, appoint);
      		   		Log.d(TAG,"type" + type);
      		   	}else {
      		   		type = 1;
      		   		Log.d(TAG,"type" + type);
      		   		}
            }else {
            	showListView = false;
            }
			return null;
		}

//		Connects the adapter to the List, if the List is not empty.
		@Override
   		protected void onPostExecute(Void result) {
			if (showListView){
				setupShare();
				appointNumber.setText(Integer.toString(appointmentCount));
				listAdapter = new ListViewAdapter(getActivity(),appointmentList, type);
				listView.setAdapter(listAdapter);
				listView.setClickable(false);
   			}else if(!showListView){
   				noDate.setVisibility(View.VISIBLE);
   				noDate.setText(getResources().getString(R.string.no_date_yet));
   			}
			swipeWithUndoListener();
			mProgressDialog.dismiss();
   		}
	}
	
//	Creates the Swipe Listener to handle the Deletion of list items
	private void swipeWithUndoListener() {
		int modeInt = 0;
		
		SwipeDismissList.UndoMode mode = SwipeDismissList.UndoMode.values()[modeInt];
		mSwipeList = new SwipeDismissList(
		listView,
		new SwipeDismissList.OnDismissCallback() {

		private int counter;
		

		public SwipeDismissList.Undoable onDismiss(AbsListView listView, final int position) {
			
				final Appointment item = appointmentList.get(position);
				counter = appointmentCount;
				mSwipeList.setAutoHideDelay(4000);
				mSwipeList.setRequireTouchBeforeDismiss(false);
				mSwipeList.setSwipeDisabled(true);
				deletedItem = item;
				int slot = deletedItem.getSlot();
				counter--;	
				if(deletedItem.getId() == "firstSwipe"){
					firstSwipe = false;
					type = 1;
					settings.edit().putBoolean("firstSwipe", false).commit();
					listAdapter.remove(deletedItem);
					listAdapter.remove(deletedItem);
					listAdapter.notifyDataSetChanged();
					resetAdapter();
					mSwipeList.setSwipeDisabled(false);
					return null;
				}else {
					if(firstSwipe){
						firstSwipe = false;
						helpItemRemoved = true;
						Appointment helpitem = appointmentList.get(0);
						listAdapter.remove(helpitem);
						listAdapter.remove(helpitem);
					}
					appointmentList.remove(deletedItem);
					appointNumber.setText(Integer.toString(counter));
					listAdapter.remove(deletedItem);
					listAdapter.notifyDataSetChanged();
					ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.APPIONT);
					query.whereEqualTo(Constants.SLOTID, slot);
					query.whereEqualTo(Constants.PARENTID, currentUser.getUsername());
					query.findInBackground(new FindCallback<ParseObject>() {
		                @Override
		                public void done(List<ParseObject> deletedItemList, ParseException e) {
		                	
		                	mSwipeList.setSwipeDisabled(false);
		                    if(deletedItemList.size()!=0)
		
		                        deletedItemList.get(0).deleteEventually(new DeleteCallback() {
		
									@Override
									public void done(ParseException e) {
										
									}
								});
		                }
				 });
					
					return new SwipeDismissList.Undoable() {
						
						private int pos;

						@Override
						public String getTitle() {
							return "Termin gelöscht";
						}
						
//						Starts new AsyncTask to undo the Deletion
						@Override
						public void undo() {
							counter++;
							if(helpItemRemoved){
								pos = position-1;
							}else{
								pos = position;
							}
							appointNumber.setText(Integer.toString(counter));
							appointmentList.add(pos, deletedItem);
							undo = new UndoAddDB();
							undo.execute();
							listAdapter.notifyDataSetChanged();	
							setupShare();
						}
		
						@Override
						public void discard() {
						}
					};
				}
		
			}
		},mode);
	
//	Handles multiple Undo. (Disabled)
	if (mode == SwipeDismissList.UndoMode.MULTI_UNDO) {
		mSwipeList.setUndoMultipleString(null);
	}
	
	// Just reset the adapter.
	resetAdapter();
	
	}
	
//	Sends shareIntent to MainActivity, clears cache
	private void setupShare() {
		Log.d(TAG,"setup share");
		ParseQuery.clearAllCachedResults();
		setShareIntent(shareIntent());
	}
	
//	Sets the Shared intent
	public void setShareIntent(Intent shareIntent) {
        if (actionProvider != null) {
        	actionProvider.setShareIntent(shareIntent);
        }
    }

//	Dummy share intent to initialize sharing
	protected Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        return shareIntent;
    }
	
//	Share Intent with the information from the Appointment List
	private Intent shareIntent() {
		int size = appointmentList.size(); 
   	 	appointments = new ArrayList<String>();
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("message/rfc822");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{currentUser.getEmail()});
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        if(size != 0){
	     	for (int pos = 0; pos < size; pos++){
	     		Appointment getApp = appointmentList.get(pos);
	     		String time = getApp.getTime();
	     		String teacher = getApp.getTeacherLastName() + Constants.SPACE + getApp.getTeacherFirstName();
	     		appointments.add(
	     				time
	     				+ "\n"
	     				+ teacher
	     				+ "\n"
	     				);
         	}
         	StringBuilder out = new StringBuilder();
         	for(int a = getInt(); a < appointments.size();a++){
         		out.append(appointments.get(a));
         		out.append("\n");
         	}
         	
         	shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.termine));
         	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, out.toString());	 
         }
         else {
        	shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        }
		return shareIntent;
	}

//	Sets the Start of the iteration depending on whether or not the user has swiped yet
	private int getInt() {
		if(firstSwipe){
			Log.d(TAG, "0");
    		return 0;
    	}else{
    		Log.d(TAG, "0");
    		return 0;
    	}
	}
	
//	Throws away the Undo, reloads the Adapter
	private void resetAdapter() {
		mSwipeList.discardUndo();
		listView.setAdapter(listAdapter);
	}
	

//	Creates a new Appointment ,identical to the one deleted, in the Background
	private class UndoAddDB extends AsyncTask<Void, Void, Void> {
		
//		Shows the User a Progress Dialog while Data is saved
   		protected void onPreExecute() {
   			super.onPreExecute();	
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setTitle(getResources().getText(R.string.please_wait));
			mProgressDialog.setMessage(getResources().getText(R.string.loading));
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}
   		
//   	Dismisses the Progress Dialog, notifies the User that the Undo is done
   		@Override
   		protected void onPostExecute(Void result) {
   			Toast.makeText(getActivity(),
   					getResources().getString(R.string.undo_done),
   					Toast.LENGTH_LONG).show();
   			mProgressDialog.dismiss();
   			setupShare();
   		}

//   	Creates the Appointment in the Background
   		@Override
   		protected Void doInBackground(Void... params) {
   			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
   			
			ParseObject appointment = new ParseObject(Constants.APPIONT);
			appointment.put(Constants.PARENTID, currentUser.getUsername());
			appointment.put(Constants.SLOTID, deletedItem.getSlot());
			appointment.put(Constants.TEACHERID, deletedItem.getTeacherUsername());
			appointment.put(Constants.TIME, deletedItem.getTime());
			appointment.put(Constants.TEACHER_FIRSTNAME, deletedItem.getTeacherFirstName());
			appointment.put(Constants.TEACHER_LASTNAME, deletedItem.getTeacherLastName());
			appointment.put(Constants.PARENT_FIRSTNAME, deletedItem.getParentFirstName());
			appointment.put(Constants.PARENT_LASTNAME, deletedItem.getParentLastName());
			appointment.saveInBackground();
   				
   			return null;
   		}
   		
   	}

}