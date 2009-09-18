package dproxies.handler.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import dproxies.handler.TuplesWritableHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class InvokeHandler extends TuplesWritableHandler {

    public static class InvocationMessage implements Externalizable {

	private Method _method;
	private Object[] _arguments = new Object[] {};
	private Class<?> _clazz;

	public InvocationMessage() {
	}

	public InvocationMessage(Class<?> clazz, Method method,
		Object[] arguments) {
	    _clazz = clazz;
	    _method = method;
	    _arguments = arguments;
	}

	public void readExternal(ObjectInput input) throws IOException {
	    try {
		String className = input.readUTF();
		String methodName = input.readUTF();
		int length = input.readInt();
		Class<?>[] classArguments = new Class[length];
		_arguments = new Object[length];
		for (int i = 0; i < length; i++) {
		    _arguments[i] = input.readObject();
		    classArguments[i] = _arguments[i].getClass();
		}
		_clazz = Class.forName(className);
		_method = _clazz.getMethod(methodName, classArguments);
	    } catch (Throwable e) {
		e.printStackTrace();
		throw new IOException(e.getMessage());
	    }
	}

	public void writeExternal(ObjectOutput output) throws IOException {
	    output.writeUTF(_clazz.getName());
	    output.writeUTF(_method.getName());

	    output.writeInt(_arguments.length);
	    for (Object object : _arguments) {
		output.writeObject(object);
	    }
	}

	public Method getMethod() {
	    return _method;
	}

	public Object[] getArguments() {
	    return _arguments;
	}

	public Class<?> getClazz() {
	    return _clazz;
	}

    }

    private static final Logger LOG = Logger.getLogger(InvokeHandler.class
	    .getName());

    private Map<String, Object> _objectsToCall = new HashMap<String, Object>();

    public InvokeHandler(Object[] objectsToCall) {
	for (Object object : objectsToCall) {
	    String name = object.getClass().getInterfaces()[0].getName();
	    LOG.info("register object: " + object.getClass().getName());
	    _objectsToCall.put(name, object);
	}
    }

    @Override
    protected boolean doHandle(TuplesWritable t) throws Exception {
	Tuple<Serializable> invocationTuple = t.getTuple("invocationMessage");
	InvocationMessage invocationMessage = (InvocationMessage) invocationTuple
		.getTupleValue();

	Class<?> clazz = invocationMessage.getClazz();
	Class<?> interfaceClass = clazz.getInterfaces()[0];
	Method method = invocationMessage.getMethod();
	Object[] arguments = invocationMessage.getArguments();
	Object object = _objectsToCall.get(interfaceClass.getName());
	boolean bit = false;

	if (object != null) {
	    try {
		Object result = method.invoke(object, arguments);
		if (result != null) {
		    if (result instanceof Serializable) {
			t.addTuple(new Tuple<Serializable>("result",
				(Serializable) result));
			bit = true;
		    } else {
			LOG.severe("dont push result ["
				+ result.getClass().getName()
				+ "] because it is not serializable");
		    }
		}
	    } catch (Throwable e) {
		LOG.info("method call [" + method.getName() + "] on object ["
			+ clazz.getName() + "] throws exception ["
			+ e.getCause() + "].");
		t.addTuple(new Tuple<Serializable>("exception", e.getCause()));
	    }
	} else {
	    LOG.severe("dont call method [" + method.getName()
		    + "] because object to call [" + clazz.getName()
		    + "] is not configured.");
	}
	return bit;
    }

}
