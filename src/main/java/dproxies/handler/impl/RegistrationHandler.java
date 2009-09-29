package dproxies.handler.impl;

import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import dproxies.handler.Handler;
import dproxies.handler.TuplesHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;
import dproxies.util.ClientRegistration;

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
	Tuple<Object> clientTuple = t.getTuple("clientName");
	Tuple<Object> socketTuple = t.getTuple("socket");
	Serializable id = (Serializable) clientTuple.getTupleValue();
	boolean ret = false;
	if (!_clientRegistration.isRegistered(id)) {
	    BlockingQueue<Socket> queue = _clientRegistration.register(id);
	    queue.add((Socket) socketTuple.getTupleValue());
	    ret = true;
	}
	return ret;
    }
}
