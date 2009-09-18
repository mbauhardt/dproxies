package dproxies.handler;

import java.io.ObjectOutput;

import dproxies.tuple.TuplesWritable;

public class TuplesWriter extends TuplesWritableHandler {

    protected final ObjectOutput _out;

    public TuplesWriter(Handler<TuplesWritable> prev, ObjectOutput out) {
	super(prev);
	_out = out;
    }

    public TuplesWriter(ObjectOutput out) {
	_out = out;
    }

    @Override
    protected boolean doHandle(TuplesWritable t) throws Exception {
	t.writeExternal(_out);
	_out.flush();
	return true;
    }

}
