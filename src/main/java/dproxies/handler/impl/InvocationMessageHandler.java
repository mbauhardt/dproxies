package dproxies.handler.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import dproxies.HandlerPool;
import dproxies.handler.impl.InvokeHandler.InvocationMessage;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.RegistrationBox;

public class InvocationMessageHandler implements InvocationHandler {

    private Random _random;
    private final HandlerPool<TuplesWritable> _threadPool;
    private final RegistrationBox _responseBox;

    public InvocationMessageHandler(RegistrationBox responseBox,
	    HandlerPool<TuplesWritable> threadPool) {
	_responseBox = responseBox;
	_threadPool = threadPool;
	_random = new Random(System.currentTimeMillis());
    }

    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	InvocationMessage invocationMessage = new InvokeHandler.InvocationMessage(
		proxy.getClass(), method, args);
	int id = _random.nextInt();
	BlockingQueue<Object> queue = _responseBox.register(id);

	TuplesWritable tuplesWritable = new TuplesWritable();
	tuplesWritable.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	tuplesWritable.addTuple(new Tuple<Serializable>("id", id));
	_threadPool.handle(tuplesWritable);

	Object take = queue.poll(10, TimeUnit.SECONDS);
	_responseBox.deregister(id);
	return take;
    }

}
