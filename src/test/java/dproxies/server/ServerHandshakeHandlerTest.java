package dproxies.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.handler.impl.IOHandler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public class ServerHandshakeHandlerTest {

    @Mock
    private Socket _socket;

    @Mock
    private DataInput _in;

    @Mock
    private DataOutput _out;

    @Mock
    private Generator _generator;

    @BeforeTest
    public void up() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandshake() throws Exception {
	ServerHandshakeHandler handler = new ServerHandshakeHandler(_generator);

	Mockito.when(_in.readByte()).thenReturn(Byte.MAX_VALUE);
	Mockito.when(_generator.generate()).thenReturn(Byte.MAX_VALUE);

	Tuples tuples = new Tuples();
	tuples.addTuple(new Tuple<Object>("socket", _socket));
	tuples.addTuple(new Tuple<Object>("in", _in));
	tuples.addTuple(new Tuple<Object>("out", _out));
	boolean handle = handler.handle(tuples);

	Mockito.verify(_in, Mockito.times(3)).readByte();
	Mockito.verify(_out, Mockito.times(3)).write(Byte.MAX_VALUE);
	assert handle;
    }
}
