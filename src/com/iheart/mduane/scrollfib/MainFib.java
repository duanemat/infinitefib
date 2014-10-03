package com.iheart.mduane.scrollfib;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MainFib extends Activity implements FibListView.FibListener{

	private static final String TAG = "MainFib";
	private static HashMap<Integer, BigInteger> fibMap;
	private final static int NUMBER_PER_REQUEST = 25;
	private final static int NUMBER_INITIAL_VALUES = 50;
	FibListView lv;	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_fib);

		// Get the custom ListView
		lv = (FibListView) findViewById(R.id.fib_list_view);

		// Create the Fibonacci list object locally.  We'll use this for computing the data, but we'll only need to keep the last data point
		// Because that will contain all of the previous computations		
		fibMap = new HashMap<Integer, BigInteger>();

		// Create the custom adapter and set it to the ListView, along with the loading screen
		FibAdapter fibAdapter = new FibAdapter(this);
		lv.setLoadingView(R.layout.fib_list_loading);
		lv.setAdapter(fibAdapter);

		// Set the listener to this activity, which will call the AsyncTask
		lv.setListener(this);

		loadFib();
	}

	// Loads new data as needed
	@Override
	public void loadFib() {
		FibTask ft = new FibTask();
		ft.execute();
	}

	private class FibTask extends AsyncTask<Void, Void, Map<Integer, BigInteger>>{

		@Override
		protected Map<Integer, BigInteger> doInBackground(Void... params) {

			Integer nextRange = fibMap.size() + NUMBER_PER_REQUEST;			

			if(isCancelled())
				return null;

			// Retrieve the nextRange, as this will recursively go back and acquire them.
			// If we have an empty list, then get the first 50.  Else get the next expected range
			try{
				if(fibMap.size() == 0)
					getFibIter(NUMBER_INITIAL_VALUES);
				else
					getFibIter(nextRange);
			}catch (Exception e){
				e.printStackTrace();
				Log.e(TAG, "We had an error b/c " + e.getMessage());
				Toast.makeText(getApplicationContext(), "Looks like we've hit the upper limit of this little device's computing power.  Isn't this Fibonacci long enough for you?", Toast.LENGTH_SHORT).show();
			}

			return fibMap;
		}

		// Update the view of the change
		@Override
		protected void onPostExecute(Map<Integer, BigInteger> results) {							
			super.onPostExecute(results);
			lv.addFibSequence(results);

			// Now get rid of the old fibMap and just add the last value from results, since we don't need the other values 
			// (they are stored in the adapter)

			Log.i(TAG, "FibMap Size = " + fibMap.size());
			// Efficiency improvement. 
			/*ArrayList<Integer> keys = new ArrayList<Integer>(results.keySet());
			Collections.sort(keys);
			Integer lastKey = keys.get(keys.size()-1);
			Integer lastKey2 = keys.get(keys.size()-2);
			BigInteger lastValue = fibMap.get(lastKey);
			BigInteger lastValue2 = fibMap.get(lastKey2);
			//fibMap.clear();
			//fibMap.put(lastKey, lastValue);
			//fibMap.put(lastKey2, lastValue2);			 
			 */


		}


	}

	/**
	 * Iterative call for Fibonacci.  This seems to be more efficient because we are not creating bigger stacks on each recursive call.
	 * @param position - value to computer
	 * @return Fibonacci value
	 */
	private static BigInteger getFibIter(Integer position){
		
		// If less than 0 for some reason we are returning 0;
		if(position < 0)
			return BigInteger.ZERO;
		
		// If map doesn't exist
		if(fibMap == null)
			fibMap = new HashMap<Integer, BigInteger>();
		
		// Base cases plus some history
		if(position == 0) {			
			return BigInteger.ZERO;
		}
		if(position == 1){			
			return BigInteger.ONE;
		}
		
		// If we've already computed it, return that value
		if(fibMap.containsKey(position))
			return fibMap.get(position);
		
		BigInteger prevPrev = BigInteger.ZERO;
		BigInteger prev = BigInteger.ONE;
		
		// Base cases in the map
		fibMap.put(0, BigInteger.ZERO);
		fibMap.put(1, BigInteger.ONE);
		
		BigInteger result = BigInteger.ZERO;
		
		for(int i=2; i<position; i++){
			result = prev.add(prevPrev);
			fibMap.put(i, result);
			prevPrev = prev;
			prev = result;
		}
				
		return result;
		
	}
	
	private static BigInteger getFib(Integer position) throws Exception{


		// Handle the possibility of the list being null.  If so, create it
		if(fibMap == null){
			fibMap = new HashMap<Integer, BigInteger>();
		}

		// Handle less than 0.  If so, just return 0.  We could also throw an error, but since this is a controlled class I'll be able to protect against this.
		if(position < 0){
			return BigInteger.ZERO;
		}

		// If we already computed it, just return that value.		
		if(fibMap.containsKey(position))
			return (BigInteger) fibMap.get(position);

		// Handle the two base cases
		if(position == 0){
			fibMap.put(position, BigInteger.ZERO);
			return BigInteger.ZERO;
		}		
		else if(position == 1){
			fibMap.put(position, BigInteger.ONE);
			return BigInteger.ONE;
		}
		else{
			// Recursive call			
			fibMap.put(position, getFib(position-2).add(getFib(position-1)));
			return (BigInteger) fibMap.get(position);			
		}
	}
}


