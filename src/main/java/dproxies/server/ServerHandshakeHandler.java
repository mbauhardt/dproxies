package dproxies.server;

import java.io.IOException;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractHandshakeHandler;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public class ServerHandshakeHandler extends AbstractHandshakeHandler {

    public ServerHandshakeHandler(Generator generator) {
	super(generator);
    }

    public ServerHandshakeHandler(Handler<Tuples> prev, Generator generator) {
	super(prev, generator);
    }

    @Override
    protected boolean doHandshake() throws IOException {
	return handleHandshake() ? startHandshake() : false;
    }

}
