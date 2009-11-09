package dproxies.handler.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import dproxies.HandlerPool;
import dproxies.handler.impl.InvocationMessageConsumer.InvocationMessage;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ProxyMethodCallResult;

public class InvocationMessageProducer implements InvocationHandler {

    private Random _random;
    private final HandlerPool<TuplesWritable> _threadPool;
    private final ProxyMethodCallResult _box;

    public InvocationMessageProducer(ProxyMethodCallResult box,
	    HandlerPool<TuplesWritable> threadPool) {
	_box = box;
	_threadPool = threadPool;
	_random = new Random(System.currentTimeMillis());
    }

    public Object invoke(Object proxy, Method method, Object[] args)
	    throws Throwable {
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		proxy.getClass(), method, args);
	int id = _random.nextInt();
	BlockingQueue<Serializable> queue = _box.register(id);

	TuplesWritable tuplesWritable = new TuplesWritable();
	tuplesWritable.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	tuplesWritable.addTuple(new Tuple<Serializable>("id", id));
	_threadPool.handle(tuplesWritable);

	Object take = queue.poll(10, TimeUnit.SECONDS);
	_box.deregister(id);
	return take;
    }

}
