package de.ur.parentime.admin;

/*
 * This class handles the creation of new teacher accounts
 */

import java.util.Random;

import com.parse.ParseException;
import com.parse.ParseUser;
import de.ur.korbinian.kasberger.R;
import de.ur.parentime.main.Constants;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class CreateTeacherFragment extends Fragment {
	
//	Declare Variables
	private static final String TAG = "Debug";
	private EditText firstName;
	private EditText lastName;
	private EditText password;
	private EditText subject1;
	private EditText subject2;
	private EditText subject3;
	private Button button;
	private EditText email;
	private Random r;
	private int userRandom;
	protected String username;
	protected String lastNameStr;
	protected String passwordStr;
	protected String emailStr;
	protected String subject1Str;
	protected String firstNameStr;
	protected String subject2Str;
	protected String subject3Str;
	
	private ProgressDialog mProgressDialog;
	private TextView showUsername;
	private TextView showPassword;
	private TextView required_title;

//	Creates the View
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.admin_teacher_list, container, false);     
       setupUI(rootView);
       registerListener();
        return rootView;
    }
	
//	Enables Actionbar items
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}

//	Loads the UI Elements
	private void setupUI(View v) {
		firstName = (EditText) v.findViewById(R.id.create_firstname);
		lastName = (EditText) v.findViewById(R.id.create_lastname);
		password = (EditText) v.findViewById(R.id.create_password);
		email = (EditText) v.findViewById(R.id.create_email);
		subject1 = (EditText) v.findViewById(R.id.create_subject1);
		subject2 = (EditText) v.findViewById(R.id.create_subject2);
		subject3 = (EditText) v.findViewById(R.id.create_subject3);
		button = (Button) v.findViewById(R.id.create_button);
		required_title = (TextView) v.findViewById(R.id.required_title);
		showUsername = (TextView) v.findViewById(R.id.create_show_username);
		showPassword = (TextView) v.findViewById(R.id.create_show_password);
	}
	
//	Switches the visibilty of the UI elements
	private void switchVisibility(){	
		if(firstName.getVisibility() == View.VISIBLE){
			firstName.setVisibility(View.INVISIBLE);
		}else{
			firstName.setVisibility(View.VISIBLE);
		}
		if(lastName.getVisibility() == View.VISIBLE){
			lastName.setVisibility(View.INVISIBLE);
		}else{
			lastName.setVisibility(View.VISIBLE);
		}
		if(password.getVisibility() == View.VISIBLE){
			password.setVisibility(View.INVISIBLE);
		}else{
			password.setVisibility(View.VISIBLE);
		}
		if(email.getVisibility() == View.VISIBLE){
			email.setVisibility(View.INVISIBLE);
		}else{
			email.setVisibility(View.VISIBLE);
		}
		if(subject1.getVisibility() == View.VISIBLE){
			subject1.setVisibility(View.INVISIBLE);
		}else{
			subject1.setVisibility(View.VISIBLE);
		}
		if(subject2.getVisibility() == View.VISIBLE){
			subject2.setVisibility(View.INVISIBLE);
		}else{
			subject2.setVisibility(View.VISIBLE);
		}
		if(subject3.getVisibility() == View.VISIBLE){
			subject3.setVisibility(View.INVISIBLE);
		}else{
			subject3.setVisibility(View.VISIBLE);
		}
		if(button.getVisibility() == View.VISIBLE){
			button.setVisibility(View.INVISIBLE);
		}else{
			button.setVisibility(View.VISIBLE);
		}if(required_title.getVisibility() == View.VISIBLE){
			required_title.setVisibility(View.INVISIBLE);
		}else{
			required_title.setVisibility(View.INVISIBLE);
		}
	}
	
//	Handles the button click
	private void registerListener() {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(firstName.getText().toString().isEmpty()
						|| lastName.getText().toString().isEmpty()
						|| password.getText().toString().isEmpty()
						|| email.getText().toString().isEmpty()
						|| subject1.getText().toString().isEmpty())
				{
					Toast.makeText(getActivity(),
							getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG)
							.show();
				}
				else{
					saveData();
					new RegistInDatabase().execute();
				}
				
			}

//			Saves the EditText values for later use
			private void saveData() {
				lastNameStr = lastName.getText().toString();
				firstNameStr = firstName.getText().toString();
				passwordStr = password.getText().toString();
				emailStr = email.getText().toString();
				subject1Str = subject1.getText().toString();
				if (subject2.getText().toString().isEmpty()== false)
				{
					subject2Str = subject2.getText().toString();
					Log.d(TAG, "subj2= " + subject2Str);
				}
				if (subject3.getText().toString().isEmpty()== false)
				{
					subject3Str = subject3.getText().toString();
					Log.d(TAG, "subj3= " +subject3Str);
				}
				
			}
		});
		
	}
//	External AsyncTask to save the Data in the Database
private class RegistInDatabase extends AsyncTask<Void, Void, Void>{
	
//	Shows the user a Progress Dialog while the Data is loading
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle(getResources().getText(R.string.app_name));
		mProgressDialog.setMessage(getResources().getText(R.string.loading));
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
	}

//	Saves the Data in the Database
	@Override
	protected Void doInBackground(Void... params) {
		ParseUser user = new ParseUser();
		r = new Random();
		userRandom = r.nextInt(1000);

		username = new String (
				firstNameStr.toLowerCase()
				+ userRandom
				+ lastNameStr.toLowerCase());
		user.setUsername(username);
		user.setEmail(emailStr);
		user.setPassword(passwordStr);
		user.put(Constants.FIRSTNAME, firstNameStr);
		user.put(Constants.LASTNAME, lastNameStr);
		if(subject1Str.isEmpty() == false){
			user.put(Constants.SUBJECT1, subject1Str);
		}
		if(subject2Str != null){
			user.put(Constants.SUBJECT2, subject2Str);
		}
		if(subject3Str != null){
			user.put(Constants.SUBJECT3, subject3Str);
		}
		user.put(Constants.ISTEACHER, true);
		try {
			user.signUp();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
/*	Updates the TextViews to show the Admin the username and password of the generated account
*	Cancels the Progress Dialog
*/
	protected void onPostExecute(Void result) {
		switchVisibility();
		showUsername.setText(getResources().getString(R.string.username)+"\n" +username);
		showPassword.setText(getResources().getString(R.string.password)+"\n" +passwordStr);
		Toast.makeText(getActivity(),
				"Erfolgreich erstellt",
				Toast.LENGTH_LONG).show();
		mProgressDialog.dismiss();
		

	}
	
}

}
