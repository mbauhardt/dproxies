package dproxies.handler.impl;

import java.io.ObjectOutput;

import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;

public class ShutdownWriter extends BytePrefixWriter {

    public ShutdownWriter(Handler<TuplesWritable> prev, ObjectOutput out) {
	super(prev, out, SHUTDOWN);
    }

    public ShutdownWriter(ObjectOutput out) {
	super(out, SHUTDOWN);
    }

    @Override
    protected boolean handlePreviousSuccess(TuplesWritable t) throws Exception {
	_out.writeByte(_type);
	_out.close();
	return true;
    }

}
