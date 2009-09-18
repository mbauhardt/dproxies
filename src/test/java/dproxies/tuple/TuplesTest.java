package dproxies.tuple;

import java.util.Collection;

import org.testng.annotations.Test;

import dproxies.tuple.AbstractTuples;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public class TuplesTest {

    @Test
    public void testGetTuple() throws Exception {
	AbstractTuples<Object> tuples = new Tuples();
	Tuple<Object> foo = new Tuple<Object>("foo", new Integer(1));
	Tuple<Object> bar = new Tuple<Object>("bar", "bar");
	tuples.addTuple(foo);
	tuples.addTuple(bar);

	Collection<Tuple<Object>> collection = tuples.getAll();
	assert collection.size() == 2;

	Tuple<?> tuple = tuples.getTuple("foo");
	assert "foo".equals(tuple.getTupleKey());
	assert new Integer(1).equals(tuple.getTupleValue());

	tuple = tuples.getTuple("bar");
	assert "bar".equals(tuple.getTupleKey());
	assert "bar".equals(tuple.getTupleValue());

    }
}
