package dproxies.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.handler.TuplesReader;
import dproxies.handler.TuplesWriter;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class TuplesReaderWriterTest {

    @Test
    public void testReadWrite() throws Exception {
	File file = new File(System.getProperty("java.io.tmpdir"),
		TuplesReaderWriterTest.class.getName());
	ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
	TuplesWriter tuplesWriter = new TuplesWriter(out);
	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("foo", new Integer(1)));
	t.addTuple(new Tuple<Serializable>("bar", "bar"));
	tuplesWriter.doHandle(t);
	out.close();

	ObjectInput in = new ObjectInputStream(new FileInputStream(file));
	TuplesReader tuplesReader = new TuplesReader(in);
	TuplesWritable tuplesWritable2 = new TuplesWritable();
	tuplesReader.doHandle(tuplesWritable2);
	assert t.equals(tuplesWritable2);
	in.close();

    }
}
