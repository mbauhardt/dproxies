package dproxies.util;

import org.testng.annotations.Test;

public class GeneratorTest {

    @Test
    public void testGenerate() throws Exception {
	Generator generator = new Generator();
	byte byte_1 = generator.generate();
	byte byte_2 = generator.generate();
	assert byte_1 != byte_2;
    }
}
