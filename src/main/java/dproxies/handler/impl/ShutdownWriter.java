package dproxies.handler.impl;

import java.io.DataOutput;
import java.io.ObjectOutput;

import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;

public class ShutdownWriter extends BytePrefixWriter {

    public ShutdownWriter(Handler<TuplesWritable> prev, DataOutput out) {
	super(prev, out, SHUTDOWN);
    }

    public ShutdownWriter(DataOutput out) {
	super(out, SHUTDOWN);
    }

    @Override
    protected boolean handlePreviousSuccess(TuplesWritable t) throws Exception {
	_out.writeByte(_type);
	return true;
    }

}
