package dproxies.handler;

import java.io.DataInput;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.HandlerPool;
import dproxies.handler.impl.BytePrefixWriter;
import dproxies.log.LogFactory;
import dproxies.tuple.TuplesWritable;

public class InfiniteReader extends TuplesReader {

    private static final Logger LOG = LogFactory
	    .getLogger(InfiniteReader.class);

    private final HandlerPool<TuplesWritable> _requestPool;
    private final HandlerPool<TuplesWritable> _responsePool;

    public InfiniteReader(HandlerPool<TuplesWritable> requestPool,
	    HandlerPool<TuplesWritable> responsePool, DataInput in) {
	super(in);
	_requestPool = requestPool;
	_responsePool = responsePool;
    }

    @Override
    protected boolean handlePreviousSuccess(TuplesWritable t) throws Exception {
	byte type = 0x00;
	while ((type = _in.readByte()) > -1) {
	    if (LOG.isLoggable(Level.FINE)) {
		LOG.fine("read byte: " + type);
	    }
	    // handle type
	    switch (type) {
	    case BytePrefixWriter.REQUEST:
		super.handlePreviousSuccess(t);
		if (LOG.isLoggable(Level.FINE)) {
		    LOG.fine("handle request: " + t);
		}
		_requestPool.handle(t);
		break;
	    case BytePrefixWriter.RESPONSE:
		super.handlePreviousSuccess(t);
		if (LOG.isLoggable(Level.FINE)) {
		    LOG.fine("handle response: " + t);
		}
		_responsePool.handle(t);
		break;
	    case BytePrefixWriter.SHUTDOWN:
		if (LOG.isLoggable(Level.INFO)) {
		    LOG.info("receive shutdown, close input.");
		}
		// TODO shutdown
	    default:
		return true;
	    }
	}
	return true;
    }

}
