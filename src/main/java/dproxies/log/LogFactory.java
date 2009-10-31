package dproxies.log;

import java.net.URL;
import java.util.logging.Logger;

public class LogFactory {

    static {
	URL resource = LogFactory.class
		.getResource("/dproxies-logging.properties");
	if (resource != null) {
	    System.setProperty("java.util.logging.config.file", resource
		    .getFile());
	}
    }

    public static Logger getLogger(Class<?> clazz) {
	return Logger.getLogger(clazz.getName());
    }

}
