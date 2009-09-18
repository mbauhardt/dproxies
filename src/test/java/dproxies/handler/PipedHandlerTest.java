package dproxies.handler;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.handler.PipedHandler;
import dproxies.handler.TuplesHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public class PipedHandlerTest {

    class SummandHandler extends TuplesHandler {

	@Override
	protected boolean doHandle(Tuples t) throws Exception {
	    t.addTuple(new Tuple<Object>("sum1", new Integer(1)));
	    t.addTuple(new Tuple<Object>("sum2", new Integer(2)));
	    return true;
	}
    }

    class SumHandler extends TuplesHandler {

	public SumHandler(Handler<Tuples> prev) {
	    super(prev);
	}

	@Override
	protected boolean doHandle(Tuples t) throws Exception {
	    Tuple<Object> sum1 = t.getTuple("sum1");
	    Tuple<Object> sum2 = t.getTuple("sum2");
	    Integer int1 = (Integer) sum1.getTupleValue();
	    Integer int2 = (Integer) sum2.getTupleValue();
	    t.addTuple(new Tuple<Object>("sum", int1 + int2));
	    return true;
	}

    }

    @Test
    public void testPipe() throws Exception {
	Tuples tuples = new Tuples();
	PipedHandler<Tuples> handler = new SummandHandler();
	handler = new SumHandler(handler);
	handler.handle(tuples);
	Tuple<Object> tuple = tuples.getTuple("sum");
	Integer sum = (Integer) tuple.getTupleValue();
	assert 3 == sum.intValue();
    }
}
