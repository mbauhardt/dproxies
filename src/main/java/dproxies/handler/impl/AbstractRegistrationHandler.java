package dproxies.handler.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.Socket;

import dproxies.handler.Handler;
import dproxies.tuple.Tuples;
import dproxies.tuple.Writable;
import dproxies.util.AlreadyRegisteredException;

public abstract class AbstractRegistrationHandler extends SocketCloseHandler {

    public static class RegistrationRequest implements Writable {

	private String _name = "nobody";

	public RegistrationRequest() {
	}

	public RegistrationRequest(String name) {
	    _name = name;
	}

	public String getName() {
	    return _name;
	}

	public void read(DataInput in) throws IOException {
	    _name = in.readUTF();
	}

	public void write(DataOutput out) throws IOException {
	    out.writeUTF(_name);
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
	public void read(DataInput in) throws IOException {
	    super.read(in);
	    _message = in.readUTF();
	    _registered = in.readBoolean();
	}

	@Override
	public void write(DataOutput out) throws IOException {
	    super.write(out);
	    out.writeUTF(_message);
	    out.writeBoolean(_registered);
	}

    }

    protected DataInput _in;
    protected DataOutput _out;
    protected Socket _socket;

    public AbstractRegistrationHandler(Handler<Tuples> prev) {
	super(prev);
    }

    @Override
    protected final boolean handlePreviousSuccess(Tuples tuples)
	    throws Exception {
	_socket = (Socket) tuples.getTuple("socket").getTupleValue();
	_in = (DataInput) tuples.getTuple("in").getTupleValue();
	_out = (DataOutput) tuples.getTuple("out").getTupleValue();
	return doRegistration();
    }

    protected abstract boolean doRegistration() throws IOException,
	    ClassNotFoundException, AlreadyRegisteredException;

}
