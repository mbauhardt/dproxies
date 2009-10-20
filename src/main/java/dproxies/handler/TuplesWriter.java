package dproxies.handler;

import java.io.IOException;
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
    protected boolean handleSuccess(TuplesWritable t) throws Exception {
	t.writeExternal(_out);
	_out.flush();
	return true;
    }
    
    @Override
    protected void handleFailure(TuplesWritable t) throws IOException {
        super.handleFailure(t);
        _out.close();
    }

}
