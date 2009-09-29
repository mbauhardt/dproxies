package dproxies.util;

import java.util.Random;

public class Generator {

    private static final Random _random = new Random(System.currentTimeMillis());

    public byte generate() {
	byte[] bytes = new byte[1];
	_random.nextBytes(bytes);
	return bytes[0];
    }
}
