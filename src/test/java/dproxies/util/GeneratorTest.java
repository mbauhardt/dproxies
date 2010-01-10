package dproxies.util;

import org.testng.annotations.Test;

public class GeneratorTest {

    @Test
    public void testGenerate_byte() throws Exception {
	Generator generator = new Generator();
	byte byte_1 = generator.generateByte();
	byte byte_2 = generator.generateByte();
	assert byte_1 != byte_2;
    }

    @Test
    public void testGenerate_byteArray() throws Exception {
	Generator generator = new Generator();
	byte[] bytes1 = generator.generateByteArray(2);
	byte[] bytes2 = generator.generateByteArray(2);
	assert bytes1[0] != bytes2[0];
	assert bytes1[1] != bytes2[1];
    }

    @Test
    public void testGenerate_selectRandomByte() throws Exception {
	Generator generator = new Generator();
	byte[] bytes = generator.generateByteArray(2);
	int randomInt = generator.generateInt(bytes.length);
	assert randomInt < 2;
    }
}
