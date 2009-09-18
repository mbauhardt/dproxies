package dproxies.handler.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import dproxies.util.NotRegisteredException;
import dproxies.util.RegistrationBox;

public class ClientRegistration extends RegistrationBox<Socket> {

    public void closeConnection(Serializable id) throws NotRegisteredException,
	    IOException {
	BlockingQueue<Socket> queue = deregister(id);
	Socket socket = queue.poll();
	socket.close();
    }
}
