package dproxies.handler;

import dproxies.tuple.TuplesWritable;

public abstract class TuplesWritableHandler extends
	PipedHandler<TuplesWritable> {

    public TuplesWritableHandler() {
	super(null);
    }

    public TuplesWritableHandler(Handler<TuplesWritable> prev) {
	super(prev);
    }

}
