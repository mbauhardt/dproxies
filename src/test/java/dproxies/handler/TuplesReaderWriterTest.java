package dproxies.handler;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class TuplesReaderWriterTest {

    @Test
    public void testReadWrite() throws Exception {
	File file = new File(System.getProperty("java.io.tmpdir"),
		TuplesReaderWriterTest.class.getName());
	DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
	TuplesWriter tuplesWriter = new TuplesWriter(out);
	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("foo", new Integer(1)));
	t.addTuple(new Tuple<Serializable>("bar", "bar"));
	tuplesWriter.doHandle(t);
	out.close();

	DataInputStream in = new DataInputStream(new FileInputStream(file));
	TuplesReader tuplesReader = new TuplesReader(in);
	TuplesWritable tuplesWritable2 = new TuplesWritable();
	tuplesReader.doHandle(tuplesWritable2);
	assert t.equals(tuplesWritable2);
	in.close();

    }
}
