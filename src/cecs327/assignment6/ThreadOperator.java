package cecs327.assignment6;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class ThreadOperator extends Thread {

    private String[] pool, stringsArr;
    private Random rand = new Random();
    private final int NUM_OPERATIONS = 50;
    private double waitTime, totalSearchTime = 0, totalReplaceTime = 0;
    private int numSearches = 0, numReplaces = 0;
    private ReentrantLock lock;
    private ArrayList<Double> stdDevSearch = new ArrayList();
    private ArrayList<Double> stdDevReplace = new ArrayList();

    /**
     * default constructor for a ThreadOperator Thread
     *
     * @param pool - the pool of strings to choose from
     * @param stringsArr - the array to be manipulated
     * @param lock - a lock shared among each of the threads
     */
    public ThreadOperator(String[] pool, String[] stringsArr, ReentrantLock lock) {
	this.pool = pool;
	this.stringsArr = stringsArr;
	this.lock = lock;
    }

    /**
     * gets the average wait time for the SEARCH operation
     *
     * @return
     */
    public double getAvgSearchTime() {
	if (numSearches == 0) {
	    return 0;
	}
	else {
	    return (totalSearchTime / numSearches);
	}
    }

    /**
     * gets the average wait time for the REPLACE operation
     *
     * @return
     */
    public double getAvgReplaceTime() {
	if (numReplaces == 0) {
	    return 0;
	}
	else {
	    return (totalReplaceTime / numReplaces);
	}
    }
    
    /**
     * Calculate and return the standard deviation of the SEARCH Operation
     * @return the standard deviation
     */
    public double getStandardDevSearch() {
	double stdDev = 0;
	for (Double i : stdDevSearch) {
	    stdDev += Math.pow((i - this.getAvgSearchTime()), 2);
	}
	return Math.sqrt(stdDev / numSearches);
    }

    /**
     * Calculate and return the standard deviation of the REPLACE Operation
     * @return the standard deviation
     */
    public double getStandardDevReplace() {
	double stdDev = 0;
	for (Double i : stdDevReplace) {
	    stdDev += Math.pow((i - this.getAvgReplaceTime()), 2);
	}
	return Math.sqrt(stdDev / numSearches);
    }

    /**
     * Runs 50 operations on the shared array
     */
    public void run() {
	//Perform OPERATION 50 times
	for (int i = 0; i < NUM_OPERATIONS; i++) {
	    int operation = rand.nextInt(1101);
	    String poolString = pool[rand.nextInt(pool.length)];


	    /*
	    Find the last occurrence of a random string picked from the pool
	     */
	    if (operation <= 999) {

		int lastOccurrence = -1;

		//time how long it takes to acquire the lock
		waitTime = System.nanoTime();
		lock.lock();
		waitTime = System.nanoTime() - waitTime;
		totalSearchTime += waitTime;
		stdDevSearch.add(totalSearchTime);
		//searches for the given string
		try {
		    for (int j = 0; j < stringsArr.length; j++) {
			if (stringsArr[j].equals(poolString)) {
			    lastOccurrence = j;
			}
		    }
		}
		finally {
		    lock.unlock();
		}
		//if last occurrence is -1 then the string wasn't found
		//otherwise, print the last occurrence
		if (lastOccurrence != -1) {
		    System.out.println("The last occurrence of "
				       + poolString + " is index " + lastOccurrence + ".");
		}
		else {
		    System.out.println("Did not find " + poolString);
		}
		numSearches++;
	    }

	    /*
	    Replace a string at a random location in stringsArr with another
	    string from the pool.
	     */
	    else if (operation >= 1000) {
		int toReplace = rand.nextInt(stringsArr.length);
		String replaced = stringsArr[toReplace];

		//time how long it takes to acquire the lock
		waitTime = System.nanoTime();
		lock.lock();
		waitTime = System.nanoTime() - waitTime;
		totalReplaceTime += waitTime;
		stdDevReplace.add(totalReplaceTime);

		try {
		    stringsArr[toReplace] = poolString;
		}
		finally {
		    lock.unlock();
		}

		System.out.println(replaced + " replaced by " + poolString
				   + " at index " + toReplace + ".");
		numReplaces++;
	    }
	}
    }
}