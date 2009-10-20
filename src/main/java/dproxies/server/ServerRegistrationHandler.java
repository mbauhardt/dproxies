package dproxies.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.util.AlreadyRegisteredException;
import dproxies.util.ClientRegistration;

public class ServerRegistrationHandler extends AbstractRegistrationHandler {

    private static final Logger LOG = Logger
	    .getLogger(ServerRegistrationHandler.class.getName());

    private final ClientRegistration _clientRegistration;

    private final Object _mutex = new Object();

    public ServerRegistrationHandler(Handler<Socket> prev,
	    ClientRegistration clientRegistration) {
	super(prev);
	_clientRegistration = clientRegistration;
    }

    public ServerRegistrationHandler(ClientRegistration clientRegistration) {
	super(null);
	_clientRegistration = clientRegistration;
    }

    @Override
    protected boolean doRegistration() throws IOException,
	    ClassNotFoundException, AlreadyRegisteredException {
	String id = readClientName();
	boolean bit = false;
	synchronized (_mutex) {

	    LOG.info("try to register client: " + id);
	    if (!_clientRegistration.isRegistered(id)) {
		BlockingQueue<Socket> queue = _clientRegistration.register(id);
		queue.add(_socket);
		bit = true;
		ObjectOutput out = new ObjectOutputStream(_socket
			.getOutputStream());
		RegistrationResponse response = new RegistrationResponse(id,
			"registration successfully. welcome '" + id + "'.",
			true);
		response.writeExternal(out);
		LOG.info("registration success for id: " + id);
	    } else {
		ObjectOutput out = new ObjectOutputStream(_socket
			.getOutputStream());
		RegistrationResponse response = new RegistrationResponse(id,
			"registration fails '" + id + "'.", false);
		response.writeExternal(out);
		LOG.warning("registration fails for id: " + id);
	    }
	}
	return bit;
    }

    private String readClientName() throws IOException, ClassNotFoundException {
	RegistrationRequest registrationRequest = new RegistrationRequest();
	registrationRequest.readExternal(new ObjectInputStream(_socket
		.getInputStream()));
	return registrationRequest.getName();
    }

}
