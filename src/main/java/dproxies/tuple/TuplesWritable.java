package dproxies.tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.log.LogFactory;

public class TuplesWritable extends AbstractTuples<Serializable> implements
	Writable {

    private static final Logger LOG = LogFactory
	    .getLogger(TuplesWritable.class);

    public void read(DataInput in) throws IOException {
	reset();
	int howMany = in.readInt();
	for (int i = 0; i < howMany; i++) {
	    String key = in.readUTF();
	    Serializable serializable = readObject(in);
	    Tuple<Serializable> tuple = new Tuple<Serializable>(key,
		    serializable);
	    addTuple(tuple);
	}
    }

    public void write(DataOutput out) throws IOException {
	Collection<Tuple<Serializable>> tuples = getAll();
	out.writeInt(tuples.size());
	for (Tuple<Serializable> tuple : tuples) {
	    out.writeUTF(tuple._tupleKey);
	    writeObject(tuple._tupleValue, out);
	}
    }

    private void writeObject(Object object, DataOutput dataOutput)
	    throws IOException {
	ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
	ObjectOutputStream stream = new ObjectOutputStream(arrayOutputStream);
	stream.writeObject(object);
	byte[] bytes = arrayOutputStream.toByteArray();
	dataOutput.writeInt(bytes.length);
	dataOutput.write(bytes);
	stream.close();
    }

    private Serializable readObject(DataInput in) throws IOException {
	int length = in.readInt();
	byte[] bytes = new byte[length];
	in.readFully(bytes);
	ObjectInputStream stream = new ObjectInputStream(
		new ByteArrayInputStream(bytes));
	Serializable object;
	try {
	    object = (Serializable) stream.readObject();
	} catch (ClassNotFoundException e) {
	    LOG.log(Level.WARNING, "class not found: " + e.getMessage(), e);
	    throw new IOException("class not found: " + e.getMessage());
	} finally {
	    stream.close();
	}
	return object;
    }

}
