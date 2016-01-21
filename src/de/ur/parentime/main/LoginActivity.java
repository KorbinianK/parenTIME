package de.ur.parentime.main;

/*
 * This class handles the Login of Users
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import de.ur.korbinian.kasberger.R;
import de.ur.parentime.admin.AdminMainActivity;
import de.ur.parentime.parent.ParentMainActivity;
import de.ur.parentime.teacher.TeacherMainActivity;

public class LoginActivity extends Activity {

	// Declare Variables
	private Button loginButton;
	private EditText password_field;
	private EditText username_field;
	private TextView signup_link;
	private TextView titleBig;
	private TextView titleSub;
	private String usernametxt;
	private String passwordtxt;

//	Creates the Activity, hides the Actionbar
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setupUI();
	}

//	
	@Override
	protected void onStart() {
		super.onStart();
		registerListener();
		checkForNewUser();
	}

//	Handles the Button Clicks
	private void registerListener() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.login:
					loginButton();
					break;
				case R.id.signup_link:
					signUpLink();
					break;
				}
			}
		};
		loginButton.setOnClickListener(listener);
		signup_link.setOnClickListener(listener);
	}

// 	If the user just registered this method will display a popup reminding the user to verify his E-Mail
	private void checkForNewUser() {
		Intent i = getIntent();
		Boolean newUser = i.getBooleanExtra("New User", false);
		if (newUser) {
			final Builder builder = new AlertDialog.Builder(this);          
	        TextView title = new TextView(this);
	        title.setText(getResources().getText(R.string.verify_title));
	        title.setPadding(10, 10, 10, 10);
	        title.setTextSize(23);
	        builder.setCustomTitle(title);
	        TextView msg = new TextView(this);
	        msg.setText(getResources().getText(R.string.verify));
	        msg.setPadding(10, 10, 10, 10);
	        msg.setGravity(Gravity.CENTER);
	        msg.setTextSize(18);
	        builder.setView(msg);
	        builder.setCancelable(true);
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            }
	        });
	        AlertDialog dialog = builder.create();
	        dialog.show();
		}
	}

//	Sets up the UI
	private void setupUI() {
		setContentView(R.layout.login);
		Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/SIGNIKA-BOLD.TTF");
		Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/SIGNIKA-REGULAR.TTF"); 
		username_field = (EditText) findViewById(R.id.username_signup_field);
		password_field = (EditText) findViewById(R.id.password_signup_field);
		titleBig = (TextView) findViewById(R.id.titleBig);
		titleSub = (TextView) findViewById(R.id.titleSub);
		signup_link = (TextView) findViewById(R.id.signup_link);
		loginButton = (Button) findViewById(R.id.login);
		titleBig.setTypeface(bold); 
		titleSub.setTypeface(bold);
		signup_link.setTypeface(regular);
		loginButton.setTypeface(regular);
	}

//	Starts the Signup Activity
	private void signUpLink() {
		Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

//	Handles the Login of the User, determines the Role
	private void loginButton() {
		loginButton.setClickable(false);
		usernametxt = username_field.getText().toString();
		passwordtxt = password_field.getText().toString();

		ParseUser.logInInBackground(usernametxt, passwordtxt,
				new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (e == null && user != null) {
							boolean verified = user.getBoolean("emailVerified");
							if (verified) {
								Intent intent;
								boolean isAdmin = user.getBoolean(Constants.ISADMIN);
								boolean isTeacher = user.getBoolean(Constants.ISTEACHER);
								if(isAdmin){
									Log.d("Debug", "admin logged in");
									intent = new Intent(LoginActivity.this, 
											AdminMainActivity.class);
									startActivity(intent);
								}else if(isTeacher){
									Log.d("Debug", "teacher logged in");
									intent = new Intent(LoginActivity.this, 
											TeacherMainActivity.class);
									startActivity(intent);
								}
								else{
									Log.d("Debug", "normal user logged in");
									intent = new Intent(LoginActivity.this, 
											ParentMainActivity.class);
									startActivity(intent);
								}
								Toast.makeText(getApplicationContext(),
										"Successfully Logged in",
										Toast.LENGTH_LONG).show();
								finish();
							} else 
							{
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(R.string.please_verify),
										Toast.LENGTH_LONG).show();
							}
						} else {
							Log.d("Debug", "error= " + e.getCode());
							if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(R.string.wrong_pw_us),
										Toast.LENGTH_LONG).show();
							}
						}
						loginButton.setClickable(true);
					}
			});
	}
}
