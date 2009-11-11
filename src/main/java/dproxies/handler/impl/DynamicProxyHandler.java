package dproxies.handler.impl;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.handler.InfiniteReader;
import dproxies.log.LogFactory;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ProxyBox;
import dproxies.util.ProxyMethodCallResult;

public class DynamicProxyHandler<T> extends SocketCloseHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(DynamicProxyHandler.class);
    private final Class<T> _clazz;
    private final ProxyBox<T> _proxies;

    public DynamicProxyHandler(Class<T> t, ProxyBox<T> proxies) {
	_clazz = t;
	_proxies = proxies;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean handlePreviousSuccess(Socket socket) throws Exception {

	// create in
	InputStream inputStream = socket.getInputStream();
	ObjectInput in = new ObjectInputStream(inputStream);

	// create out
	OutputStream outputStream = socket.getOutputStream();
	ObjectOutput out = new ObjectOutputStream(outputStream);

	ProxyMethodCallResult box = new ProxyMethodCallResult();

	// create request writer
	LOG.info("create request handler");
	Handler<TuplesWritable> handler = new RequestWriter(out);
	HandlerPool<TuplesWritable> pool = new HandlerPool<TuplesWritable>(10,
		handler);

	// create proxy
	LOG.info("create a java dynamic proxy to send/receive messages: "
		+ _clazz.getName());
	InvocationMessageProducer iHandler = new InvocationMessageProducer(box,
		pool);
	T proxy = (T) Proxy.newProxyInstance(Thread.currentThread()
		.getContextClassLoader(), new Class[] { _clazz }, iHandler);
	BlockingQueue<T> queue = _proxies.register(socket.toString());
	queue.add(proxy);

	// create response reader to read the proxy method return value
	LOG.info("create respone handler");
	// currently we support only response's and no requests from the client
	HandlerPool<TuplesWritable> requestPool = null;
	Handler<TuplesWritable> responseHandler = new ResponseHandler(box);
	HandlerPool<TuplesWritable> responsePool = new HandlerPool<TuplesWritable>(
		10, responseHandler);
	Handler<TuplesWritable> infiniteReader = new InfiniteReader(
		requestPool, responsePool, in);
	return infiniteReader.handle(new TuplesWritable());
    }

}
