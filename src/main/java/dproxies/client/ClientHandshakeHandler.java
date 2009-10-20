package dproxies.client;

import java.io.IOException;

import dproxies.handler.impl.AbstractHandshakeHandler;
import dproxies.util.Generator;

public class ClientHandshakeHandler extends AbstractHandshakeHandler {

    public ClientHandshakeHandler(Generator generator) {
	super(generator);
    }

    @Override
    protected boolean doHandshake() throws IOException {
	return startHandshake() ? handleHandshake() : false;
    }

}
