package dproxies.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.log.LogFactory;

public class Box<T> {

    private static final Logger LOG = LogFactory.getLogger(Box.class);

    protected Map<Serializable, BlockingQueue<T>> _queues = new HashMap<Serializable, BlockingQueue<T>>();

    public BlockingQueue<T> register(Serializable id)
	    throws AlreadyRegisteredException {
	if (!_queues.containsKey(id)) {
	    if (LOG.isLoggable(Level.FINE)) {
		LOG.fine("register new queue for id: " + id);
	    }
	    _queues.put(id, new ArrayBlockingQueue<T>(1));
	} else {
	    throw new AlreadyRegisteredException(
		    "a queue is already registered with id: " + id);
	}
	return _queues.get(id);
    }

    public void addToBox(Serializable id, T t) throws NotRegisteredException {
	if (!_queues.containsKey(id)) {
	    throw new NotRegisteredException("id is not regsitered: " + id);
	}
	BlockingQueue<T> queue = _queues.get(id);
	if (LOG.isLoggable(Level.FINE)) {
	    LOG.fine("add object [" + t.getClass().getName()
		    + "] to queue for id: " + id);
	}
	queue.add(t);
    }

    public BlockingQueue<T> deregister(Serializable id)
	    throws NotRegisteredException {
	if (!_queues.containsKey(id)) {
	    throw new NotRegisteredException("id is not regsitered: " + id);
	}
	return _queues.remove(id);
    }

    public boolean isRegistered(Serializable id) {
	return _queues.containsKey(id);
    }

    @Override
    public String toString() {
	return _queues.toString();
    }
}
