package dproxies.handler;

import java.io.ObjectInput;

import dproxies.tuple.TuplesWritable;

public class TuplesReader extends TuplesWritableHandler {

    protected final ObjectInput _in;

    public TuplesReader(ObjectInput in) {
	_in = in;
    }

    public TuplesReader(Handler<TuplesWritable> prev, ObjectInput in) {
	super(prev);
	_in = in;
    }

    @Override
    protected boolean handleSuccess(TuplesWritable t) throws Exception {
	t.readExternal(_in);
	return true;
    }

}
