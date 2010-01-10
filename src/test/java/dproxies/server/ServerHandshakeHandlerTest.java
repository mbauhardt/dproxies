package dproxies.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.Socket;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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

	byte[] bytes = new byte[23];
	for (int i = 0; i < bytes.length; i++) {
	    bytes[i] = (byte) i;
	}
	Mockito.when(_generator.generateByteArray(23)).thenReturn(bytes);
	Mockito.when(_generator.generateInt(bytes.length)).thenReturn(3);
	Mockito.when(_in.readInt()).thenReturn(4);
	Mockito.when(_in.readByte()).thenReturn(bytes[4]);

	Tuples tuples = new Tuples();
	tuples.addTuple(new Tuple<Object>("socket", _socket));
	tuples.addTuple(new Tuple<Object>("in", _in));
	tuples.addTuple(new Tuple<Object>("out", _out));
	boolean handle = handler.handle(tuples);

	Mockito.verify(_out, Mockito.times(1)).writeInt(bytes.length);
	Mockito.verify(_out, Mockito.times(1)).writeInt(3);
	Mockito.verify(_in, Mockito.times(1)).readByte();
	assert handle;
    }
}
