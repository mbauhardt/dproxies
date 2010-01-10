package dproxies.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractHandshakeHandler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public class ClientHandshakeHandler extends AbstractHandshakeHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(ClientHandshakeHandler.class);

    public ClientHandshakeHandler(Generator generator) {
	super(generator);
    }

    public ClientHandshakeHandler(Handler<Tuples> prev, Generator generator) {
	super(prev, generator);
    }

    @Override
    protected boolean handlePreviousSuccess(Tuples t) throws Exception {
	DataInput in = (DataInput) t.getTuple("in").getTupleValue();
	DataOutput out = (DataOutput) t.getTuple("out").getTupleValue();
	Handshake handshake = new Handshake();
	handshake.read(in);
	byte[] bytes = handshake.getBytes();
	int index = handshake.getIndex();
	int newIndex = index >= (bytes.length - 1) ? 0 : index + 1;
	out.write(bytes[newIndex]);
	boolean success = in.readBoolean();
	LOG.info("handshake success? " + success);
	return success;
    }

}
