package dproxies.client;

import java.io.DataInput;
import java.io.DataOutput;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;
import dproxies.util.Generator;

public class ClientHandshakeHandlerTest {

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
	Handler<Tuples> handler = new ClientHandshakeHandler(_generator);

	Mockito.when(_in.readInt()).thenReturn(3);
	Mockito.when(_in.readBoolean()).thenReturn(true);

	Tuples tuples = new Tuples();
	tuples.addTuple(new Tuple<Object>("in", _in));
	tuples.addTuple(new Tuple<Object>("out", _out));
	boolean handle = handler.handle(tuples);

	Mockito.verify(_in, Mockito.times(2)).readInt();
	Mockito.verify(_in, Mockito.times(1)).readBoolean();
	assert handle;
    }
}
