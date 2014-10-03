package com.iheart.mduane.scrollfib;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class FibListView extends ListView implements OnScrollListener {

	private static final String TAG = "FibListView"; 
	private View footer;
	private boolean isLoading;
	private FibListener fibListener;
	private FibAdapter fibAdapter;
	private LayoutInflater inflater = null;
	
	public FibListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);	
		this.setOnScrollListener(this);
	}

	public FibListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
	}
	
	public FibListView(Context context) {
		super(context);
		this.setOnScrollListener(this);
	}

	// Set the scroll listener for this view.
	public void setListener(FibListener listener){
		this.fibListener = listener;
	}
		

	/**
	 * Handle the current scrolling state for the view
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		// No adapter or it is empty, so nothing to scroll over.
		// In the event it is null, we shouldn't have an issue b/c it will break before getCount is called
		if(getAdapter() == null || getAdapter().getCount() == 0)
			return;
		
		// Get the viewable range
		int viewableCount = visibleItemCount + firstVisibleItem;
		
		Log.i(TAG, viewableCount + "");
		
		// If we are scrolling past the known data, load more values in the sequence.
		if(viewableCount >= totalItemCount && !isLoading){
			this.addFooterView(footer);
			isLoading = true;
			fibListener.loadFib();
		}
		
		return;
	}

	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {		
	}
	
	/**
	 * Set the loading view when adding data
	 * @param resourceId
	 */
	public void setLoadingView(int resourceId){
		// Get the inflater if you need it
		if(inflater == null){
			inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		footer = (View) inflater.inflate(resourceId, null);
		this.addFooterView(footer);
	}
	
	/**
	 * Set the adapter for this LsitView
	 * @param adapter to set for view
	 */
	public void setAdapter(FibAdapter adapter) {		
		super.setAdapter(adapter);
		this.fibAdapter = adapter;
		this.removeFooterView(footer);
	}
	
	/**
	 * Notify the adapter about the change of data
	 * @param values to add
	 */
	public void addFibSequence(Map values){
		this.removeFooterView(footer);
		fibAdapter.addData(values);
		fibAdapter.notifyDataSetChanged();
		isLoading = false;
	}
	
	/**
	 * Get the listener
	 * @return FibListener reference
	 */
	public FibListener getListener(){
		return this.fibListener;
	}
	
	// Interface for implementing the Fibonacci listener
	public static interface FibListener {
		public void loadFib();
	}
	
	

}
