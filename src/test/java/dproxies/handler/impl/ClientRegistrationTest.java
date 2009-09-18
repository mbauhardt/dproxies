package dproxies.handler.impl;

import java.net.Socket;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.handler.impl.ClientRegistration;

public class ClientRegistrationTest {

    @Mock
    private Socket _socket;

    @BeforeTest
    public void initMocks() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCloseConnection() throws Exception {
	ClientRegistration clientRegistration = new ClientRegistration();
	clientRegistration.register("foo");
	clientRegistration.addToBox("foo", _socket);
	clientRegistration.closeConnection("foo");
	Mockito.verify(_socket).close();
    }
}
