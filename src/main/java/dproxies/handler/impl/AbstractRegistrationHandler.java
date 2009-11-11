package dproxies.handler.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.Socket;

import dproxies.handler.Handler;
import dproxies.util.AlreadyRegisteredException;

public abstract class AbstractRegistrationHandler extends SocketCloseHandler {

    public static class RegistrationRequest implements Externalizable {

	private String _name = "nobody";

	public RegistrationRequest() {
	}

	public RegistrationRequest(String name) {
	    _name = name;
	}

	public String getName() {
	    return _name;
	}

	public void readExternal(ObjectInput in) throws IOException,
		ClassNotFoundException {
	    _name = in.readUTF();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	    out.writeUTF(_name);
	    out.flush();
	}

    }

    public static class RegistrationResponse extends RegistrationRequest {

	private String _message = "";
	private boolean _registered;

	public RegistrationResponse() {

	}

	public RegistrationResponse(String name, String message, boolean status) {
	    super(name);
	    _message = message;
	    _registered = status;
	}

	public boolean isRegistered() {
	    return _registered;
	}

	public String getMessage() {
	    return _message;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
		ClassNotFoundException {
	    super.readExternal(in);
	    _message = in.readUTF();
	    _registered = in.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
	    super.writeExternal(out);
	    out.writeUTF(_message);
	    out.writeBoolean(_registered);
	    out.flush();
	}

    }

    protected Socket _socket;

    public AbstractRegistrationHandler(Handler<Socket> prev) {
	super(prev);
    }

    @Override
    protected final boolean handlePreviousSuccess(Socket socket) throws Exception {
	_socket = socket;
	return doRegistration();
    }

    protected abstract boolean doRegistration() throws IOException,
	    ClassNotFoundException, AlreadyRegisteredException;

}
