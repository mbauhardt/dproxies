package dproxies.handler.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.PipedHandler;
import dproxies.log.LogFactory;

public abstract class SocketCloseHandler extends PipedHandler<Socket> {

    public SocketCloseHandler() {
	super(null);
    }

    public SocketCloseHandler(Handler<Socket> prev) {
	super(prev);
    }

    private static final Logger LOG = LogFactory
	    .getLogger(SocketCloseHandler.class);

    @Override
    protected void handleFailure(Socket socket) throws IOException {
	LOG.warning("error detect, close socket");
	socket.close();
    }

}
