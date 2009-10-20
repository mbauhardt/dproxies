package dproxies.server;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.handler.impl.AbstractRegistrationHandler.RegistrationRequest;
import dproxies.handler.impl.AbstractRegistrationHandler.RegistrationResponse;
import dproxies.util.ClientRegistration;
import dproxies.util.Generator;

public class ServerTest {

    private int _handshakePort = Ports.inc();

    private int _registrationPort = Ports.inc();

    private Server _server;

    private ClientRegistration registration = new ClientRegistration();

    @BeforeTest(groups = { "handshake" })
    public void beforeHandshake() throws IOException {
	_server = new Server(_handshakePort, new ServerHandshakeHandler(
		new Generator()));
	_server.start();
    }

    @BeforeTest(groups = { "registration" })
    public void beforeRegistration() throws IOException {
	_server = new Server(_registrationPort, new ServerRegistrationHandler(
		registration));
	_server.start();
    }

    @AfterTest(groups = { "handshake", "serverRegistration" })
    public void down() throws IOException {
	_server.stop();
    }

    @Test(groups = { "handshake" })
    public void testSuccessfullyHandshake() throws Exception {
	Socket socket = new Socket("127.0.0.1", _handshakePort);

	// client handshake
	OutputStream outputStream = socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// send
	byte clientByte = 0x01;
	out.writeByte(clientByte);

	// receive
	byte testByte = in.readByte();
	assert clientByte == testByte;

	// send client success
	out.writeByte(Byte.MAX_VALUE);

	// server handshake
	// read
	byte serverByte = in.readByte();
	out.writeByte(serverByte);
	byte serverOk = in.readByte();
	assert serverOk == Byte.MAX_VALUE;
    }

    @Test(groups = { "handshake" })
    public void testClientHandshakeFails() throws Exception {
	Socket socket = new Socket("127.0.0.1", _handshakePort);

	// client handshake
	OutputStream outputStream = socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// send
	byte clientByte = 0x01;
	out.writeByte(clientByte);

	// receive
	byte testByte = in.readByte();
	assert clientByte == testByte;

	// send client failure
	out.writeByte(Byte.MIN_VALUE);
    }

    @Test(groups = { "handshake" })
    public void testhandshakeFails() throws Exception {
	Socket socket = new Socket("127.0.0.1", _handshakePort);

	// client handshake
	OutputStream outputStream = socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// send
	byte clientByte = 0x01;
	out.writeByte(clientByte);

	// receive
	byte testByte = in.readByte();
	assert clientByte == testByte;

	// send client success
	out.writeByte(Byte.MAX_VALUE);

	// server handshake
	// read
	byte serverByte = in.readByte();
	out.writeByte(serverByte + 1);
	byte serverOk = in.readByte();
	assert serverOk == Byte.MIN_VALUE;
    }

    @Test(groups = { "registration" })
    public void testSuccessfullyRegistration() throws Exception {
	Socket socket = new Socket("127.0.0.1", _registrationPort);
	writeRequest(socket, "testSuccessfullyRegistration");
	RegistrationResponse response = readResponse(socket);
	assert response.isRegistered();
	assert "testSuccessfullyRegistration".equals(response.getName());
	assert "registration successfully. welcome 'testSuccessfullyRegistration'."
		.equals(response.getMessage());
    }

    @Test(groups = { "registration" })
    public void testClientRegistrationFails() throws Exception {
	Socket socket1 = new Socket("127.0.0.1", _registrationPort);
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
    }

    private void writeRequest(Socket socket, String name) throws IOException {
	OutputStream outputStream = socket.getOutputStream();
	ObjectOutput out = new ObjectOutputStream(outputStream);
	RegistrationRequest registrationRequest = new AbstractRegistrationHandler.RegistrationRequest(
		name);
	registrationRequest.writeExternal(out);
    }

    private RegistrationResponse readResponse(Socket socket)
	    throws IOException, ClassNotFoundException {
	InputStream inputStream = socket.getInputStream();
	ObjectInput in = new ObjectInputStream(inputStream);
	RegistrationResponse response = new AbstractRegistrationHandler.RegistrationResponse();
	response.readExternal(in);
	return response;
    }
}
