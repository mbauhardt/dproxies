package dproxies.tuple;

public class Tuple<T> {

    protected String _tupleKey;
    protected T _tupleValue;

    public Tuple(String key, T value) {
	_tupleKey = key;
	_tupleValue = value;
    }

    public String getTupleKey() {
	return _tupleKey;
    }

    public T getTupleValue() {
	return _tupleValue;
    }

    public void setTupleKey(String tupleKey) {
	_tupleKey = tupleKey;
    }

    public void setTupleValue(T tupleValue) {
	_tupleValue = tupleValue;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((_tupleKey == null) ? 0 : _tupleKey.hashCode());
	result = prime * result
		+ ((_tupleValue == null) ? 0 : _tupleValue.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Tuple other = (Tuple) obj;
	if (_tupleKey == null) {
	    if (other._tupleKey != null)
		return false;
	} else if (!_tupleKey.equals(other._tupleKey))
	    return false;
	if (_tupleValue == null) {
	    if (other._tupleValue != null)
		return false;
	} else if (!_tupleValue.equals(other._tupleValue))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "(" + _tupleKey + "," + _tupleValue + ")";
    }
}
