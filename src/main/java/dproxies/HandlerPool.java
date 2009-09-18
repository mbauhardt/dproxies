package dproxies;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import dproxies.handler.Handler;

public class HandlerPool<T> {

    class PooledThread extends Thread {

	private BlockingQueue<T> _queue = new LinkedBlockingQueue<T>(1);

	@Override
	public void run() {
	    while (!isInterrupted()) {
		try {
		    T t = _queue.take();
		    _handler.handle(t);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    _threadQueue.add(this);
		}
	    }
	}

	public boolean addElement(T t) {
	    return _queue.add(t);
	}
    }

    private BlockingQueue<PooledThread> _threadQueue = new LinkedBlockingQueue<PooledThread>();
    private final Handler<T> _handler;
    private static final Logger LOG = Logger.getLogger(HandlerPool.class
	    .getName());

    public HandlerPool(int threadCount, Handler<T> handler) {
	_handler = handler;
	for (int i = 0; i < threadCount; i++) {
	    PooledThread pooledThread = new PooledThread();
	    pooledThread.start();
	    _threadQueue.add(pooledThread);
	}
    }

    public boolean handle(T t) {
	PooledThread thread = _threadQueue.poll();
	boolean ret = false;
	if (thread != null) {
	    ret = thread.addElement(t);
	} else {
	    LOG.warning("all threads are busy!");
	}
	return ret;
    }
}
