package dproxies.handler.impl;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.testng.annotations.Test;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.handler.impl.InvocationMessageHandler;
import dproxies.handler.impl.InvokeHandler;
import dproxies.handler.impl.ResponseHandler;
import dproxies.tuple.TuplesWritable;
import dproxies.util.RegistrationBox;

public class InvocationMessageHandlerTest {

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
	RegistrationBox<Serializable> responseBox = new RegistrationBox<Serializable>();

	Foo foo = new Bar();
	Object[] objectsToCall = new Object[] { foo };

	Handler<TuplesWritable> handler = new InvokeHandler(objectsToCall);
	handler = new ResponseHandler(handler, responseBox);

	HandlerPool<TuplesWritable> pool = new HandlerPool<TuplesWritable>(10,
		handler);
	InvocationMessageHandler iHandler = new InvocationMessageHandler(
		responseBox, pool);
	Foo proxyInstance = (Foo) Proxy.newProxyInstance(Thread.currentThread()
		.getContextClassLoader(), new Class[] { Foo.class }, iHandler);
	String result = proxyInstance.foo();
	assert "foo bar".equals(result);
    }
}
