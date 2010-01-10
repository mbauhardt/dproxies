package dproxies.handler.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import dproxies.handler.Handler;
import dproxies.handler.PipedHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public class IOHandler extends PipedHandler<Tuples> {

    public IOHandler() {
	super(null);
    }

    public IOHandler(Handler<Tuples> prev) {
	super(prev);
    }

    @Override
    protected boolean handlePreviousSuccess(Tuples t) throws Exception {
	Socket socket = (Socket) t.getTuple("socket").getTupleValue();
	t.addTuple(new Tuple<Object>("in", new DataInputStream(socket
		.getInputStream())));
	t.addTuple(new Tuple<Object>("out", new DataOutputStream(socket
		.getOutputStream())));
	return true;
    }

    @Override
    protected void handleFailure(Tuples t) throws IOException {
	Socket socket = (Socket) t.getTuple("socket").getTupleValue();
	socket.close();
    }

}
