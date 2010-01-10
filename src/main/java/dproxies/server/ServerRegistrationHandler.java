package dproxies.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuples;
import dproxies.util.AlreadyRegisteredException;
import dproxies.util.ClientRegistration;

public class ServerRegistrationHandler extends AbstractRegistrationHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(ServerRegistrationHandler.class);

    private final ClientRegistration _clientRegistration;

    private final Object _mutex = new Object();

    public ServerRegistrationHandler(Handler<Tuples> prev,
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
		RegistrationResponse response = new RegistrationResponse(id,
			"registration successfully. welcome '" + id + "'.",
			true);
		response.write(_out);
		LOG.info("registration success for id: " + id);
	    } else {
		RegistrationResponse response = new RegistrationResponse(id,
			"registration fails '" + id + "'.", false);
		response.write(_out);
		LOG.warning("registration fails for id: " + id);
	    }
	}
	return bit;
    }

    private String readClientName() throws IOException, ClassNotFoundException {
	RegistrationRequest registrationRequest = new RegistrationRequest();
	registrationRequest.read(_in);
	return registrationRequest.getName();
    }

}
