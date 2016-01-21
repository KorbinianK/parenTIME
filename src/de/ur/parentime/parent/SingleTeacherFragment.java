package de.ur.parentime.parent;
/*
 * This class is responsible for the Handling of Appointment creations
 * The list shows the available times, depending on the Teacher and the current User
 */
import java.util.ArrayList;
import java.util.List;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import de.ur.korbinian.kasberger.R;
import de.ur.parentime.custom.Time;
import de.ur.parentime.custom.TimeListViewAdapter;
import de.ur.parentime.main.Constants;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SingleTeacherFragment extends Fragment {
	
	private TextView txtforename;
	private TextView txtsurname;
	private TextView txtsubject;
	private TextView update;
	private ListView listView;
	private ParseUser currentUser;
	
	private ProgressDialog mProgressDialog;
	private ProgressDialog mProgressDialogTeacher;

	private TimeListViewAdapter listAdapter;
	private Time confirmedTime;
	private String occTimeString;
	private String tFirstname;
	private String tLastname;
	private String tSubject1;
	private String tSubject2;
	private String tSubject3;
	private String teacherUsername;
	private ArrayList<Time> timesList;
	private ArrayList<Time> queriedTimes;
	private List<ParseObject> allTimesList;
	private List<ParseObject> hasDateList;
	private List<ParseObject> timesAvailable;
	private List<ParseObject> parentDateList;
	
	private boolean userHasDate;
	private boolean teacherNoDate;
	private int maxTimes;
	private Bundle bundle;
	
	private List<ParseObject> teacherDateList;
	private int teacherDateCount;
	private ArrayList<Integer> tOccslots;
	private ArrayList<Integer> pOccslots;
	
//	AsyncTasks
	private CurrentTeacher currTeacher;
	private TeacherList tList;
	private AddInDB add;
	private AvailableTimesTask availableTimes;
	private AllTimes allTimes;
	

//	Creates the View and fetches the clicked Teacher from the TeacherListFragment
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.single_teacher_view, container, false);
        listView = (ListView) rootView.findViewById(android.R.id.list);
        bundle = this.getArguments();
	 	teacherUsername = bundle.get("teacherUsername").toString();
	 	tFirstname = bundle.get(Constants.FIRSTNAME).toString();
		tLastname = bundle.get(Constants.LASTNAME).toString();
		tSubject1 = bundle.get(Constants.SUBJECT1).toString();
		if(bundle.containsKey(Constants.SUBJECT2)){
			tSubject2 = bundle.get(Constants.SUBJECT2).toString();
		}
		if(bundle.containsKey(Constants.SUBJECT3)){
			tSubject3 = bundle.get(Constants.SUBJECT3).toString();
		}
		setupUi(rootView);
		allTimes = new AllTimes();
		allTimes.execute();
		currTeacher = new CurrentTeacher();
		currTeacher.execute();
        return rootView;
	}
	
//	Enables the Actionbar items
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setRetainInstance(true);
	    setHasOptionsMenu(false);
	}
	
// Cancels AsyncTasks when leaving Fragment, prevents null
	public void onPause(){
		super.onPause();
		if (this.currTeacher != null && this.currTeacher.getStatus() == Status.RUNNING) {
			this.currTeacher.cancel(true);
			cancelToast();
		}if (this.tList !=null && this.tList.getStatus() == Status.RUNNING) {
			this.tList.cancel(true);
			cancelToast();
		}if (this.add !=null && this.add.getStatus() == Status.RUNNING) {
			this.add.cancel(true);
			cancelToast();
		}if (this.allTimes !=null && this.allTimes.getStatus() == Status.RUNNING) {
			this.allTimes.cancel(true);
			cancelToast();
		}if (this.availableTimes !=null && this.availableTimes.getStatus() == Status.RUNNING) {
			this.availableTimes.cancel(true);
			cancelToast();
		}
	}

//	Shows the user a Toast to notify him an AsyncTask has been canceled
	private void cancelToast() {
		Toast.makeText(getActivity(),
					"Vorgang abgebrochen",
					Toast.LENGTH_LONG).show();
		}

//		Setup for the UI items
	   	private void setupUi(View v) {
	   		txtsurname = (TextView) v.findViewById(R.id.surnametxt);
	   		txtforename = (TextView) v.findViewById(R.id.nametxt);
	   		txtsubject = (TextView) v.findViewById(R.id.subjecttxt);
	   		update = (TextView) v.findViewById(R.id.notimelisttxt);
	}

