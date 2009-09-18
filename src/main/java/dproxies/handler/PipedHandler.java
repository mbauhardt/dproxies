package dproxies.handler;

public abstract class PipedHandler<T> implements Handler<T> {

    private final Handler<T> _prev;

    public PipedHandler(Handler<T> prev) {
	_prev = prev;
    }

    public boolean handle(T t) throws Exception {
	boolean handle = _prev != null ? _prev.handle(t) : true;
	return handle ? doHandle(t) : false;
    }

    protected abstract boolean doHandle(T t) throws Exception;

}
