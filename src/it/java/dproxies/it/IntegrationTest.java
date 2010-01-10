package dproxies.it;

import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.client.Client;
import dproxies.handler.Handler;
import dproxies.server.Server;
import dproxies.tuple.Tuples;
import dproxies.util.HandlerChainFactory;

public class IntegrationTest {

    public static interface One {
	int one();
    }

    public static class Two implements One {

	private final int _number;

	public Two(int number) {
	    _number = number;
	}

	public int one() {
	    return _number;
	}

    }

    private int _port = Ports.inc();

    @Test
    public void testRealWorld() throws Exception {
	assert One.class.isAssignableFrom(Two.class);
	HandlerChainFactory<One> factory = new HandlerChainFactory<One>(
		One.class, new Two(23));

	// server
	Handler<Tuples> serverDefaultHandlerChain = factory
		.getServerDefaultHandlerChain();
	Server server = new Server(_port, serverDefaultHandlerChain);
	server.start();

	Thread.sleep(500);

	// client
	Handler<Tuples> clientDefaultHandlerChain = factory
		.getClientDefaultHandlerChain("twentyThree");
	Client client = new Client("localhost", _port,
		clientDefaultHandlerChain);
	client.start();

	Thread.sleep(500);

	Object[] allProxies = server.getAllProxies();
	assert 1 == allProxies.length;
	One one = (One) allProxies[0];
	assert 23 == one.one();

    }
}
