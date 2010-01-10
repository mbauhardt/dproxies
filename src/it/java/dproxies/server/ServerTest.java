package dproxies.server;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractHandshakeHandler;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.handler.impl.BytePrefixWriter;
import dproxies.handler.impl.DynamicProxyHandler;
import dproxies.handler.impl.IOHandler;
import dproxies.handler.impl.AbstractHandshakeHandler.Handshake;
import dproxies.handler.impl.AbstractRegistrationHandler.RegistrationRequest;
import dproxies.handler.impl.AbstractRegistrationHandler.RegistrationResponse;
import dproxies.handler.impl.InvocationMessageConsumer.InvocationMessage;
import dproxies.tuple.Tuple;
import dproxies.tuple.TuplesWritable;
import dproxies.util.ClientRegistration;
import dproxies.util.Generator;

public class ServerTest {

    private int _handshakePort = Ports.inc();

    private int _registrationPort = Ports.inc();

    private int _dynamicProxyPort = Ports.inc();

    private ClientRegistration registration = new ClientRegistration();

    @DataProvider(name = "handshake")
    public Object[][] createHandshakeDatas() {

	return new Object[][] { { _handshakePort,
		new ServerHandshakeHandler(new IOHandler(), new Generator()) } };
    }

    @DataProvider(name = "registration")
    public Object[][] createRegistrationDatas() {
	return new Object[][] { { _registrationPort,
		new ServerRegistrationHandler(new IOHandler(), registration) } };
    }

    @DataProvider(name = "dynamicProxy")
    public Object[][] createDynamicProxyDatas() {
	return new Object[][] { {
		_dynamicProxyPort,
		new DynamicProxyHandler<Serializable>(new IOHandler(),
			Serializable.class) } };
    }

    @Test(dataProvider = "handshake")
    public void testHandshake_success(Integer port, Handler handler)
	    throws Exception {
	Server server = new Server(port, handler);
	try {
	    server.start();
	    Thread.sleep(500);

	    Socket socket = new Socket("127.0.0.1", port);

	    OutputStream outputStream = socket.getOutputStream();
	    DataOutput out = new DataOutputStream(outputStream);
	    InputStream inputStream = socket.getInputStream();
	    DataInput in = new DataInputStream(inputStream);

	    Handshake handshake = new AbstractHandshakeHandler.Handshake();
	    handshake.read(in);
	    byte[] bytes = handshake.getBytes();
	    int index = handshake.getIndex();
	    int newIndex = index >= (bytes.length - 1) ? 0 : index + 1;
	    out.write(bytes[newIndex]);
	    assert in.readBoolean();

	} catch (Exception e) {
	    e.printStackTrace();
	    assert false;
	} finally {
	    server.stop();
	}
    }

    @Test(dataProvider = "handshake")
    public void testHandshake_fails(Integer port, Handler handler)
	    throws Exception {
	Server server = new Server(port, handler);
	try {
	    server.start();
	    Thread.sleep(500);

	    Socket socket = new Socket("127.0.0.1", port);

	    // client handshake
	    OutputStream outputStream = socket.getOutputStream();
	    DataOutput out = new DataOutputStream(outputStream);
	    InputStream inputStream = socket.getInputStream();
	    DataInput in = new DataInputStream(inputStream);

	    Handshake handshake = new AbstractHandshakeHandler.Handshake();
	    handshake.read(in);
	    byte[] bytes = handshake.getBytes();
	    int index = handshake.getIndex();
	    int newIndex = index >= (bytes.length - 1) ? 0 : index;
	    out.write(bytes[newIndex]);
	    assert !in.readBoolean();

	} catch (Exception e) {
	    e.printStackTrace();
	    assert false;
	} finally {
	    server.stop();
	}
    }

