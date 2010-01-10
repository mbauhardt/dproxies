package dproxies.util;

import java.util.Random;

public class Generator {

    private static final Random _random = new Random(System.currentTimeMillis());

    public byte generateByte() {
	byte[] bytes = new byte[1];
	_random.nextBytes(bytes);
	return bytes[0];
    }

    public byte[] generateByteArray(int size) {
	byte[] bytes = new byte[size];
	for (int i = 0; i < size; i++) {
	    bytes[i] = generateByte();
	}
	return bytes;
    }

    public int generateInt(int length) {
	return _random.nextInt(length);
    }
}
