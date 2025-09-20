package com.bancolombia.integracion.test.cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.bancolombia.integracion.cache.ESQLCacheWrapper;

public class ESQLCacheWraperTest {
    String CACHE_NAME = "TEST";

    @Test
    public void TestPut() {
	System.out.println("----TestPut----");
	long maxElements = 100000;
	long currentTime = System.currentTimeMillis();

	System.out.println("Writing " + maxElements + " elements");
	for (long a = 0; a < maxElements; a++) {
	    ESQLCacheWrapper.put(CACHE_NAME, String.valueOf(a),
		    String.valueOf(System.currentTimeMillis()));
	}

	long endTime = System.currentTimeMillis();
	System.out.println("End Wiriting elements, elapsed millis "
		+ (endTime - currentTime));

	ESQLCacheWrapper.clear(CACHE_NAME);
    }

    @Test
    public void TestRemove() {
	System.out.println("----TestRemove----");
	long maxElements = 100000;
	long currentTime = System.currentTimeMillis();

	System.out.println("Writing and Removing" + maxElements + " elements");
	for (long a = 0; a < maxElements; a++) {
	    ESQLCacheWrapper.put(CACHE_NAME, String.valueOf(a),
		    String.valueOf(System.currentTimeMillis()));
	}
	for (long a = 0; a < maxElements; a++) {
	    ESQLCacheWrapper.remove(CACHE_NAME, String.valueOf(a));
	}

	long endTime = System.currentTimeMillis();
	System.out
		.println("End Wiriting and Removing elements, elapsed millis "
			+ (endTime - currentTime));

	ESQLCacheWrapper.clear(CACHE_NAME);
    }

    @Test
    public void TestConcurrentPut() throws InterruptedException {
	System.out.println("----TestConcurrentPut----");
	final long maxElements = 100000;
	int maxConcurrentThreads = 5;

	final CountDownLatch countDownLatch = new CountDownLatch(
		maxConcurrentThreads);
	Runnable put = new Runnable() {

	    @Override
	    public void run() {
		String id = String.valueOf(Thread.currentThread().getId());
		long currentTime = System.currentTimeMillis();
		System.out.println("Writing " + maxElements
			+ " elements from thread " + id);
		for (long a = 0; a < maxElements; a++) {
		    ESQLCacheWrapper.put(CACHE_NAME, String.valueOf(a),
			    String.valueOf(System.currentTimeMillis()));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("End Wiriting elements, elapsed millis "
			+ (endTime - currentTime) + " from thread " + id);
		countDownLatch.countDown();
	    }
	};
	System.out.println("Starting concurrent call");
	ExecutorService executorService = Executors.newCachedThreadPool();
	for (int i = 0; i < maxConcurrentThreads; i++) {
	    executorService.execute(put);
	}
	countDownLatch.await();
	System.out.println("End concurrent call");
	ESQLCacheWrapper.clear(CACHE_NAME);
    }

    @Test
    public void TestConcurrentPutNotbalanced() throws InterruptedException {
	System.out.println("----TestConcurrentPut----");
	final long maxElements = 100000;
	int maxConcurrentThreads = 30;

	final CountDownLatch countDownLatch = new CountDownLatch(
		maxConcurrentThreads);
	Runnable put = new Runnable() {

	    @Override
	    public void run() {
		String id = String.valueOf(Thread.currentThread().getId());
		long currentTime = System.currentTimeMillis();
		System.out.println("Writing " + maxElements
			+ " elements from thread " + id);
		for (long a = 0; a < maxElements; a++) {
		    ESQLCacheWrapper.put(CACHE_NAME, String.valueOf(a),
			    String.valueOf(System.currentTimeMillis()));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("End Wiriting elements, elapsed millis "
			+ (endTime - currentTime) + " from thread " + id);
		countDownLatch.countDown();
	    }
	};
	System.out.println("Starting concurrent call");
	ExecutorService executorService = Executors.newCachedThreadPool();
	for (int i = 0; i < maxConcurrentThreads; i++) {
	    executorService.execute(put);
	}
	countDownLatch.await();
	System.out.println("End concurrent call");
	ESQLCacheWrapper.clear(CACHE_NAME);
    }

    /*
     * @Test public void testRemove() { fail("Not yet implemented"); }
     * 
     * @Test public void testInitCache() { fail("Not yet implemented"); }
     * 
     * @Test public void testClear() { fail("Not yet implemented"); }
     */
}
