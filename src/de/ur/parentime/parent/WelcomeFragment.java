package de.ur.parentime.parent;

/*
 * This class shows the user a welcome screen with some information
 */

import com.parse.ParseUser;

import de.ur.korbinian.kasberger.R;
import de.ur.parentime.main.Constants;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WelcomeFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome, container, false);
        setupUI(rootView);
        return rootView;
    }

	private void setupUI(View v) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        TextView txtuser = (TextView) v.findViewById(R.id.txtuser);
        txtuser.setText(
        		currentUser.getString(Constants.FIRSTNAME)
        		+ Constants.SPACE
        		+ currentUser.getString(Constants.LASTNAME));
 
      
		
	}

}
