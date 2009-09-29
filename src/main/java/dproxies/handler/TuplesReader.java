package dproxies.handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import dproxies.tuple.TuplesWritable;

public class TuplesReader extends TuplesWritableHandler {

    private final ObjectInput _in;

    public TuplesReader(ObjectInput in) {
	_in = in;
    }

    public TuplesReader(Handler<TuplesWritable> prev, ObjectInput in) {
	super(prev);
	_in = in;
    }

    @Override
    protected boolean doHandle(TuplesWritable t) throws Exception {
	t.readExternal(_in);
	return true;
    }

    public static void main(String[] args) throws IOException {
	ServerSocket serverSocket = new ServerSocket(50060);
	Socket accept = serverSocket.accept();
	InputStream inputStream = accept.getInputStream();
	DataInputStream dataInputStream = new DataInputStream(inputStream);
	System.out.println(dataInputStream.readInt());
	
	
    }
}