//	   	Asyntask handling UI updates and starting the loading of the TimeList
	   	private class CurrentTeacher extends AsyncTask<Void, Void, Void> {
			protected void onPreExecute() {	
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(getActivity());
				mProgressDialog.setTitle(getResources().getString(R.string.please_wait));
				mProgressDialog.setMessage(getResources().getText(R.string.loading));
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.show();
				showView();
			}

//			Updates the TextViews
			private void showView() {
				txtforename.setText(tFirstname);
   		   		txtsurname.setText(tLastname);
	   		   	if(tSubject1 != null && tSubject2 != null && tSubject3 == null){
	   		   	txtsubject.setText(
	   		   			tSubject1
	   		   			+Constants.SEPARATOR
	   		   			+tSubject2);
				}else if(tSubject2 != null && tSubject3 != null){
					txtsubject.setText(
							tSubject1
							+Constants.SEPARATOR
							+tSubject2+Constants.SEPARATOR
							+tSubject3);
				}else {
					txtsubject.setText(tSubject1);
				}
			}

			@Override
			protected Void doInBackground(Void... params) {	
				currentUser = ParseUser.getCurrentUser();
				timesList = new ArrayList<Time>();        
	            return null;
				
			}
			@Override
	   		protected void onPostExecute(Void result) {
				tList = new TeacherList();
	   			mProgressDialog.dismiss();
	   			tList.execute();
   	   		}
	}

//	   	AsyncTask to load all Time Objects from the Database in the Background
	   	private class AllTimes extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.TIMELINE);
            	query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            	query.addAscendingOrder(Constants.SLOTID);
            	try {
					allTimesList = query.find();
					maxTimes = allTimesList.size();
					Log.e("Debug", "alltimes count= " + maxTimes);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
	   		
	   	}
	   	
//	   	Asyntask to load the Times from the Database
	   	
	   	private class TeacherList extends AsyncTask<Void, Void, Void> {
	   	

//	   		Shows a progress Dialog to inform the user, that the data is loading
			protected void onPreExecute() {
				super.onPreExecute();
				mProgressDialogTeacher = new ProgressDialog(getActivity());
				mProgressDialogTeacher.setTitle(getResources().getString(R.string.please_wait));
				mProgressDialogTeacher
						.setMessage(getResources().getText(R.string.loading));
				mProgressDialogTeacher.setIndeterminate(false);
				mProgressDialogTeacher.setCanceledOnTouchOutside(false);
				mProgressDialogTeacher.show();
			}


//	   		Loads the available Times of the Teacher, removes the already filled Slots from the user and shows the remaining possibilities
			@SuppressLint("UseSparseArrays")
	   		@Override
	   		protected Void doInBackground(Void... params) {
                ParseQuery<ParseObject> hasDate = new ParseQuery<ParseObject>(Constants.APPIONT);
                hasDate.whereEqualTo(Constants.PARENTID, currentUser.getUsername());
                hasDate.whereEqualTo(Constants.TEACHERID, teacherUsername);
                try {
					hasDateList = hasDate.find();
				} catch (ParseException e) {
					e.printStackTrace();
				}
                if (hasDateList.isEmpty()){
                	userHasDate = false;
                	ParseQuery<ParseObject> teacherHasDates = new ParseQuery<ParseObject>(Constants.APPIONT);
                	teacherHasDates.addAscendingOrder(Constants.SLOTID);
                	try {
						teacherDateList = teacherHasDates.find();
						teacherDateCount = teacherDateList.size();
						Log.e("Debug", "teacher date count= " + teacherDateCount);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	if (maxTimes == teacherDateCount){
                		teacherNoDate = true;
                	}else{
                		teacherNoDate = false;
                		ParseQuery<ParseObject> pTimes = new ParseQuery<ParseObject>(Constants.APPIONT);
                		pTimes.addAscendingOrder(Constants.SLOTID);
                		try {
							parentDateList = pTimes.find();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                }else{
                	userHasDate = true;
                	ParseObject o = hasDateList.get(0);
                	occTimeString = o.getString(Constants.TIME);
                }
	   			return null;
	   		}
			
//			Dismisses the Progress Dialog and handles the display of information whether an appointment is saved 
			protected void onPostExecute(Void result) {
	   			mProgressDialogTeacher.dismiss();
					if(userHasDate){
						Log.e("Debug","alreadyDate");
						update.setVisibility(View.VISIBLE);
						update.setText(
	            			getResources().getString(R.string.alreadydate)
	            			+ Constants.SPACE
	            			+ occTimeString);
	
					}else if(teacherNoDate){
						Log.e("Debug","teacherNoDate");
						update.setVisibility(View.VISIBLE);
						update.setText(getResources().getString(R.string.nolistview));
					}else{
						availableTimes = new AvailableTimesTask();
	            		availableTimes.execute();
					}
	          }
		   	}
	   	
//	   	AsyncTask to get the available Times from the Database 
	   	private class AvailableTimesTask extends AsyncTask<Void, Void, Void>{
	   		
//	   		Shows a progress Dialog to inform the user, that the data is loading
			protected void onPreExecute() {
	   		super.onPreExecute();
			mProgressDialogTeacher = new ProgressDialog(getActivity());
			mProgressDialogTeacher.setTitle(getResources().getString(R.string.please_wait));
			mProgressDialogTeacher.setMessage(getResources().getText(R.string.loading));
			mProgressDialogTeacher.setIndeterminate(false);
			mProgressDialogTeacher.setCanceledOnTouchOutside(false);
			mProgressDialogTeacher.show();
		}

//			Connects the Adapter with the available Times and setup for the ItemClickListener
	   		@Override
	   		protected void onPostExecute(Void result) {
	   			mProgressDialogTeacher.dismiss();
   				listAdapter = new TimeListViewAdapter(getActivity(),timesList);
				listView.setAdapter(listAdapter);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
		   			public void onItemClick(AdapterView<?> parent, View view, int position,
		   					long id){
		   				ConfirmDialogFragment confirm = new ConfirmDialogFragment();
		   				confirm.show(getFragmentManager(), "Dialog");
		   				confirmedTime = timesList.get(position);
		   			}
				});

	   			mProgressDialogTeacher.dismiss();
	   		}

//	   		Asks the User for a Confirmation of the selected Time Slot, if confirmed starts another AsyncTask to add the Appointment to the Database
	   		@SuppressLint("ValidFragment")
		   	public class ConfirmDialogFragment extends DialogFragment {

		   		@Override
		   	    public Dialog onCreateDialog(Bundle savedInstanceState) {
		   	      
		   	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		   	        builder.setMessage(confirmedTime.getTime())        
		   	               .setTitle(R.string.dialog_confirm_date)
		   	               .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
		   	                
							public void onClick(DialogInterface dialog, int id) {
		   	                       add = new AddInDB();
		   	                	   add.execute();
		   	                	   dialog.dismiss();
		   	                   }
		   	               })
		   	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		   	                   public void onClick(DialogInterface dialog, int id) {
		   	                       
		   	                	   dialog.dismiss();
		   	                   }
		   	               });
		   	     
		   	        return builder.create();
		   	    }
		   	}

