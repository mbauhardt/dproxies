package dproxies.handler.impl;

import java.io.ObjectOutput;

import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;

public class ResponseWriter extends BytePrefixWriter {

    public ResponseWriter(Handler<TuplesWritable> prev, ObjectOutput out) {
	super(prev, out, RESPONSE);
    }

    public ResponseWriter(ObjectOutput out) {
	super(out, RESPONSE);
    }
}
