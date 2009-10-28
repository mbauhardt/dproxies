package dproxies.handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.HandlerPool;
import dproxies.handler.impl.BytePrefixWriter;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;

public class InfiniteReaderTest {

    @Mock
    private HandlerPool<TuplesWritable> _responsePool;

    @Mock
    private HandlerPool<TuplesWritable> _requestPool;

    @BeforeTest
    public void init() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInfiniteRead() throws Exception {

	PipedInputStream inPipe = new PipedInputStream();
	PipedOutputStream outPipe = new PipedOutputStream(inPipe);
	ObjectOutputStream out = new ObjectOutputStream(outPipe);

	ObjectInputStream in = new ObjectInputStream(inPipe);

	writeRequest(out);
	writeRequest(out);
	writeResponse(out);
	writeRequest(out);
	out.writeByte(BytePrefixWriter.SHUTDOWN);
	out.flush();

	InfiniteReader infiniteReader = new InfiniteReader(_requestPool,
		_responsePool, in);
	TuplesWritable t = new TuplesWritable();
	infiniteReader.handle(t);

	Mockito.verify(_requestPool, Mockito.times(3)).handle(t);
	Mockito.verify(_responsePool, Mockito.times(1)).handle(t);

    }

    private void writeResponse(ObjectOutputStream out) throws IOException {
	out.write(BytePrefixWriter.RESPONSE);
	TuplesWritable testTuple = new TuplesWritable();
	testTuple.addTuple(new Tuple<Serializable>("foo", "bar"));
	testTuple.writeExternal(out);
    }

    private void writeRequest(ObjectOutputStream out) throws IOException {
	out.write(BytePrefixWriter.REQUEST);
	TuplesWritable testTuple = new TuplesWritable();
	testTuple.addTuple(new Tuple<Serializable>("foo", "bar"));
	testTuple.writeExternal(out);
    }
}
