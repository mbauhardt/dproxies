package dproxies.client;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.handler.Handler;
import dproxies.handler.impl.AbstractRegistrationHandler;
import dproxies.handler.impl.BytePrefixWriter;
import dproxies.handler.impl.IOHandler;
import dproxies.handler.impl.InvocationMessageConsumer;
import dproxies.handler.impl.AbstractRegistrationHandler.RegistrationRequest;
import dproxies.handler.impl.AbstractRegistrationHandler.RegistrationResponse;
import dproxies.handler.impl.InvocationMessageConsumer.InvocationMessage;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;
import dproxies.tuple.TuplesWritable;
import dproxies.util.Generator;

public class ClientTest {

    private int _testPort = Ports.inc();
    private ServerSocket _serverSocket;
    private Socket _socket;

    @BeforeTest(groups = { "handshake", "registration", "objectCall" })
    public void beforeHandshake() throws IOException {
	_serverSocket = new ServerSocket(_testPort);
    }

    @AfterTest(groups = { "handshake", "registration", "objectCall" })
    public void down() throws IOException {
	_serverSocket.close();
    }

    @Test(groups = { "handshake" })
    public void testSuccessfullyHandshake() throws Exception {
	// client handshake
	Handler<Tuples> handler = new IOHandler();
	handler = new ClientHandshakeHandler(handler, new Generator());
	Client client = new Client("127.0.0.1", _testPort, handler);
	client.start();
	_socket = _serverSocket.accept();

	OutputStream outputStream = _socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = _socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// receive
	byte clientByte = in.readByte();
	out.writeByte(clientByte);
	byte callbackByte = in.readByte();
	assert callbackByte == Byte.MAX_VALUE;

	// send
	byte serverByte = 0x01;
	out.writeByte(serverByte);
	callbackByte = in.readByte();
	assert serverByte == callbackByte;

	out.writeByte(Byte.MAX_VALUE);
    }

    @Test(groups = { "handshake" })
    public void testServerHandshakeFails() throws Exception {
	// client handshake
	Handler<Tuples> handler = new IOHandler();
	handler = new ClientHandshakeHandler(handler, new Generator());
	Client client = new Client("127.0.0.1", _testPort, handler);
	client.start();
	_socket = _serverSocket.accept();

	OutputStream outputStream = _socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = _socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// receive
	byte clientByte = in.readByte();
	out.writeByte(clientByte + 1);
	byte callbackByte = in.readByte();
	assert callbackByte == Byte.MIN_VALUE;
    }

    @Test(groups = { "handshake" })
    public void testClientHandshakeFails() throws Exception {
	// client handshake
	Handler<Tuples> handler = new IOHandler();
	handler = new ClientHandshakeHandler(handler, new Generator());
	Client client = new Client("127.0.0.1", _testPort, handler);
	client.start();
	_socket = _serverSocket.accept();

	OutputStream outputStream = _socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = _socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// receive
	byte clientByte = in.readByte();
	out.writeByte(clientByte);
	byte callbackByte = in.readByte();
	assert callbackByte == Byte.MAX_VALUE;

	// send
	byte serverByte = 0x01;
	out.writeByte(serverByte);
	callbackByte = in.readByte();
	assert serverByte == callbackByte;

	out.writeByte(Byte.MIN_VALUE);
    }

    @Test(groups = { "registration" })
    public void testSuccessfullyRegistration() throws Exception {
	Handler<Tuples> handler = new IOHandler();
	handler = new ClientRegistrationHandler(handler, "foo");

	Client client = new Client("127.0.0.1", _testPort, handler);
	client.start();
	_socket = _serverSocket.accept();

	OutputStream outputStream = _socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = _socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// read request
	RegistrationRequest request = new AbstractRegistrationHandler.RegistrationRequest();
	request.read(in);

	// send response
	RegistrationResponse response = new RegistrationResponse("foo",
		"allowed", true);
	response.write(out);

	assert request.getName().equals("foo");

    }

    @Test(groups = { "registration" })
    public void testRegistrationFails() throws Exception {
	Handler<Tuples> handler = new IOHandler();
	handler = new ClientRegistrationHandler(handler, "foo");
	Client client = new Client("127.0.0.1", _testPort, handler);
	client.start();
	_socket = _serverSocket.accept();

	OutputStream outputStream = _socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = _socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	// read request
	RegistrationRequest request = new AbstractRegistrationHandler.RegistrationRequest();
	request.read(in);

	// send response
	RegistrationResponse response = new RegistrationResponse("foo",
		"not allowed", false);
	response.write(out);

	assert request.getName().equals("foo");
    }

    @Test(groups = { "objectCall" })
    public void testObjectCall() throws Exception {
	Handler<Tuples> handler = new IOHandler();
	handler = new ObjectCallHandler<String>(handler, "foo");

	Client client = new Client("127.0.0.1", _testPort, handler);
	client.start();
	_socket = _serverSocket.accept();

	OutputStream outputStream = _socket.getOutputStream();
	DataOutput out = new DataOutputStream(outputStream);
	InputStream inputStream = _socket.getInputStream();
	DataInput in = new DataInputStream(inputStream);

	out.writeByte(BytePrefixWriter.REQUEST);
	InvocationMessage invocationMessage = new InvocationMessageConsumer.InvocationMessage(
		String.class, String.class.getMethod("toString"),
		new Object[] {});
	TuplesWritable requestTuple = new TuplesWritable();
	requestTuple.addTuple(new Tuple<Serializable>("id", "myId"));
	requestTuple.addTuple(new Tuple<Serializable>("invocationMessage",
		invocationMessage));
	requestTuple.write(out);

	byte readByte = in.readByte();
	assert readByte == BytePrefixWriter.RESPONSE;
	TuplesWritable tuplesWritable = new TuplesWritable();
	tuplesWritable.read(in);
	Serializable tupleValue = tuplesWritable.getTuple("id").getTupleValue();
	assert tupleValue.equals("myId");
	tupleValue = tuplesWritable.getTuple("result").getTupleValue();
	assert tupleValue.equals("foo");

    }

}
