package dproxies.client;

import java.io.DataInput;
import java.io.DataOutput;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.handler.InfiniteReader;
import dproxies.handler.impl.InvocationMessageConsumer;
import dproxies.handler.impl.ResponseWriter;
import dproxies.handler.impl.SocketCloseHandler;
import dproxies.tuple.Tuples;
import dproxies.tuple.TuplesWritable;

public class ObjectCallHandler<T> extends SocketCloseHandler {

    private final T _delegate;

    public ObjectCallHandler(T objectToCall) {
	_delegate = objectToCall;
    }

    public ObjectCallHandler(Handler<Tuples> prev, T objectToCall) {
	super(prev);
	_delegate = objectToCall;
    }

    @Override
    protected boolean handlePreviousSuccess(Tuples tuples) throws Exception {
	DataInput in = (DataInput) tuples.getTuple("in").getTupleValue();
	DataOutput out = (DataOutput) tuples.getTuple("out").getTupleValue();

	Handler<TuplesWritable> handler = new InvocationMessageConsumer(
		new Object[] { _delegate });
	handler = new ResponseWriter(handler, out);
	HandlerPool<TuplesWritable> requestPool = new HandlerPool<TuplesWritable>(
		10, handler);
	HandlerPool<TuplesWritable> responsePool = null;
	InfiniteReader infiniteReader = new InfiniteReader(requestPool,
		responsePool, in);
	return infiniteReader.handle(new TuplesWritable());
    }

}
