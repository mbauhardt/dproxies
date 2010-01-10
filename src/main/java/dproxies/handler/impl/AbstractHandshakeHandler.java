package dproxies.handler.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public abstract class AbstractHandshakeHandler extends SocketCloseHandler {

    private static final Logger LOG = LogFactory
	    .getLogger(AbstractHandshakeHandler.class);

    private final Generator _generator;

    protected DataInput _in;

    protected DataOutput _out;

    public AbstractHandshakeHandler(Generator generator) {
	_generator = generator;
    }

    public AbstractHandshakeHandler(Handler<Tuples> prev, Generator generator) {
	super(prev);
	_generator = generator;
    }

    public boolean handlePreviousSuccess(Tuples tuples) throws Exception {
	_in = (DataInput) tuples.getTuple("in").getTupleValue();
	_out = (DataOutput) tuples.getTuple("out").getTupleValue();
	boolean doHandshake = doHandshake();
	if (doHandshake) {
	    LOG.info("overall handshake success.");
	}
	return doHandshake;
    }

    protected abstract boolean doHandshake() throws IOException;

    protected boolean handleHandshake() throws IOException {
	byte aByte = _in.readByte();
	_out.write(aByte);
	aByte = _in.readByte();
	boolean verify = aByte == Byte.MAX_VALUE;
	LOG.log(verify ? Level.INFO : Level.WARNING,
		"handle handshake success? [" + verify + "]");
	return verify;
    }

    protected boolean startHandshake() throws IOException {
	byte aByte = _generator.generate();
	_out.write(aByte);
	byte callbackByte = _in.readByte();
	byte verifyByte = aByte == callbackByte ? Byte.MAX_VALUE
		: Byte.MIN_VALUE;
	_out.write(verifyByte);
	boolean verify = verifyByte == Byte.MAX_VALUE;
	LOG.log(verify ? Level.INFO : Level.WARNING,
		"start handshake success? [" + verify + "]");
	return verify;

    }
}