//	   		Queries the Data and calculates the available Times
			@Override
			protected Void doInBackground(Void... params) {
				tOccslots = new ArrayList<Integer>();
				pOccslots = new ArrayList<Integer>();
				for (int i = 0; i < teacherDateList.size(); i++){
					ParseObject o = teacherDateList.get(i);
					tOccslots.add(o.getInt(Constants.SLOTID));
				}
				for (int i = 0; i < parentDateList.size(); i++){
					ParseObject o = parentDateList.get(i);
					pOccslots.add(o.getInt(Constants.SLOTID));
				}
    			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.TIMELINE);
    			query.whereNotContainedIn(Constants.SLOTID, tOccslots);
    			query.whereNotContainedIn(Constants.SLOTID, pOccslots);
    			query.addAscendingOrder(Constants.SLOTID);
    			try {
					timesAvailable = query.find();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			queriedTimes = new ArrayList<Time>();
                for (ParseObject allTimes : timesAvailable){
                	Time time = new Time();
                	time.setSlot(allTimes.getInt(Constants.SLOTID));
                    time.setTeacher(tFirstname+Constants.SPACE+tLastname);
                    time.setTime(allTimes.getString(Constants.TIME));
                    queriedTimes.add(time);
                    } 
                timesList.addAll(queriedTimes);
				return null;
			}
	   	}
				
//	   	AsyncTask to add the selected Appointment to the Database
	   	private class AddInDB extends AsyncTask<Void, Void, Void> {
	   		
	   		protected void onPreExecute() {
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(getActivity());
				mProgressDialog.setTitle(getResources().getString(R.string.please_wait));
				mProgressDialog
						.setMessage(getResources().getText(R.string.loading));
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.show();
			}

//	   		Saves the Appointment in the Database
	   		@Override
	   		protected Void doInBackground(Void... params) {
   				ParseObject appointment = new ParseObject(Constants.APPIONT);
   				appointment.put(Constants.PARENTID, currentUser.getUsername());
   				appointment.put(Constants.TEACHER_FIRSTNAME,tFirstname);
   				appointment.put(Constants.TEACHER_LASTNAME, tLastname);
   				appointment.put(Constants.PARENT_FIRSTNAME, currentUser.getString(Constants.FIRSTNAME));
   				appointment.put(Constants.PARENT_LASTNAME, currentUser.getString(Constants.LASTNAME));
   				Log.d("Debug", "name= " +  currentUser.getString(Constants.FIRSTNAME)+currentUser.getString(Constants.LASTNAME));
   				appointment.put(Constants.TIME, confirmedTime.getTime());
   				appointment.put(Constants.SLOTID, confirmedTime.getSlot());
   				appointment.put(Constants.TEACHERID, teacherUsername);
   				
   				appointment.saveInBackground();
	   			return null;
	   		}
	   		
//	   		Disables the ListView, notifies the User the Appointment has been saved
	   		@Override
	   		protected void onPostExecute(Void result) {
	   			listView.setVisibility(View.INVISIBLE);
	   			Toast.makeText(getActivity(),
	   					getResources().getString(R.string.saved),
	   					Toast.LENGTH_LONG).show();
	   			update.setVisibility(View.VISIBLE);
	   			update.setText(getResources().getString(R.string.saved));
	   			mProgressDialog.dismiss();
	   		}
	   		
	   	}
	}
