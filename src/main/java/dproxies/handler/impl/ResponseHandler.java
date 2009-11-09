package dproxies.handler.impl;

import java.io.Serializable;

import dproxies.handler.Handler;
import dproxies.handler.TuplesWritableHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ProxyMethodCallResult;

public class ResponseHandler extends TuplesWritableHandler {

    private ProxyMethodCallResult _box = null;

    public ResponseHandler(ProxyMethodCallResult box) {
	_box = box;
    }

    public ResponseHandler(Handler<TuplesWritable> prev,
	    ProxyMethodCallResult box) {
	super(prev);
	_box = box;
    }

    @Override
    protected boolean handleSuccess(TuplesWritable t) throws Exception {
	Tuple<Serializable> tupleId = t.getTuple("id");
	Serializable id = tupleId.getTupleValue();
	Tuple<Serializable> resultTuple = t.getTuple("result");
	Serializable result = resultTuple.getTupleValue();
	_box.addToBox(id, result);
	return true;
    }

}
