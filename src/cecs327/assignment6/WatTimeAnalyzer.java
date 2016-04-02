package cecs327.assignment6;

import java.util.Random;
import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

public class WatTimeAnalyzer {

    //global variables that will be shared across several threads
    static String[] stringsArr = new String[100];
    static String[] pool = new String[110];
    static ReentrantLock lock = new ReentrantLock();

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {

	//initial variable declarations
	Random rand = new Random();
	DecimalFormat df = new DecimalFormat("#.##");
	final int STRING_MAX = 20, STRING_MIN = 5, NUM_THREADS = 20;
	ThreadOperator[] threadList = new ThreadOperator[NUM_THREADS];
	double avgSearchTime = 0, avgReplaceTime = 0;

	//Generate a random string of uppercase characters for each index
	for (int i = 0; i < pool.length; i++) {
	    pool[i] = "";   //initialize each string w/ empty string

	    //for loop generates a random string, 1 to 20 characters long
	    for (int j = 0; j < (rand.nextInt(STRING_MAX - STRING_MIN) + STRING_MIN); j++) {
		pool[i] += (char) (rand.nextInt(26) + 'A'); //randomly generate a random uppercase char
	    }
	}

	//randomly assigns strings from pool to the array
	System.out.println("Now randomly assigning strings from the pool to our array");
	for (int i = 0; i < stringsArr.length; i++) {
	    stringsArr[i] = pool[rand.nextInt(pool.length)];
	    System.out.println(i + "|  " + stringsArr[i]);
	}

	//Create and run the list of threads
	for (int i = 0; i < threadList.length; i++) {
	    threadList[i] = new ThreadOperator(pool, stringsArr, lock);
	    threadList[i].start();
	}

	for (ThreadOperator i : threadList) {
	    try {
		i.join();//causes main to wait until all threads finish
	    }
	    catch (InterruptedException e) {
		System.out.println(e.toString());
	    }
	}

	//print out the final string to check the results.
	for (int i = 0; i < stringsArr.length; i++) {
	    System.out.println(i + "|  " + stringsArr[i]);
	}

	//get average SEARCH and REPLACE wait times for each individual thread
	System.out.println("--------------------------------------------------------------");
	System.out.format("%7s%12s%15s%15s%17s", "Thread#", "Search(ns)", "Std Dev Search", "Replace(ns)", "Std Dev Replace");
	System.out.println("\n----------------------------------------------------------------");
	for (int i = 0; i < threadList.length; i++) {
	    avgSearchTime += threadList[i].getAvgSearchTime();
	    avgReplaceTime += threadList[i].getAvgReplaceTime();
	    //output data retrieved for each thread & format into neat columns
	    System.out.format("%7s%12s%15s%15s%17s", i,
		    df.format(threadList[i].getAvgSearchTime()),
		    df.format(threadList[i].getStandardDevSearch()),
		    df.format(threadList[i].getAvgReplaceTime()),
		    df.format(threadList[i].getStandardDevReplace()));
	    System.out.println();
	}

	//calcluate average time to wait for the lock for the Search and the Replace operations
	avgSearchTime = avgSearchTime / threadList.length;
	avgReplaceTime = avgReplaceTime / threadList.length;

	//formats output of average wait times
	System.out.println("\n");
	System.out.format("%15s%25s", "AVG Search(ns)", "AVG Replace(ns)");
	System.out.println("\n---------------------------------------");
	System.out.format("%15s%25s", df.format(avgSearchTime), df.format(avgReplaceTime));
	System.out.println();
    }
}