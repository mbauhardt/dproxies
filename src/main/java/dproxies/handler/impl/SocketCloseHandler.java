package dproxies.handler.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.PipedHandler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public abstract class SocketCloseHandler extends PipedHandler<Tuples> {

    public SocketCloseHandler() {
	super(null);
    }

    public SocketCloseHandler(Handler<Tuples> prev) {
	super(prev);
    }

    private static final Logger LOG = LogFactory
	    .getLogger(SocketCloseHandler.class);

    @Override
    protected void handleFailure(Tuples tuples) throws IOException {
	Tuple<Object> tuple = tuples.getTuple("socket");
	Socket socket = (Socket) tuple.getTupleValue();
	LOG.warning("error detect, close socket");
	socket.close();
    }

}
