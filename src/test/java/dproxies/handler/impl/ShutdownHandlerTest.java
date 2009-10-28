package dproxies.handler.impl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class ShutdownHandlerTest {

    @Test
    public void testWriteShutdown() throws Exception {
	PipedInputStream pipedInputStream = new PipedInputStream();
	PipedOutputStream pipedOutputStream = new PipedOutputStream(
		pipedInputStream);
	ObjectOutputStream out = new ObjectOutputStream(pipedOutputStream);
	Handler<TuplesWritable> handler = new ShutdownWriter(out);
	TuplesWritable t = new TuplesWritable();
	t.addTuple(new Tuple<Serializable>("foo", "bar"));
	handler.handle(t);

	ObjectInputStream objectInputStream = new ObjectInputStream(
		pipedInputStream);
	assert objectInputStream.available() == 1;
	byte readByte = objectInputStream.readByte();
	assert readByte == BytePrefixWriter.SHUTDOWN;
	assert objectInputStream.available() == 0;
    }

}
