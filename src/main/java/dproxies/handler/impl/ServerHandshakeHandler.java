package dproxies.handler.impl;

import java.io.IOException;

import dproxies.util.Generator;

public class ServerHandshakeHandler extends AbstractHandshakeHandler {

    public ServerHandshakeHandler(Generator generator) {
	super(generator);
    }

    @Override
    protected boolean doHandshake() throws IOException {
	return handleHandshake() ? startHandshake() : false;
    }

}
