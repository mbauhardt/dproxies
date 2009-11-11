package dproxies.client;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.handler.InfiniteReader;
import dproxies.handler.impl.InvocationMessageConsumer;
import dproxies.handler.impl.ResponseWriter;
import dproxies.handler.impl.SocketCloseHandler;
import dproxies.tuple.TuplesWritable;

public class ObjectCallHandler<T> extends SocketCloseHandler {

    private final T _objectToCall;

    public ObjectCallHandler(T objectToCall) {
	_objectToCall = objectToCall;
    }

    @Override
    protected boolean handlePreviousSuccess(Socket socket) throws Exception {

	// create in
	InputStream inputStream = socket.getInputStream();
	ObjectInput in = new ObjectInputStream(inputStream);

	// create out
	OutputStream outputStream = socket.getOutputStream();
	ObjectOutput out = new ObjectOutputStream(outputStream);

	Handler<TuplesWritable> handler = new InvocationMessageConsumer(
		new Object[] { _objectToCall });
	handler = new ResponseWriter(handler, out);
	HandlerPool<TuplesWritable> requestPool = new HandlerPool<TuplesWritable>(
		10, handler);
	HandlerPool<TuplesWritable> responsePool = null;
	InfiniteReader infiniteReader = new InfiniteReader(requestPool,
		responsePool, in);
	return infiniteReader.handle(new TuplesWritable());
    }

}
