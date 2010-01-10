package dproxies.handler.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class ResponseWriterTest {

    @Test
    public void testWriteResponse() throws Exception {
	PipedInputStream pipedInputStream = new PipedInputStream();
	PipedOutputStream pipedOutputStream = new PipedOutputStream(
		pipedInputStream);
	DataOutputStream out = new DataOutputStream(pipedOutputStream);
	Handler<TuplesWritable> handler = new ResponseWriter(out);
	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("foo", "bar"));
	handler.handle(t);

	DataInputStream objectInputStream = new DataInputStream(
		pipedInputStream);
	byte readByte = objectInputStream.readByte();
	assert readByte == BytePrefixWriter.RESPONSE;
	TuplesWritable other = new TuplesWritable();
	other.read(objectInputStream);
	assert t.equals(other);

	out.close();
	objectInputStream.close();
    }
}
