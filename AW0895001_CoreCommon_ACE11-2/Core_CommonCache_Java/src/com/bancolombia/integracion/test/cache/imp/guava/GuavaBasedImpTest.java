package com.bancolombia.integracion.test.cache.imp.guava;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bancolombia.integracion.cache.ICache;
import com.bancolombia.integracion.cache.imp.guava.GuavaBasedCacheProperties;
import com.bancolombia.integracion.cache.imp.guava.GuavaBasedImp;

public class GuavaBasedImpTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void TestDefaulConstructor() {
	System.out.println("----TestDefaulConstructor----");
	long maxElements = 100000;
	int maxConcurrentThreads = 5;
	long expiryTime = 60;
	long expiryAccess = 120;
	ICache<String, String> guavaBasedImp = GuavaBasedImp.getInstance(
		maxElements, maxConcurrentThreads, expiryTime, expiryAccess);

	System.out.println("Writing " + maxElements + " elements");

	long currentTime = System.currentTimeMillis();
	for (long a = 0; a < maxElements; a++) {
	    guavaBasedImp.put(String.valueOf(a),
		    String.valueOf(System.currentTimeMillis()));
	}
	long endTime = System.currentTimeMillis();

	System.out.println("End Wiriting elements, elapsed millis "
		+ (endTime - currentTime));

