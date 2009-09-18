package dproxies.util;

public class AlreadyRegisteredException extends Exception {

    private static final long serialVersionUID = 7165329021533176223L;

    public AlreadyRegisteredException(String message) {
	super(message);
    }

}
