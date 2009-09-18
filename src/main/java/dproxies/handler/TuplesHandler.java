package dproxies.handler;

import dproxies.tuple.Tuples;

public abstract class TuplesHandler extends PipedHandler<Tuples> {

    public TuplesHandler() {
	super(null);
    }

    public TuplesHandler(Handler<Tuples> prev) {
	super(prev);
    }

}
