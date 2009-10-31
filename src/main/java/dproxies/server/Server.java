package dproxies.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dproxies.HandlerPool;
import dproxies.handler.Handler;
import dproxies.log.LogFactory;

public class Server implements Handler<Integer> {

    private static final Logger LOG = LogFactory.getLogger(Server.class);

    private final int _port;

    private HandlerPool<Integer> _serverPool;

    private ServerSocket _serverSocket;

    private boolean _closingEvent;

    private HandlerPool<Socket> _socketHandlerPool;

    public Server(int port, Handler<Socket> socketHandler) {
	_port = port;
	_serverPool = new HandlerPool<Integer>(1, this);
	_socketHandlerPool = new HandlerPool<Socket>(10, socketHandler);
    }

    public void start() throws IOException {
	LOG.info("start server on port: " + _port);
	_serverPool.handle(_port);
    }

    public void stop() throws IOException {
	LOG.info("shutdown server");
	_closingEvent = true;
	_serverSocket.close();
    }

    public boolean handle(Integer port) throws Exception {
	boolean bit = true;
	_serverSocket = new ServerSocket(port);
	Socket socket = null;
	try {
	    while ((socket = _serverSocket.accept()) != null) {
		LOG.info("accept new socket connection: " + socket);
		_socketHandlerPool.handle(socket);
	    }
	} catch (SocketException e) {
	    if (_closingEvent) {
		LOG.info("server is down");
	    } else {
		bit = false;
		LOG.log(Level.WARNING,
			"error while accepting new socket connections: ", e);
	    }
	}
	return bit;
    }

}
