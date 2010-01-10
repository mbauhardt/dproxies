package dproxies.handler;

import java.io.DataInput;
import java.io.ObjectInput;

import dproxies.tuple.TuplesWritable;

public class TuplesReader extends TuplesWritableHandler {

    protected final DataInput _in;

    public TuplesReader(DataInput in) {
	_in = in;
    }

    public TuplesReader(Handler<TuplesWritable> prev, ObjectInput in) {
	super(prev);
	_in = in;
    }

    @Override
    protected boolean handlePreviousSuccess(TuplesWritable t) throws Exception {
	t.read(_in);
	return true;
    }

}
