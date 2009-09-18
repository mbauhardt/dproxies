package dproxies.handler.impl;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.handler.impl.ResponseHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.RegistrationBox;

public class ResponseHandlerTest {

    @Test
    public void testResult() throws Exception {
	RegistrationBox<Serializable> responseBox = new RegistrationBox<Serializable>();
	Integer id = new Integer(1);
	BlockingQueue<Serializable> queue = responseBox.register(id);
	Handler<TuplesWritable> handler = new ResponseHandler(responseBox);

	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("id", id));
	t.addTuple(new Tuple<Serializable>("result", "foo bar"));
	handler.handle(t);

	Object take = queue.poll();
	assert "foo bar".equals(take);
    }
}
