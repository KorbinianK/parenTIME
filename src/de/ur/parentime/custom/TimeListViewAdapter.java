package de.ur.parentime.custom;

/*
 * Custom Adapter to handle Time Objects
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import de.ur.korbinian.kasberger.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
 
public class TimeListViewAdapter extends BaseAdapter {
 
//  Declare Variables
	private Context mContext;
	private LayoutInflater inflater;
    private List<Time> timesList;
    private ArrayList<Time> arraylist;
    
//  Constructor
    public TimeListViewAdapter(Context context,
            List<Time> timesList)  {
        mContext = context;
        this.timesList = timesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Time>();
        this.arraylist.addAll(timesList);
    }
 
//  Holds the Views
    public class ViewHolder {
        TextView time;
        TextView parent;
    }
 
//  Returns the size of the List
    @Override
    public int getCount() {
        return timesList.size();
    }
 
//  Returns a Time object depending on the position
    @Override
    public Time getItem(int position) {
        return timesList.get(position);
    }
 
//  Gets the position
    @Override
    public long getItemId(int position) {
        return position;
    }
 
//  Creates the Listitem
    public View getView(final int position, View view, ViewGroup parent)  {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.appointment_list_item, parent,false);
            holder.time = (TextView) view.findViewById(R.id.app_time);    
            holder.parent = (TextView) view.findViewById(R.id.app_user); 
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.time.setText(timesList.get(position).getTime());
        if(parent != null){
        	 holder.parent.setText(timesList.get(position).getParent());
        }
        return view;
    }
}
