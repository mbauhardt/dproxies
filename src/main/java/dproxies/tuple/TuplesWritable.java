package dproxies.tuple;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;

public class TuplesWritable extends AbstractTuples<Serializable> implements
	Externalizable {

    public void readExternal(ObjectInput in) throws IOException,
	    ClassNotFoundException {
	reset();
	int howMany = in.readInt();
	for (int i = 0; i < howMany; i++) {
	    String key = in.readUTF();
	    Serializable serializable = (Serializable) in.readObject();
	    Tuple<Serializable> tuple = new Tuple<Serializable>(key,
		    serializable);
	    addTuple(tuple);
	}
    }

    public void writeExternal(ObjectOutput out) throws IOException {
	Collection<Tuple<Serializable>> tuples = getAll();
	out.writeInt(tuples.size());
	for (Tuple<Serializable> tuple : tuples) {
	    out.writeUTF(tuple._tupleKey);
	    out.writeObject(tuple._tupleValue);
	}
    }

}
