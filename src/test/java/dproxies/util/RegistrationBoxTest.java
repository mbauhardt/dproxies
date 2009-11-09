package dproxies.util;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.testng.annotations.Test;

import dproxies.util.AlreadyRegisteredException;
import dproxies.util.NotRegisteredException;
import dproxies.util.Box;

public class RegistrationBoxTest {

    class ResponseRunnable implements Runnable {

	private final Box<Object> _responseBox;

	private boolean _exceptionOccurs = false;

	public ResponseRunnable(Box<Object> responseBox) {
	    _responseBox = responseBox;
	}

	public void run() {
	    try {
		Thread.sleep(1000);
		Object object = "foo bar";
		Serializable id = new Integer(1);
		_responseBox.addToBox(id, object);
	    } catch (Exception e) {
		_exceptionOccurs = true;
	    }
	}

    }

    class ResponseAgainRunnable implements Runnable {

	private final Box<Object> _responseBox;

	private boolean _exceptionOccurs = false;

	public ResponseAgainRunnable(Box<Object> responseBox) {
	    _responseBox = responseBox;
	}

	public void run() {
	    try {
		Thread.sleep(1000);
		Object object = "foo bar";
		Serializable id = new Integer(1);
		_responseBox.addToBox(id, object);
		_responseBox.addToBox(id, object);
	    } catch (Exception e) {
		_exceptionOccurs = true;
	    }
	}

    }

    @Test
    public void testRegister() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	BlockingQueue<Object> queue = responseBox.register(id);
	ResponseRunnable responseRunnable = new ResponseRunnable(responseBox);
	new Thread(responseRunnable).start();
	Object take = queue.take();
	assert "foo bar".equals(take);
	assert !responseRunnable._exceptionOccurs;
    }

    @Test
    public void testNotRegistered() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	ResponseRunnable responseRunnable = new ResponseRunnable(responseBox);
	new Thread(responseRunnable).start();
	Thread.sleep(1500);
	assert responseRunnable._exceptionOccurs;
    }

    @Test
    public void testRegisterAgain() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	responseBox.register(id);
	try {
	    responseBox.register(id);
	    assert false;
	} catch (AlreadyRegisteredException e) {

	}
    }

    @Test
    public void testResponseAgain() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	responseBox.register(id);
	ResponseAgainRunnable responseAgainRunnable = new ResponseAgainRunnable(
		responseBox);
	new Thread(responseAgainRunnable).start();
	Thread.sleep(1500);
	assert responseAgainRunnable._exceptionOccurs;
    }

    @Test
    public void testDeregister() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	responseBox.register(id);
	BlockingQueue<Object> queue = responseBox.deregister(id);
	assert queue != null;
    }

    @Test
    public void testDeregisterAgain() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	responseBox.register(id);
	responseBox.deregister(id);
	try {
	    responseBox.deregister(id);
	    assert false;
	} catch (NotRegisteredException e) {
	}
    }

    @Test
    public void testRegisterAndDeregister() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	responseBox.register(id);
	BlockingQueue<Object> queue = responseBox.deregister(id);
	assert queue != null;
	responseBox.register(id);
	queue = responseBox.deregister(id);
	assert queue != null;
    }

    @Test
    public void testIsRegistered() throws Exception {
	Box<Object> responseBox = new Box<Object>();
	Serializable id = new Integer(1);
	responseBox.register(id);
	assert responseBox.isRegistered(id);
    }

}
