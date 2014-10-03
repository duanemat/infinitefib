package com.iheart.mduane.scrollfib;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MainFib extends Activity implements FibListView.FibListener{

	private static final String TAG = "MainFib";
	
	// Two notes about the data structure 
	// (1) I chose Integer as the key even though it does have an upper limit (2^31-1), but at that point the system would also likely fail.  Could change to Long or larger type as necessary 
	// (2) I recognize this could be a SparseArray, but I'm more comfortable with Maps
	private static HashMap<Integer, BigInteger> fibMap; 
	private final static int NUMBER_PER_REQUEST = 35; // Found that 35 streams nicely
	private final static int NUMBER_INITIAL_VALUES = 75;
	FibListView lv;	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_fib);

		// Get the custom ListView
		lv = (FibListView) findViewById(R.id.fib_list_view);

		// Create the Fibonacci list object locally.  We'll use this for computing the data, but we'll only need to keep the last data point
		// Because that will contain all of the previous computations		
		initializeFibMap();

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

	/**
	 * 
	 * Internal ASyncTask for computing the next x number of values in the sequence.
	 *
	 */
	private class FibTask extends AsyncTask<Void, Void, Map<Integer, BigInteger>>{

		@Override
		protected Map<Integer, BigInteger> doInBackground(Void... params) {

			Integer nextRange = fibMap.size() + NUMBER_PER_REQUEST;			

			if(isCancelled())
				return null;

			// Retrieve the nextRange, as this will iteratively acquire them.
			try{
				if(fibMap == null || fibMap.size() == 0){
					initializeFibMap();
					getFibIter(NUMBER_INITIAL_VALUES);
				}
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
		
		// If map doesn't exist or doesn't have the two base cases
		if(fibMap == null || fibMap.size() < 2)
			initializeFibMap();
		
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
		
		// Base cases in the map
		fibMap.put(0, BigInteger.ZERO);
		fibMap.put(1, BigInteger.ONE);
		
		BigInteger result = BigInteger.ZERO;
		
		// Now save a bit of time by using the map if we already have some of the work completed.
		BigInteger[] startingPoints = getStartingPoints();		
				
		
		int fibIndex = startingPoints[2].intValue();				
		BigInteger prev = startingPoints[1];
		BigInteger prevPrev = startingPoints[0];
		
		// For debugging
		//Log.i(TAG, fibIndex + " " + prev + " "+ prevPrev);
		
		for(int i=fibIndex; i<position; i++){
			result = prev.add(prevPrev);
			fibMap.put(i, result);
			prevPrev = prev;
			prev = result;
		}
				
		return result;
		
	}
	
	/**
	 * Initializer for the Fibonacci Map
	 */
	private static void initializeFibMap(){
		fibMap = new HashMap<Integer, BigInteger>(); 
		fibMap.put(0, BigInteger.ZERO);
		fibMap.put(1, BigInteger.ONE);
	}
	
	/**
	 * Little function to improve efficiency by using the Mapped values.  
	 * @return Array of initial index plus prev and prevPrev starting points
	 */
	private static BigInteger[] getStartingPoints(){
		BigInteger[] initialPoints = new BigInteger[3];
		
		if (fibMap == null){
			initializeFibMap();
		}
		
		// Order the keys and get the two highest.  These are our prev and prevPrev
		Set<Integer> keys = fibMap.keySet();
		ArrayList<Integer> keyList = new ArrayList<Integer>(keys);
		Collections.sort(keyList);
		
		// Get the last key and make sure we also have n-1 as well.  If not, start from the bottom
		int lastKey = keyList.get(keyList.size()-1);
		if(keyList.get(keyList.size()-2) == lastKey-1){
			initialPoints[2] = BigInteger.valueOf(lastKey).add(BigInteger.ONE); // Initial index
			initialPoints[1] = fibMap.get(lastKey); // relates to prev
			initialPoints[0] = fibMap.get(lastKey-1); // relates to prevPrev
		}else{
			initialPoints[2] = BigInteger.valueOf(2); // Initial value
			initialPoints[1] = BigInteger.ONE;
			initialPoints[0] = BigInteger.ZERO;
		}
		
		return initialPoints;
	}
	
	/**
	 * Recursive fib seqeunce
	 * @param position being sought
	 * @return computed value in the sequence
	 * @throws Exception - throws the exception (usually out-of-memory)
	 */
	private static BigInteger getFib(Integer position) throws Exception{


		// Handle the possibility of the list being null.  If so, create it
		if(fibMap == null){
			initializeFibMap();
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


