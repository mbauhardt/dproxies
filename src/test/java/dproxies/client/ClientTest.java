package dproxies.client;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.handler.impl.ClientHandshakeHandler;
import dproxies.util.Generator;

public class ClientTest {

    private int _testPort = Ports.inc();
    private ServerSocket _serverSocket;
    private Socket _socket;

    @BeforeTest(groups = { "handshake" })
    public void beforeHandshake() throws IOException {
	_serverSocket = new ServerSocket(_testPort);
    }

    @AfterTest(groups = { "handshake" })
    public void down() throws IOException {
	_serverSocket.close();
    }

    @Test(groups = { "handshake" })
    public void testSuccessfullyHandshake() throws Exception {
	// client handshake
	Client client = new Client("127.0.0.1", _testPort,
		new ClientHandshakeHandler(new Generator()));
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
	Client client = new Client("127.0.0.1", _testPort,
		new ClientHandshakeHandler(new Generator()));
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
	Client client = new Client("127.0.0.1", _testPort,
		new ClientHandshakeHandler(new Generator()));
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

}
