package de.ur.parentime.main;

/*
 * This class handles the Account Creation for normal Users
 */

import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import de.ur.korbinian.kasberger.R;

public class SignupActivity extends Activity {

	private Button register;
	private TextView desc;
	private EditText inputField1;
	private EditText inputField2;
	private TextView usernameLabel;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String password;
	private String EMPTY = "";
	private Random r;
	private Locale locale;
	private int signupState = 0;
	private int userRandom;
	private TextView passwordLabel;

	private static final String TAG = "Debug";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 if(getResources().getBoolean(R.bool.portrait_only)){
		        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		    }
		getActionBar().hide();
		signupState = 0;
		setContentView();
		setupContent();
		buttonListener();
	}

//	Handles the Button Clicks
	private void buttonListener() {
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (signupState) {
				case 0:
					getName();
					break;
				case 1:
					getEmailAndPassword();
					break;
				case 2:
					displayUsernamePassword();
					break;
				}
			}
		});
	}
	
//	Creates the Account in the Databse
	private void registInDatabase() {
		ParseUser user = new ParseUser();
		r = new Random();
		userRandom = r.nextInt(1000);
		username = new String (
				firstName.toLowerCase(locale)
				+ userRandom
				+ lastName.toLowerCase(locale));
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.put(Constants.FIRSTNAME, firstName);
		user.put(Constants.LASTNAME, lastName);
		user.put(Constants.ISTEACHER, false);
		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					signupState = 2;
					setupContent();
					clearFields();
				} else {
					if(e.getCode() == ParseException.EMAIL_TAKEN)
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.email_taken),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

//	Set up for the UI, depending on the Signup Step
	private void setupContent() {
		switch (signupState) {
		case 0:
			inputField1.setHint(R.string.forename);
			inputField2.setHint(R.string.surname);
			desc.setText(R.string.name_desc);
			inputField1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
			inputField2.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
			break;
		case 1:
			desc.setText(R.string.email_password_desc);
			register.setText(R.string.register);
			inputField2.setVisibility(View.VISIBLE);
			inputField1.setHint(R.string.email);
			inputField2.setHint(R.string.password);
			inputField1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			inputField2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			inputField2.setTransformationMethod(PasswordTransformationMethod.getInstance());
			break;
		case 2:
			desc.setText(R.string.username_desc);
			usernameLabel.setText(
					getResources().getString(R.string.username) 
					+ "\n" 
					+ username);
			passwordLabel.setText(
					getResources().getString(R.string.password) 
					+ "\n" 
					+ password);
			passwordLabel.setVisibility(View.VISIBLE);
			usernameLabel.setVisibility(View.VISIBLE);
			inputField1.setVisibility(View.INVISIBLE);
			inputField2.setVisibility(View.INVISIBLE);
			break;
		}
	}

//	Gets the E-mail and Password from the EditTexts
	private void getEmailAndPassword() {
		if (inputField1.getText().toString().isEmpty()
			|| inputField2.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG)
					.show();
		} else {
			saveInput();
			registInDatabase();
		}
	}

//	Starts Asynctask to display the Users password and Username
	private void displayUsernamePassword() {
		new RemoteDataTask().execute();
	}

//	Gets the full Name of the User
	private void getName() {
		if (inputField1.getText().toString().isEmpty()
				|| inputField2.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.fill_all_fields), Toast.LENGTH_LONG)
					.show();
		} else {
			saveInput();
			signupState = 1;
			setupContent();
			clearFields();
		}
	}

//	Saves the Strings for later use
	private void saveInput() {
		switch (signupState) {
		case 0:
			firstName = inputField1.getText().toString();
			lastName = inputField2.getText().toString();
			Log.v(TAG, "saved " + firstName + " " + lastName);
			break;
		case 1:
			email = inputField1.getText().toString();
			password = inputField2.getText().toString();
			Log.v(TAG, "saved " + email + " " + password);
			break;
		}
	}

//	clears the EditTexts
	private void clearFields() {
		inputField1.setText(EMPTY);
		inputField2.setText(EMPTY);
	}

//	Sets up the UI
	private void setContentView() {
		setContentView(R.layout.signup);
		locale = new Locale("de_DE");
		Locale.setDefault(locale);
		desc = (TextView) findViewById(R.id.signupDesc);
		inputField1 = (EditText) findViewById(R.id.create_firstname);
		inputField1.requestFocus();
		inputField2 = (EditText) findViewById(R.id.create_lastname);
		usernameLabel = (TextView) findViewById(R.id.usernameLabel);
		passwordLabel = (TextView) findViewById(R.id.passwordLabel);
		register = (Button) findViewById(R.id.create_button);
	}
	
	
	private class RemoteDataTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPostExecute(Void result) {
			
			setupContent();
			signupState = 3;
			Toast.makeText(getApplicationContext(), "signup success",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(SignupActivity.this,
					LoginActivity.class);
			intent.putExtra("New User", true);
			startActivity(intent);
			finish();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}