package dproxies.log;

import java.util.logging.Logger;

import org.testng.annotations.Test;

public class LogFactoryTest {

    @Test
    public void testFormatter() throws Exception {
	Class<?> clazz = LogFactoryTest.class;
	Logger logger = LogFactory.getLogger(clazz);
	String property = System.getProperty("java.util.logging.config.file");
	assert property.endsWith("dproxies-logging.properties");
	assert logger != null;
	logger.info("logger found");
    }
}