    @Test(dataProvider = "registration")
    public void testRegistration_success(Integer port, Handler handler)
	    throws Exception {
	Server server = new Server(port, handler);
	try {
	    server.start();
	    Thread.sleep(500);

	    Socket socket = new Socket("127.0.0.1", port);
	    writeRequest(socket, "testSuccessfullyRegistration");
	    RegistrationResponse response = readResponse(socket);
	    assert response.isRegistered();
	    assert "testSuccessfullyRegistration".equals(response.getName());
	    assert "registration successfully. welcome 'testSuccessfullyRegistration'."
		    .equals(response.getMessage());

	} catch (Exception e) {
	    e.printStackTrace();
	    assert false;
	} finally {
	    server.stop();
	}
    }

    @Test(dataProvider = "registration")
    public void testRegistration_fails(Integer port, Handler handler)
	    throws Exception {
	Server server = new Server(port, handler);
	try {
	    server.start();
	    Thread.sleep(500);

	    Socket socket1 = new Socket("127.0.0.1", port);
	    writeRequest(socket1, "testClientRegistrationFails");
	    RegistrationResponse response = readResponse(socket1);
	    assert response.isRegistered();
	    assert "testClientRegistrationFails".equals(response.getName());
	    assert "registration successfully. welcome 'testClientRegistrationFails'."
		    .equals(response.getMessage());

	    Socket socket2 = new Socket("127.0.0.1", _registrationPort);
	    writeRequest(socket2, "testClientRegistrationFails");
	    response = readResponse(socket2);
	    assert !response.isRegistered();
	    assert "testClientRegistrationFails".equals(response.getName());
	    assert "registration fails 'testClientRegistrationFails'."
		    .equals(response.getMessage());

	} catch (Exception e) {
	    e.printStackTrace();
	    assert false;
	} finally {
	    server.stop();
	}
    }

    @Test(dataProvider = "dynamicProxy")
    public void testDynamicProxyCall(Integer port, Handler handler)
	    throws Exception {
	Server server = new Server(port, handler);
	try {
	    server.start();
	    Thread.sleep(500);

	    Socket socket = new Socket("127.0.0.1", _dynamicProxyPort);

	    OutputStream outputStream = socket.getOutputStream();
	    DataOutput out = new DataOutputStream(outputStream);
	    InputStream inputStream = socket.getInputStream();
	    DataInput in = new DataInputStream(inputStream);

	    Thread.sleep(500);
	    System.out.println("ServerTest.testDynamicProxyCall()");
	    Object[] allProxies = server.getAllProxies();
	    System.out.println(allProxies.length);
	    assert allProxies.length == 1;
	    final Object proxy = allProxies[0];
	    assert proxy != null;

	    Runnable call = new Runnable() {
		public void run() {
		    String string = proxy.toString();
		    assert "foo".equals(string);
		}
	    };
	    Thread thread = new Thread(call);
	    thread.start();

	    Thread.sleep(400);
	    byte readByte = in.readByte();
	    assert BytePrefixWriter.REQUEST == readByte;
	    TuplesWritable tuplesWritable = new TuplesWritable();
	    tuplesWritable.read(in);

	    InvocationMessage message = (InvocationMessage) tuplesWritable
		    .getTuple("invocationMessage").getTupleValue();
	    assert 0 == message.getArguments().length;
	    assert message.getMethod().getName().equals("toString");

	    out.writeByte(BytePrefixWriter.RESPONSE);
	    TuplesWritable writable = new TuplesWritable();
	    writable.addTuple(new Tuple<Serializable>("id", tuplesWritable
		    .getTuple("id").getTupleValue()));
	    writable.addTuple(new Tuple<Serializable>("result", "foo"));
	    writable.write(out);
	    thread.join();

	} catch (Exception e) {
	    e.printStackTrace();
	    assert false;
	} finally {
	    server.stop();
	}
    }

    private void writeRequest(Socket socket, String name) throws IOException {
	OutputStream outputStream = socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	RegistrationRequest registrationRequest = new AbstractRegistrationHandler.RegistrationRequest(
		name);
	registrationRequest.write(out);
    }

    private RegistrationResponse readResponse(Socket socket)
	    throws IOException, ClassNotFoundException {
	InputStream inputStream = socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);
	RegistrationResponse response = new AbstractRegistrationHandler.RegistrationResponse();
	response.read(in);
	return response;
    }

}
