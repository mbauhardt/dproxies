package dproxies.handler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.handler.Handler;
import dproxies.util.Generator;

public abstract class AbstractHandshakeHandler implements Handler<Socket> {

    private static final Logger LOG = Logger
	    .getLogger(AbstractHandshakeHandler.class.getName());

    private final Generator _generator;

    protected InputStream _in;

    protected OutputStream _out;

    public AbstractHandshakeHandler(Generator generator) {
	_generator = generator;
    }

    public boolean handle(Socket socket) throws Exception {
	_in = socket.getInputStream();
	_out = socket.getOutputStream();
	boolean doHandshake = doHandshake();
	if (!doHandshake) {
	    LOG.warning("overall handshake fails, close the socket.");
	    socket.close();
	} else {
	    LOG.info("overall handshake success.");
	}
	return doHandshake;
    }

    protected abstract boolean doHandshake() throws IOException;

    protected boolean handleHandshake() throws IOException {
	byte aByte = (byte) _in.read();
	_out.write(aByte);
	aByte = (byte) _in.read();
	boolean verify = aByte == Byte.MAX_VALUE;
	LOG.log(verify ? Level.INFO : Level.WARNING,
		"handle handshake success? [" + verify + "]");
	return verify;
    }

    protected boolean startHandshake() throws IOException {
	byte aByte = _generator.generate();
	_out.write(aByte);
	byte callbackByte = (byte) _in.read();
	byte verifyByte = aByte == callbackByte ? Byte.MAX_VALUE
		: Byte.MIN_VALUE;
	_out.write(verifyByte);
	boolean verify = verifyByte == Byte.MAX_VALUE;
	LOG.log(verify ? Level.INFO : Level.WARNING,
		"start handshake success? [" + verify + "]");
	return verify;

    }
}
