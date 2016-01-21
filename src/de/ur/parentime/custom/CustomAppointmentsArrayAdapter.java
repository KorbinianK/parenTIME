package de.ur.parentime.custom;

/*
 * This class handles the custom Appointment Objects and displays them in a ListView
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import de.ur.parentime.main.Constants;

public class CustomAppointmentsArrayAdapter extends ArrayAdapter<ParseUser> {

	private List<ParseUser> people;
	private List<ParseObject> time;
	private Context context;
	
//	Constructor
	public CustomAppointmentsArrayAdapter(Context context, List<ParseUser> people, List <ParseObject> time) {
		super(context, android.R.layout.simple_list_item_2, people);
		
		this.context = context;
		this.people = people;
		this.time = time;
	}
	
//	Creates the ListItem
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(android.R.layout.simple_list_item_2, null);
		}	
		
		ParseObject currentTime = time.get(position);
		ParseUser currentPerson = people.get(position);	
		if (currentTime != null) {
			TextView date = (TextView) v.findViewById(android.R.id.text1);
			TextView person = (TextView) v.findViewById(android.R.id.text2);
		
			date.setText(currentTime.getString(Constants.TIMELINE));
			person.setText(currentPerson.getString(Constants.LASTNAME) + " " + currentPerson.getString(Constants.FIRSTNAME));
		}
		return v;
	}


}
