package dproxies.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuples;
import dproxies.util.AlreadyRegisteredException;

public class ClientRegistrationHandler extends AbstractRegistrationHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(ClientRegistrationHandler.class);

    private final String _name;

    public ClientRegistrationHandler(String name) {
	super(null);
	_name = name;
    }

    public ClientRegistrationHandler(Handler<Tuples> prev, String name) {
	super(prev);
	_name = name;
    }

    @Override
    protected boolean doRegistration() throws IOException,
	    ClassNotFoundException, AlreadyRegisteredException {
	writeRequest(_out, _name);
	RegistrationResponse response = readResponse(_in);
	String message = response.getMessage();
	LOG.info(message);
	return response.isRegistered();
    }

    private void writeRequest(DataOutput out, String name) throws IOException {
	LOG.info("send registration request");
	RegistrationRequest registrationRequest = new AbstractRegistrationHandler.RegistrationRequest(
		name);
	registrationRequest.write(out);
    }

    private RegistrationResponse readResponse(DataInput in) throws IOException,
	    ClassNotFoundException {
	LOG.info("read registration response");
	RegistrationResponse response = new AbstractRegistrationHandler.RegistrationResponse();
	response.read(in);
	return response;
    }

}
