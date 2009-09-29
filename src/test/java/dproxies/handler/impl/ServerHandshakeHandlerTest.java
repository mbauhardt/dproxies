package dproxies.handler.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.handler.Handler;
import dproxies.util.Generator;

public class ServerHandshakeHandlerTest {

    @Mock
    private Socket _socket;

    @Mock
    private InputStream _inputStream;

    @Mock
    private OutputStream _outputStream;

    @Mock
    private Generator _generator;

    @BeforeTest
    public void up() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandshake() throws Exception {
	Handler<Socket> handler = new ServerHandshakeHandler(_generator);

	Mockito.when(_socket.getInputStream()).thenReturn(_inputStream);
	Mockito.when(_socket.getOutputStream()).thenReturn(_outputStream);
	Mockito.when(_inputStream.read()).thenReturn(
		new Integer(Byte.MAX_VALUE));
	Mockito.when(_generator.generate()).thenReturn(Byte.MAX_VALUE);

	boolean handle = handler.handle(_socket);

	Mockito.verify(_inputStream, Mockito.times(3)).read();
	Mockito.verify(_outputStream, Mockito.times(3)).write(Byte.MAX_VALUE);
	assert handle;
    }
}
