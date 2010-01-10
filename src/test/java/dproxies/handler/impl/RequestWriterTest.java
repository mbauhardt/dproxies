package dproxies.handler.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.handler.impl.BytePrefixWriter;
import dproxies.handler.impl.RequestWriter;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class RequestWriterTest {

    @Test
    public void testWriteRequest() throws Exception {
	PipedInputStream pipedInputStream = new PipedInputStream();
	PipedOutputStream pipedOutputStream = new PipedOutputStream(
		pipedInputStream);
	DataOutputStream out = new DataOutputStream(pipedOutputStream);
	Handler<TuplesWritable> handler = new RequestWriter(out);
	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("foo", "bar"));
	handler.handle(t);

	DataInputStream objectInputStream = new DataInputStream(
		pipedInputStream);
	byte readByte = objectInputStream.readByte();
	assert readByte == BytePrefixWriter.REQUEST;
	TuplesWritable other = new TuplesWritable();
	other.read(objectInputStream);
	assert t.equals(other);

	out.close();
	objectInputStream.close();
    }

}
