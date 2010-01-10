package dproxies.tuple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.testng.annotations.Test;

public class TuplesWritableTest {

    public static class MyExt implements Externalizable {

	private String _key = "";
	private String _value = "";

	public MyExt() {
	}

	public MyExt(String key, String value) {
	    _key = key;
	    _value = value;
	}

	public void readExternal(ObjectInput in) throws IOException,
		ClassNotFoundException {
	    System.out.println("TuplesWritableTest.MyExt.readExternal()");
	    _key = in.readUTF();
	    _value = in.readUTF();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	    System.out.println("TuplesWritableTest.MyExt.writeExternal()");
	    out.writeUTF(_key);
	    out.writeUTF(_value);
	}

    }

    @Test
    public void testWriteAndRead() throws Exception {
	TuplesWritable tuplesWritable = new TuplesWritable();
	Tuple<Serializable> tuple1 = new Tuple<Serializable>("foo",
		new Integer(1));
	Tuple<Serializable> tuple2 = new Tuple<Serializable>("bar", "bar");
	Tuple<Serializable> tuple3 = new Tuple<Serializable>("foobar",
		new MyExt("one", "two"));
	tuplesWritable.addTuple(tuple1);
	tuplesWritable.addTuple(tuple2);
	tuplesWritable.addTuple(tuple3);

	File file = new File(System.getProperty("java.io.tmpdir"),
		"pipedHandlerTest.ser");
	DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
	tuplesWritable.write(out);
	out.close();

	DataInputStream in = new DataInputStream(new FileInputStream(file));
	tuplesWritable = new TuplesWritable();
	tuplesWritable.read(in);
	in.close();

	Tuple<Serializable> tuple = tuplesWritable.getTuple("foo");
	assert "foo".equals(tuple.getTupleKey());
	assert new Integer(1).equals(tuple.getTupleValue());

	tuple = tuplesWritable.getTuple("bar");
	assert "bar".equals(tuple.getTupleKey());
	assert "bar".equals(tuple.getTupleValue());

	tuple = tuplesWritable.getTuple("foobar");
	assert "foobar".equals(tuple.getTupleKey());
	assert tuple.getTupleValue() instanceof MyExt;
    }

}
