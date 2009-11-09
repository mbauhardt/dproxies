package dproxies.handler.impl;

import java.lang.reflect.Proxy;

import org.testng.annotations.Test;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ProxyMethodCallResult;

public class InvocationMessageProducerTest {

    public static interface Foo {
	String foo();
    }

    public static class Bar implements Foo {
	public String foo() {
	    return "foo bar";
	}
    }

    @Test
    public void testInvocation() throws Exception {
	ProxyMethodCallResult box = new ProxyMethodCallResult();

	Foo foo = new Bar();
	Object[] objectsToCall = new Object[] { foo };

	Handler<TuplesWritable> handler = new InvocationMessageConsumer(
		objectsToCall);
	handler = new ResponseHandler(handler, box);

	HandlerPool<TuplesWritable> pool = new HandlerPool<TuplesWritable>(10,
		handler);
	InvocationMessageProducer iHandler = new InvocationMessageProducer(box,
		pool);
	Foo proxyInstance = (Foo) Proxy.newProxyInstance(Thread.currentThread()
		.getContextClassLoader(), new Class[] { Foo.class }, iHandler);
	String result = proxyInstance.foo();
	assert "foo bar".equals(result);
    }
}
