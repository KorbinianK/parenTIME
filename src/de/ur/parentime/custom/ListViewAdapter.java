package de.ur.parentime.custom;

/*
 * Custom Adapter to handle the Appointment Items
 */

import java.util.ArrayList;

import de.ur.korbinian.kasberger.R;
import de.ur.parentime.main.Constants;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ListViewAdapter extends BaseAdapter {
 
    // Declare Variables
	private Context mContext;
	private LayoutInflater inflater;
    private ArrayList<Appointment> appointmentsList = null;
	private int type;
	
//	Constructor
    public ListViewAdapter(Context context,ArrayList<Appointment> appointmentsList, int type) {
        mContext = context;
        this.appointmentsList = appointmentsList;
        inflater = LayoutInflater.from(mContext);
        this.type = type;
    }
 
// Holds the individual Views
    public class ViewHolder  {
        TextView time;
        TextView teacher;
        TextView parent;
        TextView swipe;
        ImageView back;
        ImageView forw;
    }
    
//   Gets the size of the List
    @Override
    public int getCount() {
        return appointmentsList.size();
    }
 
//  Gets the item at the called position
    @Override
    public Appointment getItem(int position)  {
        return appointmentsList.get(position);
    }
 
//   Gets the item ID at the called position
    @Override
    public long getItemId(int position) {
        return position;
    }
    
//  Removes the item
    public void remove(Appointment deletedItem){
    	ArrayList<Appointment> update = appointmentsList;
    	update.remove(deletedItem);
    	updateResults(update);
    }
    
//  Updates the List
    public void updateResults(ArrayList<Appointment> update) {
    	appointmentsList = update;
        notifyDataSetChanged();
    }
 
//  Setup for the List items, depending on the Type
//  Type 0 = help swipe item, Type 1 = default appointment item, Type 3 = teacher appointment
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        View row = view;

        if (row == null)   {
        	if(type == 0 && position == 0){
        	 type = 1;
        	 Log.d("Debug", "custom" + position + type);
        	 holder = new ViewHolder();
    		 view = inflater.inflate(R.layout.appointment_list_example_item, parent,false);
             holder.swipe = (TextView) view.findViewById(R.id.swipetxt);
             holder.back = (ImageView) view.findViewById(R.id.swipeback);
             holder.forw = (ImageView) view.findViewById(R.id.swipeforward);
        	}
        	else if(type == 0 && position != 0) {
        		 Log.d("Debug", "custom" + position + type);
        		 holder = new ViewHolder();
        		 view = inflater.inflate(R.layout.appointment_list_item, parent,false);
                 holder.time = (TextView) view.findViewById(R.id.app_time);
                 holder.teacher = (TextView) view.findViewById(R.id.app_user);
                 holder.time.setText(appointmentsList.get(position).getTime());
          		 holder.teacher.setText(
          				 appointmentsList.get(position).getTeacherLastName()
          				 +Constants.SPACE
          				 +appointmentsList.get(position).getTeacherFirstName()
          				 );
        	}
        	else if(type == 3){
        		Log.d("Debug", "custom" + position + type);
        		holder = new ViewHolder();
        		view = inflater.inflate(R.layout.appointment_list_item, parent,false);
                holder.time = (TextView) view.findViewById(R.id.app_time);
                holder.parent = (TextView) view.findViewById(R.id.app_user);
                holder.time.setText(appointmentsList.get(position).getTime());
                holder.parent.setText(
         				 appointmentsList.get(position).getParentLastName()
         				 +Constants.SPACE
         				 +appointmentsList.get(position).getParentFirstName()
         				 );
                Log.d("Debug",appointmentsList.get(position).getParentFirstName()
         				 +Constants.SPACE
         				 +appointmentsList.get(position).getParentLastName());
        	} else {
        		Log.d("Debug", "custom" + position + type);
        		holder = new ViewHolder();
        		view = inflater.inflate(R.layout.appointment_list_item, parent,false);
                holder.time = (TextView) view.findViewById(R.id.app_time);
                holder.teacher = (TextView) view.findViewById(R.id.app_user);
                holder.time.setText(appointmentsList.get(position).getTime());
                holder.teacher.setText(
         				 appointmentsList.get(position).getTeacherLastName()
         				 +Constants.SPACE
         				 +appointmentsList.get(position).getTeacherFirstName()
         				 );
        	}
            view.setTag(holder);
        } else {
        	Log.d("Debug", "pos= "+ position + appointmentsList.get(position).getTime());
        	Log.d("Debug", "custom" + position + type);
    		holder = new ViewHolder();
    		view = inflater.inflate(R.layout.appointment_list_item, parent,false);
            holder.time = (TextView) view.findViewById(R.id.app_time);
            holder.teacher = (TextView) view.findViewById(R.id.app_user);
            holder.time.setText(appointmentsList.get(position).getTime());
            holder.teacher.setText(
     				 appointmentsList.get(position).getTeacherLastName()
     				 +Constants.SPACE
     				 +appointmentsList.get(position).getTeacherFirstName()
     				 );
      		view.getTag();
        }
        	
        return view;
    }
}
