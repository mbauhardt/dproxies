package dproxies.handler.impl;

import java.io.Serializable;

import dproxies.handler.Handler;
import dproxies.handler.TuplesWritableHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.RegistrationBox;

public class ResponseHandler extends TuplesWritableHandler {

    private final RegistrationBox<Serializable> _responseBox;

    public ResponseHandler(RegistrationBox<Serializable> responseBox) {
	_responseBox = responseBox;
    }

    public ResponseHandler(Handler<TuplesWritable> prev,
	    RegistrationBox<Serializable> responseBox) {
	super(prev);
	_responseBox = responseBox;
    }

    @Override
    protected boolean handleSuccess(TuplesWritable t) throws Exception {
	Tuple<Serializable> tupleId = t.getTuple("id");
	Serializable id = tupleId.getTupleValue();
	Tuple<Serializable> resultTuple = t.getTuple("result");
	Serializable result = resultTuple.getTupleValue();
	_responseBox.addToBox(id, result);
	return true;
    }

}
