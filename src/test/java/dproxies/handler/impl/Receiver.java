package dproxies.handler.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.handler.PipedHandler;
import dproxies.handler.TuplesReader;
import dproxies.tuple.TuplesWritable;

public class Receiver {

    private HandlerPool<TuplesWritable> _pool;

    public Receiver(int threadCount, Socket socket, Object[] objectsToCall)
	    throws IOException {

	// instantiate handler
	Handler<TuplesWritable> handler = new TuplesReader(
		new ObjectInputStream(socket.getInputStream()));
	handler = new InvokeHandler(handler, objectsToCall);
	handler = new ResponseWriter(handler, new ObjectOutputStream(socket
		.getOutputStream()));
	
	// create thread pool
	_pool = new HandlerPool<TuplesWritable>(1, handler);

    }
    
    public static void main(String[] args) {
	Handler<String> handler = new Handler<String>() {

	    public boolean handle(String t) throws Exception {
		System.out.println("start");
		return true;
	    }
	};
	handler = new PipedHandler<String>(handler) {
	    @Override
	    protected boolean doHandle(String t) throws Exception {
		System.out.println("pipe");
		return true;
	    }
	};
	handler = new PipedHandler<String>(handler) {

	    @Override
	    protected boolean doHandle(String t) throws Exception {
		System.out.println("nothing");
		return false;
	    }
	};
	
    }

}
