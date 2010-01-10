package dproxies.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
	DataOutputStream out = new DataOutputStream(outPipe);

	DataInputStream in = new DataInputStream(inPipe);

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

    private void writeResponse(DataOutputStream out) throws IOException {
	out.write(BytePrefixWriter.RESPONSE);
	TuplesWritable testTuple = new TuplesWritable();
	testTuple.addTuple(new Tuple<Serializable>("foo", "bar"));
	testTuple.write(out);
    }

    private void writeRequest(DataOutputStream out) throws IOException {
	out.write(BytePrefixWriter.REQUEST);
	TuplesWritable testTuple = new TuplesWritable();
	testTuple.addTuple(new Tuple<Serializable>("foo", "bar"));
	testTuple.write(out);
    }
}
