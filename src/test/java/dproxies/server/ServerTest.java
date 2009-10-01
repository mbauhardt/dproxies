package dproxies.server;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.handler.impl.ServerHandshakeHandler;
import dproxies.util.Generator;

public class ServerTest {

    private int _testPort = Ports.inc();

    private Server _server;

    @BeforeTest(groups = { "serverHandshake" })
    public void beforeHandshake() throws IOException {
	_server = new Server(_testPort, new ServerHandshakeHandler(
		new Generator()));
	_server.start();
    }

    @AfterTest(groups = { "serverHandshake" })
    public void down() throws IOException {
	_server.stop();
    }

    @Test(groups = { "serverHandshake" })
    public void testSuccessfullyHandshake() throws Exception {
	Socket socket = new Socket("127.0.0.1", _testPort);

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

    @Test(groups = { "serverHandshake" })
    public void testClientHandshakeFails() throws Exception {
	Socket socket = new Socket("127.0.0.1", _testPort);

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

    @Test(groups = { "serverHandshake" })
    public void testServerHandshakeFails() throws Exception {
	Socket socket = new Socket("127.0.0.1", _testPort);

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
}
