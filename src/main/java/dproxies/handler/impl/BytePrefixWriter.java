package dproxies.handler.impl;

import java.io.ObjectOutput;

import dproxies.handler.Handler;
import dproxies.handler.TuplesWriter;
import dproxies.tuple.TuplesWritable;

public abstract class BytePrefixWriter extends TuplesWriter {

    public static final byte REQUEST = 0x01;

    public static final byte RESPONSE = 0x02;

    public static final byte SHUTDOWN = 0x10;

    protected final byte _type;

    public BytePrefixWriter(Handler<TuplesWritable> prev, ObjectOutput out,
	    byte type) {
	super(prev, out);
	_type = type;
    }

    public BytePrefixWriter(ObjectOutput out, byte type) {
	super(out);
	_type = type;
    }

    @Override
    protected boolean handleSuccess(TuplesWritable t) throws Exception {
	_out.writeByte(_type);
	return super.handleSuccess(t);
    }

}
