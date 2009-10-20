package dproxies.handler;

import java.io.IOException;

public abstract class PipedHandler<T> extends AbstractHandler<T> {

    private final Handler<T> _prev;

    public PipedHandler(Handler<T> prev) {
	_prev = prev;
    }

    @Override
    protected final boolean doHandle(T t) throws Exception {
	boolean handle = _prev != null ? _prev.handle(t) : true;
	return handle ? handleSuccess(t) : fail(t);
    }

    private boolean fail(T t) throws IOException {
	handleFailure(t);
	return false;
    }

    protected abstract boolean handleSuccess(T t) throws Exception;

}
