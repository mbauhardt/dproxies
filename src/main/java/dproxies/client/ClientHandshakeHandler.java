package dproxies.client;

import java.io.IOException;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractHandshakeHandler;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public class ClientHandshakeHandler extends AbstractHandshakeHandler {

    public ClientHandshakeHandler(Generator generator) {
	super(generator);
    }

    public ClientHandshakeHandler(Handler<Tuples> prev, Generator generator) {
	super(prev, generator);
    }

    @Override
    protected boolean doHandshake() throws IOException {
	return startHandshake() ? handleHandshake() : false;
    }

}
