package dproxies.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractHandshakeHandler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public class ServerHandshakeHandler extends AbstractHandshakeHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(ServerHandshakeHandler.class);

    public ServerHandshakeHandler(Generator generator) {
	super(generator);
    }

    public ServerHandshakeHandler(Handler<Tuples> prev, Generator generator) {
	super(prev, generator);
    }

    @Override
    protected boolean handlePreviousSuccess(Tuples t) throws Exception {
	DataInput in = (DataInput) t.getTuple("in").getTupleValue();
	DataOutput out = (DataOutput) t.getTuple("out").getTupleValue();
	byte[] bytes = _generator.generateByteArray(23);
	int index = _generator.generateInt(bytes.length);
	int newIndex = index >= (bytes.length - 1) ? 0 : index;
	Handshake handshakeRequest = new Handshake(bytes, newIndex);
	handshakeRequest.write(out);
	byte b = in.readByte();
	boolean success = bytes[newIndex + 1] == b;
	out.writeBoolean(success);
	LOG.info("handshake success? " + success);
	return success;
    }

}
