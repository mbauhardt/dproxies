package dproxies.handler.impl;

import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.handler.impl.InvocationMessageConsumer;
import dproxies.handler.impl.InvocationMessageConsumer.InvocationMessage;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class InvocationMessageConsumerTest {

    static class ResultNotSerializable implements Serializable {
	private static final long serialVersionUID = 1L;

	class NotSerializable {

	}

	public NotSerializable foo() {
	    return new NotSerializable();
	}
    }

    static class ThrowException implements Serializable {
	private static final long serialVersionUID = 1L;

	public void foo() {
	    throw new RuntimeException("foo bar");
	}
    }

    static class VoidClass implements Serializable {
	private static final long serialVersionUID = 1L;

	public void foo() {
	    // do nothing
	}
    }

    @Test
    public void testNoObject() throws Exception {
	Object[] objectsToCall = new Object[] {};
	Handler<TuplesWritable> handler = new InvocationMessageConsumer(objectsToCall);
	TuplesWritable t = new TuplesWritable();
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		String.class, String.class.getMethod("concat", String.class),
		new Object[] { " world" });
	t.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	handler.handle(t);
	assert t.getTuple("result") == null;
    }

    @Test
    public void testObject() throws Exception {
	Object[] objectsToCall = new Object[] { "hello" };
	Handler<TuplesWritable> handler = new InvocationMessageConsumer(objectsToCall);
	TuplesWritable t = new TuplesWritable();
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		String.class, String.class.getMethod("concat", String.class),
		new Object[] { " world" });
	t.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	handler.handle(t);
	assert t.getTuple("result") != null;
	Tuple<Serializable> result = t.getTuple("result");
	assert "hello world".equals(result.getTupleValue());
    }

    @Test
    public void testResultNotSerializable() throws Exception {
	Object[] objectsToCall = new Object[] { new ResultNotSerializable() };
	Handler<TuplesWritable> handler = new InvocationMessageConsumer(objectsToCall);
	TuplesWritable t = new TuplesWritable();
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		ResultNotSerializable.class, ResultNotSerializable.class
			.getMethod("foo"), new Object[] {});
	t.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	handler.handle(t);
	assert t.getTuple("result") == null;
    }

    @Test
    public void testException() throws Exception {
	Object[] objectsToCall = new Object[] { new ThrowException() };
	Handler<TuplesWritable> handler = new InvocationMessageConsumer(objectsToCall);
	TuplesWritable t = new TuplesWritable();
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		ThrowException.class, ThrowException.class.getMethod("foo"),
		new Object[] {});
	t.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	handler.handle(t);
	assert t.getTuple("result") == null;
	Tuple<Serializable> tuple = t.getTuple("exception");
	Serializable tupleValue = tuple.getTupleValue();
	assert tupleValue instanceof RuntimeException;
	RuntimeException exception = (RuntimeException) tupleValue;
	assert exception.getMessage().equals("foo bar");
    }

    @Test
    public void testVoid() throws Exception {
	Object[] objectsToCall = new Object[] { new VoidClass() };
	Handler<TuplesWritable> handler = new InvocationMessageConsumer(objectsToCall);
	TuplesWritable t = new TuplesWritable();
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		VoidClass.class, VoidClass.class.getMethod("foo"),
		new Object[] {});
	t.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	handler.handle(t);
	assert t.getTuple("result") == null;

    }

}
