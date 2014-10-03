package com.iheart.mduane.scrollfib;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FibAdapter extends BaseAdapter {

	private static final String TAG = "FibAdapter";
	private final Activity context;
	private static Map fibMap;

	// Use as a proxy for the fields in the list item view.  Improves overall feel of the scrolling.
	static class ViewHolder{
		public TextView fibNumber;
		public TextView fibValue;        
	}


	// Default constructor
	public FibAdapter(Activity context){
		this.context = context;
		this.fibMap = new HashMap<Integer, BigInteger>();
	}
	
	// Constructor with link to values
	public FibAdapter(Activity context, Map values){
		this.context = context;
		this.fibMap = values;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fibMap.size();
	}

	@Override
	public Object getItem(int position) {
		if(position >= 0)
			return fibMap.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// Reuse this view for efficiency.
		if(rowView == null){
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.fib_list_item, null);

			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.fibNumber= (TextView) rowView.findViewById(R.id.fib_number);
			viewHolder.fibValue = (TextView) rowView.findViewById(R.id.fib_value);            
			rowView.setTag(viewHolder);
		}

		// Fill in the data
		ViewHolder holder = (ViewHolder)rowView.getTag();

		try{
			holder.fibNumber.setText("["+Integer.toString(position) + "]");
			holder.fibValue.setText(fibMap.get(position).toString());
		}catch (Exception e){
			Log.e(TAG, "Error creating row in Adapter b/c: " + e.getMessage());			
			holder.fibValue.setText("Can't compute the next entry in the Fibonacci sequence");
		}

		return rowView;
	}
	
	// Add the new data to the map.  This will retain the old data so that we don't to reload the whole data set each time.
	public void addData(Map values){
		this.fibMap.putAll(values);
	}

}
