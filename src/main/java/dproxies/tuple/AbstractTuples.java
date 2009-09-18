package dproxies.tuple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTuples<T> {

    private Map<String, Tuple<T>> _tuples = new HashMap<String, Tuple<T>>();

    public void addTuple(Tuple<T> tuple) {
	_tuples.put(tuple.getTupleKey(), tuple);
    }

    public Tuple<T> getTuple(String key) {
	return _tuples.get(key);
    }

    public Collection<Tuple<T>> getAll() {
	return _tuples.values();
    }

    public void reset() {
	_tuples.clear();
    }

    @Override
    public int hashCode() {
	return _tuples.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	AbstractTuples<T> other = (AbstractTuples<T>) obj;
	return _tuples.equals(other._tuples);
    }

    @Override
    public String toString() {
	return _tuples.toString();
    }
}
