package dproxies.handler.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import dproxies.handler.Handler;
import dproxies.tuple.Tuples;
import dproxies.tuple.Writable;
import dproxies.util.Generator;

public abstract class AbstractHandshakeHandler extends SocketCloseHandler {

    public static class Handshake implements Writable {

	private byte[] _bytes;
	private int _index;

	public Handshake(byte[] bytes, int index) {
	    _bytes = bytes;
	    _index = index;
	}

	public Handshake() {
	}

	public byte[] getBytes() {
	    return _bytes;
	}

	public int getIndex() {
	    return _index;
	}

	public void read(DataInput in) throws IOException {
	    int length = in.readInt();
	    _bytes = new byte[length];
	    in.readFully(_bytes);
	    _index = in.readInt();
	}

	public void write(DataOutput out) throws IOException {
	    out.writeInt(_bytes.length);
	    out.write(_bytes);
	    out.writeInt(_index);
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + Arrays.hashCode(_bytes);
	    result = prime * result + _index;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    Handshake other = (Handshake) obj;
	    if (!Arrays.equals(_bytes, other._bytes))
		return false;
	    if (_index != other._index)
		return false;
	    return true;
	}

    }

    protected final Generator _generator;

    protected DataInput _in;

    protected DataOutput _out;

    public AbstractHandshakeHandler(Generator generator) {
	_generator = generator;
    }

    public AbstractHandshakeHandler(Handler<Tuples> prev, Generator generator) {
	super(prev);
	_generator = generator;
    }

}
