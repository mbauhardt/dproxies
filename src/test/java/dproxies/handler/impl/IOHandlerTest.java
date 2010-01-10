package dproxies.handler.impl;

import java.net.Socket;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public class IOHandlerTest {

    @Mock
    private Socket _socket;

    @BeforeTest
    public void setupMocks() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandle() throws Exception {
	Handler<Tuples> ioHandler = new IOHandler();
	Tuples t = new Tuples();
	t.addTuple(new Tuple<Object>("socket", _socket));
	ioHandler.handle(t);
	Mockito.verify(_socket).getInputStream();
	Mockito.verify(_socket).getOutputStream();
	assert t.getTuple("in") != null;
	assert t.getTuple("out") != null;
    }
}
