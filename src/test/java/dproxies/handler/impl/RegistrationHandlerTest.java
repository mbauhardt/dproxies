package dproxies.handler.impl;

import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;
import dproxies.util.ClientRegistration;

public class RegistrationHandlerTest {

    @Mock
    private ClientRegistration _clientRegistration;

    @BeforeTest
    public void init() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegistration() throws Exception {
	Handler<Tuples> handler = new RegistrationHandler(_clientRegistration);
	Tuples t = new Tuples();
	t.addTuple(new Tuple<Object>("clientName", "foo"));
	t.addTuple(new Tuple<Object>("socket", new Socket()));
	Mockito.when(_clientRegistration.isRegistered("foo")).thenReturn(false);
	Mockito.when(_clientRegistration.register("foo")).thenReturn(
		new ArrayBlockingQueue<Socket>(1));
	handler.handle(t);
	Mockito.verify(_clientRegistration).isRegistered("foo");
	Mockito.verify(_clientRegistration).register("foo");

    }
}
