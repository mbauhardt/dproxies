package dproxies.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.log.LogFactory;
import dproxies.tuple.Tuple;
import dproxies.tuple.Tuples;

public class Client implements Handler<SocketAddress> {

    private static final Logger LOG = LogFactory.getLogger(Client.class);

    private static final int TIMEOUT = 10000;
    private HandlerPool<SocketAddress> _serverPool;
    private HandlerPool<Tuples> _socketHandlerPool;
    private SocketAddress _socketAddress;

    private Socket _socket;

    public Client(String host, int port, Handler<Tuples> socketHandler) {
	_socketAddress = new InetSocketAddress(host, port);
	_serverPool = new HandlerPool<SocketAddress>(1, this);
	_socketHandlerPool = new HandlerPool<Tuples>(10, socketHandler);
    }

    public void start() {
	LOG.info("start client on address: " + _socketAddress);
	_serverPool.handle(_socketAddress);
    }

    public void stop() throws IOException {
	LOG.info("shutdown client");
	_socket.close();
    }

    public boolean handle(SocketAddress t) throws Exception {
	_socket = new Socket();
	LOG.info("connect to adress: " + t);
	_socket.connect(t, TIMEOUT);
	Tuples tuples = new Tuples();
	tuples.addTuple(new Tuple<Object>("socket", _socket));
	return _socketHandlerPool.handle(tuples);
    }

}