	guavaBasedImp.clear();
    }

    @Test
    public void TestPropertiesConstructor() {
	System.out.println("----TestPropertiesConstructor----");
	long maxElements = 100000;
	int maxConcurrentThreads = 5;
	long expiryTime = 60;
	long expiryAccess = 120;

	Properties properties = new Properties();
	properties.put(GuavaBasedCacheProperties.MAX_ELEMNTS, maxElements);
	properties.put(GuavaBasedCacheProperties.MAX_CONCURRENT_THREADS,
		maxConcurrentThreads);
	properties.put(GuavaBasedCacheProperties.EXPIRY_TIME, expiryTime);
	properties.put(GuavaBasedCacheProperties.EXPIRY_ACCESS, expiryAccess);

	ICache<String, String> guavaBasedImp = GuavaBasedImp.getInstance(properties);

	System.out.println("Writing " + maxElements + " elements");
	Assert.assertNotNull(guavaBasedImp);

	long currentTime = System.currentTimeMillis();
	for (long a = 0; a < maxElements; a++) {
	    guavaBasedImp.put(String.valueOf(a),
		    String.valueOf(System.currentTimeMillis()));
	}
	long endTime = System.currentTimeMillis();

	System.out.println("End Wiriting elements, elapsed millis "
		+ (endTime - currentTime));

	guavaBasedImp.clear();
    }

    @Test
    public void TestConcurrentPut() throws InterruptedException {
	System.out.println("----TestConcurrentPut----");
	final long maxElements = 100000;
	int maxConcurrentThreads = 5;
	long expiryTime = 60;
	long expiryAccess = 120;

	final ICache<String, String> guavaBasedImp = GuavaBasedImp.getInstance(
		maxElements, maxConcurrentThreads, expiryTime, expiryAccess);
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
		    guavaBasedImp.put(String.valueOf(a),
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
	guavaBasedImp.clear();
    }

    @Test
    public void TestConcurrentPutNotBalaced() throws InterruptedException {
	System.out.println("----TestConcurrentPutNotBalaced----");
	final long maxElements = 100000;
	int maxConcurrentThreads = 30;
	long expiryTime = 60;
	long expiryAccess = 120;
	int maxConcurrency = 3;
	
	final ICache<String, String> guavaBasedImp = GuavaBasedImp.getInstance(
		maxElements, maxConcurrency, expiryTime, expiryAccess);
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
		    guavaBasedImp.put(String.valueOf(a),
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
	guavaBasedImp.clear();
    }

    @Test
    public void TestConcurrentPutAndGet() throws InterruptedException {
	System.out.println("----TestConcurrentPutAndGet----");
	final long maxElements = 100000;
	int maxConcurrentThreads = 5;
	long expiryTime = 60;
	long expiryAccess = 120;
	
	final ICache<String, String> guavaBasedImp = GuavaBasedImp.getInstance(
		maxElements, maxConcurrentThreads, expiryTime, expiryAccess);
	final CountDownLatch countDownLatch = new CountDownLatch(
		maxConcurrentThreads * 2);
	Runnable put = new Runnable() {

	    @Override
	    public void run() {
		String id = String.valueOf(Thread.currentThread().getId());
		long currentTime = System.currentTimeMillis();
		System.out.println("Writing " + maxElements
			+ " elements from thread " + id);
		for (long a = 0; a < maxElements; a++) {
		    guavaBasedImp.put(String.valueOf(a),
			    String.valueOf(System.currentTimeMillis()));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("End Wiriting elements, elapsed millis "
			+ (endTime - currentTime) + " from thread " + id);
		countDownLatch.countDown();
	    }
	};

	Runnable get = new Runnable() {

	    @Override
	    public void run() {
		String id = String.valueOf(Thread.currentThread().getId());
		long currentTime = System.currentTimeMillis();
		System.out.println("Reading " + maxElements
			+ " elements from thread " + id);
		String data = null;
		long datacount = 0;
		for (long a = 0; a < maxElements; a++) {
		    data = guavaBasedImp.get(String.valueOf(a));
		    if (data != null) {
			datacount++;
			data = null;
		    }
		}
		long endTime = System.currentTimeMillis();
		System.out.println("End Reading elements total of " + datacount
			+ ", elapsed millis " + (endTime - currentTime)
			+ " from thread " + id);
		countDownLatch.countDown();
	    }
	};

	System.out.println("Starting concurrent call");
	ExecutorService executorService = Executors.newCachedThreadPool();
	for (int i = 0; i < maxConcurrentThreads; i++) {
	    executorService.execute(put);
	    executorService.execute(get);
	}
	countDownLatch.await();
	System.out.println("End concurrent call");
	guavaBasedImp.clear();
    }

    @Test
    public void TestConcurrentPutAndGetNotBalanced()
	    throws InterruptedException {
	System.out.println("----TestConcurrentPutAndGetNotBalanced----");
	final long maxElements = 100000;
	int maxConcurrentThreads = 30;
	long expiryTime = 60;
	long expiryAccess = 120;
	int maxConcurrency = 3;
	
	final ICache<String, String> guavaBasedImp = GuavaBasedImp.getInstance(
		maxElements, maxConcurrency, expiryTime, expiryAccess);
	final CountDownLatch countDownLatch = new CountDownLatch(
		maxConcurrentThreads * 2);
	Runnable put = new Runnable() {

	    @Override
	    public void run() {
		String id = String.valueOf(Thread.currentThread().getId());
		long currentTime = System.currentTimeMillis();
		System.out.println("Writing " + maxElements
			+ " elements from thread " + id);
		for (long a = 0; a < maxElements; a++) {
		    guavaBasedImp.put(String.valueOf(a),
			    String.valueOf(System.currentTimeMillis()));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("End Wiriting elements, elapsed millis "
			+ (endTime - currentTime) + " from thread " + id);
		countDownLatch.countDown();
	    }
	};

	Runnable get = new Runnable() {

	    @Override
	    public void run() {
		String id = String.valueOf(Thread.currentThread().getId());
		long currentTime = System.currentTimeMillis();
		System.out.println("Reading " + maxElements
			+ " elements from thread " + id);
		String data = null;
		long datacount = 0;
		for (long a = 0; a < maxElements; a++) {
		    data = guavaBasedImp.get(String.valueOf(a));
		    if (data != null) {
			datacount++;
			data = null;
		    }
		}
		long endTime = System.currentTimeMillis();
		System.out.println("End Reading elements total of " + datacount
			+ ", elapsed millis " + (endTime - currentTime)
			+ " from thread " + id);
		countDownLatch.countDown();
	    }
	};

	System.out.println("Starting concurrent call");
	ExecutorService executorService = Executors.newCachedThreadPool();
	for (int i = 0; i < maxConcurrentThreads; i++) {
	    executorService.execute(put);
	    executorService.execute(get);
	}
	countDownLatch.await();
	System.out.println("End concurrent call");
	guavaBasedImp.clear();
    }
}
