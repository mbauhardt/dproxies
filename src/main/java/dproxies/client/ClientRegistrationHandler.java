package dproxies.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.log.LogFactory;
import dproxies.util.AlreadyRegisteredException;

public class ClientRegistrationHandler extends AbstractRegistrationHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(ClientRegistrationHandler.class);

    private final String _name;

    public ClientRegistrationHandler(String name) {
	super(null);
	_name = name;
    }

    public ClientRegistrationHandler(Handler<Socket> prev, String name) {
	super(prev);
	_name = name;
    }

    @Override
    protected boolean doRegistration() throws IOException,
	    ClassNotFoundException, AlreadyRegisteredException {
	writeRequest(_socket, _name);
	RegistrationResponse response = readResponse(_socket);
	String message = response.getMessage();
	LOG.info(message);
	return response.isRegistered();
    }

    private void writeRequest(Socket socket, String name) throws IOException {
	LOG.info("send registration request");
	OutputStream outputStream = socket.getOutputStream();
	ObjectOutput out = new ObjectOutputStream(outputStream);
	RegistrationRequest registrationRequest = new AbstractRegistrationHandler.RegistrationRequest(
		name);
	registrationRequest.writeExternal(out);
    }

    private RegistrationResponse readResponse(Socket socket)
	    throws IOException, ClassNotFoundException {
	LOG.info("read registration response");
	InputStream inputStream = socket.getInputStream();
	ObjectInput in = new ObjectInputStream(inputStream);
	RegistrationResponse response = new AbstractRegistrationHandler.RegistrationResponse();
	response.readExternal(in);
	return response;
    }

}
