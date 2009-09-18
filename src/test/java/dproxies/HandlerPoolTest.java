package dproxies;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import dproxies.HandlerPool;
import dproxies.handler.Handler;

public class HandlerPoolTest {

    class TestHandler implements Handler<AtomicInteger> {

	public boolean handle(AtomicInteger t) throws Exception {
	    Thread.sleep(500);
	    t.incrementAndGet();
	    return true;
	}

    }

    @Test
    public void testOneThread() throws Exception {
	HandlerPool<AtomicInteger> threadPool = new HandlerPool<AtomicInteger>(
		1, new TestHandler());
	AtomicInteger atomicInteger = new AtomicInteger(0);
	assert threadPool.handle(atomicInteger);
	Thread.sleep(1000);
	assert atomicInteger.get() == 1;
    }

    @Test
    public void testOneThreadTwoTimes() throws Exception {
	HandlerPool<AtomicInteger> threadPool = new HandlerPool<AtomicInteger>(
		1, new TestHandler());
	AtomicInteger atomicInteger = new AtomicInteger(0);
	assert threadPool.handle(atomicInteger);
	Thread.sleep(1000);
	assert atomicInteger.get() == 1;
	assert threadPool.handle(atomicInteger);
	Thread.sleep(1000);
	assert atomicInteger.get() == 2;
    }

    @Test
    public void testAllThreadsBusy() throws Exception {
	HandlerPool<AtomicInteger> threadPool = new HandlerPool<AtomicInteger>(
		1, new TestHandler());
	AtomicInteger atomicInteger = new AtomicInteger(0);
	assert threadPool.handle(atomicInteger);
	assert !threadPool.handle(atomicInteger);
	Thread.sleep(1000);
	assert atomicInteger.get() == 1;
    }

    @Test
    public void testTwoThreads() throws Exception {
	HandlerPool<AtomicInteger> threadPool = new HandlerPool<AtomicInteger>(
		2, new TestHandler());
	AtomicInteger atomicInteger = new AtomicInteger(0);
	assert threadPool.handle(atomicInteger);
	assert threadPool.handle(atomicInteger);
	Thread.sleep(1000);
	assert atomicInteger.get() == 2;
    }

}
