package de.ur.parentime.custom;

/*
 * This class handles the Display of the Teacher List
 */

import com.parse.ParseUser;

import de.ur.parentime.main.Constants;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class TeacherListArrayAdapter extends ArrayAdapter<ParseUser> {

	private List<ParseUser> teacherList;
	private ParseUser teacher;
	private Context context;
	
//	Constructor
	public TeacherListArrayAdapter(Context context, List<ParseUser> teacher) {
		super(context, android.R.layout.simple_list_item_2, teacher);
		
		this.context = context;
		this.teacherList = teacher;
	}
	
//	Returns the List Size
	public int getCount() {
        return teacherList.size();
    }
	
//	Sets the teacher
	public void setTeacher(ParseUser teacher){
		this.teacher = teacher;
	}
	
//	returns the Teacher
	public ParseUser getTeacher(){
		return teacher;
	}

//	Creates the List Item
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(android.R.layout.simple_list_item_2, null);
		}
			
		ParseUser user = teacherList.get(position);
		teacher = user;
		
		if (user != null) {
			TextView teacherSurname = (TextView) v.findViewById(android.R.id.text1);
			TextView subject = (TextView) v.findViewById(android.R.id.text2);
			
			String subject1 = user.getString(Constants.SUBJECT1);
			String subject2 = user.getString(Constants.SUBJECT2);
			String subject3 = user.getString(Constants.SUBJECT3);
			if(subject1 != null && subject2 != null && subject3 == null){
				subject.setText(
						subject1
						+Constants.SEPARATOR
						+subject2);
			}else if(subject2 != null && subject3 != null){
				subject.setText(
						subject1
						+Constants.SEPARATOR
						+subject2+Constants.SEPARATOR
						+subject3);
			}else {
				subject.setText(subject1);
			}
			teacherSurname.setText(user.getString(Constants.LASTNAME) + " " + user.getString(Constants.FIRSTNAME));
		}

		return v;
	}

}