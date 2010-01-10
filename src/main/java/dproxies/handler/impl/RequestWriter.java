package dproxies.handler.impl;

import java.io.DataOutput;

import dproxies.handler.Handler;
import dproxies.tuple.TuplesWritable;

public class RequestWriter extends BytePrefixWriter {

    public RequestWriter(Handler<TuplesWritable> prev, DataOutput out) {
	super(prev, out, REQUEST);
    }

    public RequestWriter(DataOutput out) {
	super(out, REQUEST);
    }

}
