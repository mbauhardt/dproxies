package dproxies.handler.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.logging.Logger;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.handler.InfiniteReader;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ProxyBox;
import dproxies.util.ProxyMethodCallResult;

public class DynamicProxyHandler<T> extends SocketCloseHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(DynamicProxyHandler.class);
    private final Class<T> _interfaceClass;

    public DynamicProxyHandler(Class<T> interfaceClass) {
	_interfaceClass = interfaceClass;
    }

    public DynamicProxyHandler(Handler<Tuples> prev, Class<T> interfaceClass) {
	super(prev);
	_interfaceClass = interfaceClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean handlePreviousSuccess(Tuples tuples) throws Exception {

	Tuple<Object> tuple = tuples.getTuple("proxyBox");
	ProxyBox<T> proxies = (ProxyBox<T>) tuple.getTupleValue();

	Socket socket = (Socket) tuples.getTuple("socket").getTupleValue();
	DataInput in = (DataInput) tuples.getTuple("in").getTupleValue();
	DataOutput out = (DataOutput) tuples.getTuple("out").getTupleValue();

	ProxyMethodCallResult box = new ProxyMethodCallResult();

	// create request writer
	LOG.info("create request handler");
	Handler<TuplesWritable> handler = new RequestWriter(out);
	HandlerPool<TuplesWritable> pool = new HandlerPool<TuplesWritable>(10,
		handler);

	// create proxy
	LOG.info("create a java dynamic proxy to send/receive messages: "
		+ _interfaceClass.getName());
	InvocationMessageProducer iHandler = new InvocationMessageProducer(box,
		pool);
	T proxy = (T) Proxy.newProxyInstance(Thread.currentThread()
		.getContextClassLoader(), new Class[] { _interfaceClass },
		iHandler);
	proxies.register(socket.toString());
	proxies.addToBox(socket.toString(), proxy);
	// queue.add(proxy);

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
