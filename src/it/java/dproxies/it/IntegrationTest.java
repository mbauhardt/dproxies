package dproxies.it;

import org.testng.annotations.Test;

import dproxies.Ports;
import dproxies.client.Client;
import dproxies.handler.Handler;
import dproxies.server.Server;
import dproxies.tuple.Tuples;
import dproxies.util.HandlerChainFactory;

public class IntegrationTest {

    public static interface TestNumber {
	int getNumber();
    }

    public static class TestNumberImpl implements TestNumber {

	private final int _number;

	public TestNumberImpl(int number) {
	    _number = number;
	}

	public int getNumber() {
	    return _number;
	}

    }

    private int _port = Ports.inc();

    @Test
    public void testRealWorld() throws Exception {
	assert TestNumber.class.isAssignableFrom(TestNumberImpl.class);
	HandlerChainFactory<TestNumber> factory = new HandlerChainFactory<TestNumber>(
		TestNumber.class, new TestNumberImpl(23));

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
	TestNumber number = (TestNumber) allProxies[0];
	assert 23 == number.getNumber();

    }
}
