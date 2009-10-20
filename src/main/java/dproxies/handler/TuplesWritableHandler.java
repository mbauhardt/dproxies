package dproxies.handler;

import java.io.IOException;

import dproxies.tuple.TuplesWritable;

public abstract class TuplesWritableHandler extends
	PipedHandler<TuplesWritable> {

    public TuplesWritableHandler() {
	super(null);
    }

    public TuplesWritableHandler(Handler<TuplesWritable> prev) {
	super(prev);
    }

    @Override
    protected void handleFailure(TuplesWritable t) throws IOException {
	// do nothing
    }
}
