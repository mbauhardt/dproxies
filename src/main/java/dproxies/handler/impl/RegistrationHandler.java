package dproxies.handler.impl;

import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import dproxies.handler.Handler;
import dproxies.handler.TuplesHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public class RegistrationHandler extends TuplesHandler {

    private final ClientRegistration _clientRegistration;

    public RegistrationHandler(ClientRegistration clientRegistration) {
	_clientRegistration = clientRegistration;
    }

    public RegistrationHandler(Handler<Tuples> prev,
	    ClientRegistration clientRegistration) {
	super(prev);
	_clientRegistration = clientRegistration;
    }

    @Override
    protected boolean doHandle(Tuples t) throws Exception {
	Tuple<Object> clientTuple = t.getTuple("client");
	Tuple<Object> socketTuple = t.getTuple("socket");

	Serializable id = (Serializable) clientTuple.getTupleValue();
	if (!_clientRegistration.isRegistered(id)) {
	    BlockingQueue<Socket> register = _clientRegistration.register(id);

	    _clientRegistration.addToBox(id, (Socket) socketTuple
		    .getTupleValue());
	}
	return false;
    }
}
