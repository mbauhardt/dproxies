package dproxies.handler;

import java.io.IOException;

public abstract class AbstractHandler<T> implements Handler<T> {

    public final boolean handle(T t) throws Exception {
	return doHandle(t) ? true : fail(t);
    }

    private boolean fail(T t) throws IOException {
	handleFailure(t);
	return false;
    }

    protected abstract void handleFailure(T t) throws IOException;

    protected abstract boolean doHandle(T t) throws Exception;
}
