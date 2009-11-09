package dproxies.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

public class ProxyBox<T> extends RegistrationBox<T> {

    public Object[] getAllProxies() {
	Collection<BlockingQueue<T>> values = _queues.values();
	Object[] proxies = new Object[values.size()];
	Iterator<BlockingQueue<T>> iterator = values.iterator();
	int c = 0;
	while (iterator.hasNext()) {
	    BlockingQueue<T> blockingQueue = (BlockingQueue<T>) iterator.next();
	    T element = blockingQueue.element();
	    proxies[c++] = element;
	}
	return proxies;
    }
}
