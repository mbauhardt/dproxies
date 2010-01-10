package dproxies.handler;

import java.io.DataOutput;

import dproxies.tuple.TuplesWritable;

public class TuplesWriter extends TuplesWritableHandler {

    protected final DataOutput _out;

    public TuplesWriter(Handler<TuplesWritable> prev, DataOutput out) {
	super(prev);
	_out = out;
    }

    public TuplesWriter(DataOutput out) {
	_out = out;
    }

    @Override
    protected boolean handlePreviousSuccess(TuplesWritable t) throws Exception {
	t.write(_out);
	return true;
    }

}
