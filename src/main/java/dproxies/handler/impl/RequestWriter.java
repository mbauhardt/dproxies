package dproxies.handler.impl;

import java.io.ObjectOutput;

import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;

public class RequestWriter extends BytePrefixWriter {

    public RequestWriter(Handler<TuplesWritable> prev, ObjectOutput out) {
	super(prev, out, REQUEST);
    }

    public RequestWriter(ObjectOutput out) {
	super(out, REQUEST);
    }

}
