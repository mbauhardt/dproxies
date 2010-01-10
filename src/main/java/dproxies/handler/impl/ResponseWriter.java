package dproxies.handler.impl;

import java.io.DataOutput;

import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;

public class ResponseWriter extends BytePrefixWriter {

    public ResponseWriter(Handler<TuplesWritable> prev, DataOutput out) {
	super(prev, out, RESPONSE);
    }

    public ResponseWriter(DataOutput out) {
	super(out, RESPONSE);
    }
}
