package dproxies.handler.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.TuplesWritableHandler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class InvocationMessageConsumer extends TuplesWritableHandler {

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
	    _arguments = arguments != null ? arguments : new Object[] {};
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

    private static final Logger LOG = LogFactory
	    .getLogger(InvocationMessageConsumer.class);

    private final Object[] _delegates;

    public InvocationMessageConsumer(Object[] objectsToCall) {
	this(null, objectsToCall);
    }

    public InvocationMessageConsumer(Handler<TuplesWritable> prev,
	    Object[] objectsToCall) {
	super(prev);
	_delegates = objectsToCall;
    }

    @Override
    protected boolean handlePreviousSuccess(TuplesWritable t) throws Exception {
	Tuple<Serializable> invocationTuple = t.getTuple("invocationMessage");
	InvocationMessage invocationMessage = (InvocationMessage) invocationTuple
		.getTupleValue();

	Method method = invocationMessage.getMethod();
	Class<?> declaringClass = method.getDeclaringClass();
	Class<?>[] declaringInterfaces = declaringClass.getInterfaces();
	Class<?>[] declaringClasses = new Class<?>[declaringInterfaces.length + 1];
	System.arraycopy(declaringInterfaces, 0, declaringClasses, 1,
		declaringInterfaces.length);
	declaringClasses[0] = declaringClass;

	Object[] arguments = invocationMessage.getArguments();

	Object delegate = null;
	for (int i = 0; i < declaringClasses.length; i++) {
	    for (int j = 0; j < _delegates.length; j++) {
		if (declaringClasses[i].isAssignableFrom(_delegates[j]
			.getClass())) {
		    delegate = _delegates[j];
		    break;
		}
	    }
	}

	boolean bit = false;
	if (delegate != null) {
	    try {
		if (LOG.isLoggable(Level.FINE)) {
		    LOG.fine("call method [" + method.getName()
			    + "] on object [" + delegate.getClass().getName()
			    + "]");
		}
		// reload method
		Method[] methods = delegate.getClass().getMethods();
		for (Method method2 : methods) {
		    if (method2.getName().equals(method.getName())) {
			method = method2;
			break;
		    }
		}
		Object result = method.invoke(delegate, arguments);
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
		LOG.log(Level.WARNING, "method call [" + method.getName()
			+ "] on object [" + delegate.getClass().getName()
			+ "] throws exception [" + e.getCause() + "].", e);
		t.addTuple(new Tuple<Serializable>("exception", e.getCause()));
	    }
	} else {
	    LOG.severe("dont call method [" + method.getName()
		    + "] because object to call ["
		    + method.getDeclaringClass().getName()
		    + "] is not configured.");
	}
	return bit;
    }
}
