package dproxies.handler.impl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.handler.impl.BytePrefixWriter;
import dproxies.handler.impl.ResponseWriter;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class ResponseWriterTest {

    @Test
    public void testWriteResponse() throws Exception {
	PipedInputStream pipedInputStream = new PipedInputStream();
	PipedOutputStream pipedOutputStream = new PipedOutputStream(
		pipedInputStream);
	ObjectOutputStream out = new ObjectOutputStream(pipedOutputStream);
	Handler<TuplesWritable> handler = new ResponseWriter(out);
	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("foo", "bar"));
	handler.handle(t);

	ObjectInputStream objectInputStream = new ObjectInputStream(
		pipedInputStream);
	byte readByte = objectInputStream.readByte();
	assert readByte == BytePrefixWriter.RESPONSE;
	TuplesWritable other = new TuplesWritable();
	other.readExternal(objectInputStream);
	assert t.equals(other);

	out.close();
	objectInputStream.close();
    }
}