package dproxies.handler.impl;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ProxyMethodCallResult;

public class ResponseHandlerTest {

    @Test
    public void testResult() throws Exception {
	ProxyMethodCallResult box = new ProxyMethodCallResult();
	Integer id = new Integer(1);
	BlockingQueue<Serializable> queue = box.register(id);
	Handler<TuplesWritable> handler = new ResponseHandler(box);

	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("id", id));
	t.addTuple(new Tuple<Serializable>("result", "foo bar"));
	handler.handle(t);

	Object take = queue.poll();
	assert "foo bar".equals(take);
    }
}
